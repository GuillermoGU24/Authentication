package co.com.crediya.api.security;

import co.com.crediya.model.auth.AuthUser;
import co.com.crediya.model.auth.gateways.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.*;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(-1)
@RequiredArgsConstructor
public class AuthWebFilter implements WebFilter {

    private final TokenService tokenService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        log.debug("Incoming request to [{}]", path);

        // Public endpoints
        if (path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/webjars/")
                || path.startsWith("/api/v1/login")) {
            log.trace("Skipping authentication for public endpoint [{}]", path);
            return chain.filter(exchange);
        }

        // Authorization header
        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path [{}]", path);
            return unauthorized(exchange, "authorization: Missing Bearer token");
        }

        String token = auth.substring("Bearer ".length());
        log.debug("Validating token for path [{}]", path);

        return tokenService.validate(token)
                .flatMap(user -> {
                    log.info("Token validated successfully for user [{}] with role [{}]", user.getEmail(), user.getRol().getName());
                    exchange.getAttributes().put("authUser", user);

                    if (path.startsWith("/api/v1/usuarios")) {
                        if (!(user.getRol().getName().equalsIgnoreCase("ADMIN")
                                || user.getRol().getName().equalsIgnoreCase("ASESOR"))) {
                            log.warn("Access denied for user [{}] with role [{}] to [{}]", user.getEmail(), user.getRol().getName(), path);
                            return forbidden(exchange, "forbidden: Requires ADMIN or ASESOR");
                        }
                    }

                    return chain.filter(exchange);
                })
                .doOnError(e -> log.error("Error validating token for path [{}]: {}", path, e.getMessage(), e))
                .onErrorResume(e -> unauthorized(exchange, e.getMessage()));
    }

    private Mono<Void> unauthorized(ServerWebExchange ex, String msg) {
        log.error("Unauthorized access: {}", msg);
        ex.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        ex.getResponse().getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        String body = String.format("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\"}", msg);
        byte[] bytes = body.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        return ex.getResponse().writeWith(Mono.just(ex.getResponse()
                .bufferFactory()
                .wrap(bytes)));
    }

    private Mono<Void> forbidden(ServerWebExchange ex, String msg) {
        log.error("Forbidden access: {}", msg);
        return Mono.error(new IllegalArgumentException(msg));
    }
}
