package co.com.crediya.api;

import co.com.crediya.api.dto.LoginRequest;
import co.com.crediya.api.dto.LoginResponse;
import co.com.crediya.api.util.ValidationUtil;
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
        return req.bodyToMono(LoginRequest.class)
                .flatMap(body -> ValidationUtil.validate(body, validator))
                .flatMap(body -> loginUseCase.login(body.getEmail(), body.getPassword()))
                .map(token -> LoginResponse.builder()
                        .token(token).tokenType("Bearer").expiresIn(ttlSeconds).build())
                .flatMap(res -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(res));
    }
}
