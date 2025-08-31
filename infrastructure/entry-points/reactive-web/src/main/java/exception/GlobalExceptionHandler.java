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
@Order(-2)
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

        return Mono.error(ex);
    }

    private Mono<Void> handleConstraintViolation(ServerWebExchange exchange, ConstraintViolationException ex) {
        log.error("Constraint validation error: {}", ex.getMessage());

        List<Map<String, String>> details = ex.getConstraintViolations().stream()
                .map(violation -> Map.of(
                        "field", violation.getPropertyPath().toString(),
                        "message", violation.getMessage()
                ))
                .collect(Collectors.toList());

        Map<String, Object> errorResponse = Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Validation failed",
                "details", details
        );

        return writeErrorResponse(exchange, HttpStatus.BAD_REQUEST, errorResponse);
    }

    private Mono<Void> handleValidationErrors(ServerWebExchange exchange, WebExchangeBindException ex) {
        log.error("Binding validation error: {}", ex.getMessage());

        List<Map<String, String>> details = ex.getFieldErrors().stream()
                .map(err -> Map.of(
                        "field", err.getField(),
                        "message", err.getDefaultMessage() != null ? err.getDefaultMessage() : "Validation error"
                ))
                .collect(Collectors.toList());

        Map<String, Object> errorResponse = Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Validation failed",
                "details", details
        );

        return writeErrorResponse(exchange, HttpStatus.BAD_REQUEST, errorResponse);
    }

    private Mono<Void> handleInvalidFormat(ServerWebExchange exchange, ServerWebInputException ex) {
        log.error("Invalid input format", ex);

        String fieldName = "field";
        String message = "Invalid format";

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            if (!ife.getPath().isEmpty()) {
                fieldName = ife.getPath().get(0).getFieldName();
            }
            message = String.format("Value '%s' is not valid for field %s. Expected type: %s",
                    ife.getValue(),
                    fieldName,
                    ife.getTargetType().getSimpleName());
        }

        Map<String, Object> errorResponse = Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Invalid request format",
                "details", List.of(Map.of("message", message))
        );

        return writeErrorResponse(exchange, HttpStatus.BAD_REQUEST, errorResponse);
    }

    private Mono<Void> handleIllegalArgument(ServerWebExchange exchange, IllegalArgumentException ex) {
        log.error("Illegal argument error: {}", ex.getMessage());

        String field = "general";
        String message = ex.getMessage();

        if (message != null && message.contains(":")) {
            String[] parts = message.split(":", 2);
            field = parts[0].trim();
            message = parts[1].trim();
        }

        Map<String, Object> errorResponse = Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Domain validation failed",
                "details", List.of(Map.of(
                        "field", field,
                        "message", message
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
            log.error("Error serializing error response", e);
            return exchange.getResponse().setComplete();
        }
    }
}
