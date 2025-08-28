package exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Order(-2) // Prioridad alta para manejar excepciones antes que otros handlers
@RequiredArgsConstructor
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        if (ex instanceof ConstraintViolationException) {
            return handleConstraintViolation(exchange, (ConstraintViolationException) ex);
        }

        if (ex instanceof WebExchangeBindException) {
            return handleValidationErrors(exchange, (WebExchangeBindException) ex);
        }

        if (ex instanceof ServerWebInputException) {
            return handleInvalidFormat(exchange, (ServerWebInputException) ex);
        }

        if (ex instanceof IllegalArgumentException) {
            return handleIllegalArgument(exchange, (IllegalArgumentException) ex);
        }

        // No manejamos otras excepciones, las dejamos pasar al siguiente handler
        return Mono.error(ex);
    }

    private Mono<Void> handleConstraintViolation(ServerWebExchange exchange, ConstraintViolationException ex) {
        log.error("Error de validación de constraints: {}", ex.getMessage());

        List<Map<String, String>> details = ex.getConstraintViolations().stream()
                .map(violation -> Map.of(
                        "field", violation.getPropertyPath().toString(),
                        "message", violation.getMessage()
                ))
                .collect(Collectors.toList());

        Map<String, Object> errorResponse = Map.of(
                "error", "Validación fallida",
                "details", details
        );

        return writeErrorResponse(exchange, HttpStatus.BAD_REQUEST, errorResponse);
    }

    private Mono<Void> handleValidationErrors(ServerWebExchange exchange, WebExchangeBindException ex) {
        log.error("Error de validación de binding: {}", ex.getMessage());

        List<Map<String, String>> details = ex.getFieldErrors().stream()
                .map(err -> Map.of(
                        "field", err.getField(),
                        "message", err.getDefaultMessage() != null ? err.getDefaultMessage() : "Error de validación"
                ))
                .collect(Collectors.toList());

        Map<String, Object> errorResponse = Map.of(
                "error", "Validación fallida",
                "details", details
        );

        return writeErrorResponse(exchange, HttpStatus.BAD_REQUEST, errorResponse);
    }

    private Mono<Void> handleInvalidFormat(ServerWebExchange exchange, ServerWebInputException ex) {
        log.error("Error de formato en input", ex);

        String fieldName = "campo";
        String message = "Formato inválido";

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            // Jackson trae la referencia al campo
            if (!ife.getPath().isEmpty()) {
                fieldName = ife.getPath().get(0).getFieldName();
            }
            message = String.format("El valor '%s' no es válido para el campo %s. Tipo esperado: %s",
                    ife.getValue(),
                    fieldName,
                    ife.getTargetType().getSimpleName());
        }

        Map<String, Object> errorResponse = Map.of(
                "error", "Formato inválido en request",
                "details", List.of(Map.of(
                        "message", message
                ))
        );

        return writeErrorResponse(exchange, HttpStatus.BAD_REQUEST, errorResponse);
    }


    private Mono<Void> handleIllegalArgument(ServerWebExchange exchange, IllegalArgumentException ex) {
        log.error("Error de argumento ilegal: {}", ex.getMessage());

        Map<String, Object> errorResponse = Map.of(
                "error", "Validación de dominio fallida",
                "details", List.of(Map.of(
                        "field", "general",
                        "message", ex.getMessage() != null ? ex.getMessage() : "Error de validación de dominio"
                ))
        );

        return writeErrorResponse(exchange, HttpStatus.BAD_REQUEST, errorResponse);
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, Map<String, Object> errorResponse) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            DataBuffer buffer = exchange.getResponse().bufferFactory()
                    .wrap(jsonResponse.getBytes(StandardCharsets.UTF_8));

            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Error al serializar respuesta de error", e);
            return exchange.getResponse().setComplete();
        }
    }
}