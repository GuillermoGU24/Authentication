package co.com.crediya.api.security;// co.com.crediya.security.AuthWebFilter.java


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

        if (path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/webjars/")
                || path.startsWith("/api/v1/login")) {
            return chain.filter(exchange);
        }
        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            return unauthorized(exchange, "authorization: Missing Bearer token");
        }

        String token = auth.substring("Bearer ".length());
        AuthUser user;
        try {
            user = tokenService.validate(token);
        } catch (IllegalArgumentException e) {
            return unauthorized(exchange, e.getMessage());
        }

        exchange.getAttributes().put("authUser", user);

        if (path.startsWith("/api/v1/usuarios")) {
            if (!(user.getRol().getName().equalsIgnoreCase("ADMIN")
                    || user.getRol().getName().equalsIgnoreCase("ASESOR"))) {
                return forbidden(exchange, "forbidden: Requires ADMIN or ASESOR");
            }
        }
        return chain.filter(exchange);
    }

    private Mono<Void> unauthorized(ServerWebExchange ex, String msg) {
        ex.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        ex.getResponse().getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        String body = String.format("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\"}", msg);
        byte[] bytes = body.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        return ex.getResponse().writeWith(Mono.just(ex.getResponse()
                .bufferFactory()
                .wrap(bytes)));
    }


    private Mono<Void> forbidden(ServerWebExchange ex, String msg) {
        return Mono.error(new IllegalArgumentException(msg));
    }
}
