package co.com.crediya.api;

import co.com.crediya.api.dto.LoginRequest;
import co.com.crediya.api.dto.LoginResponse;
import co.com.crediya.api.util.ValidationUtil;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.login.LoginUseCase;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthHandler {

    private final LoginUseCase loginUseCase;
    private final Validator validator;

    @Value("${jwt.ttl-seconds}")
    private long ttlSeconds;

    public Mono<ServerResponse> login(ServerRequest req) {
        log.debug("Processing login request from [{}]", req.remoteAddress().map(Object::toString).orElse("unknown"));

        return req.bodyToMono(LoginRequest.class)
                .doOnNext(body -> log.trace("Received login request payload for email [{}]", body.getEmail()))
                .flatMap(body -> ValidationUtil.validate(body, validator))
                .flatMap(body -> loginUseCase.login(body.getEmail(), body.getPassword())
                        .doOnSuccess(token -> log.info("User [{}] logged in successfully", body.getEmail()))
                        .doOnError(e -> log.warn("Login failed for [{}]: {}", body.getEmail(), e.getMessage()))
                )
                .map(token -> LoginResponse.builder()
                        .token(token)
                        .tokenType("Bearer")
                        .expiresIn(ttlSeconds)
                        .build()
                )
                .flatMap(res -> {
                    log.debug("Returning login response with token TTL [{}s]", ttlSeconds);
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(res);
                });
    }

    public Mono<ServerResponse> me(ServerRequest req) {
        User user = (User) req.exchange().getAttributes().get("authUser");

        if (user == null) {
            log.warn("Unauthorized access attempt to /me endpoint");
            return ServerResponse.status(401).build();
        }

        log.info("Returning authenticated user [{} {}] with role [{}]",
                user.getName(), user.getLastName(),
                user.getRol() != null ? user.getRol().getName() : "N/A");

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user);
    }
}
