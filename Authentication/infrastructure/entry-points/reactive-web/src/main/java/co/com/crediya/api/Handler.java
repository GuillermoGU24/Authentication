package co.com.crediya.api;

import co.com.crediya.api.dto.UserRequest;
import co.com.crediya.api.mapper.UserMapper;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.registeruser.RegisterUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final RegisterUserUseCase registerUserUseCase;
    private final UserMapper userMapper;
    private final org.springframework.validation.Validator validator;

    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequest.class)
                .flatMap(req -> {
                    var errors = new org.springframework.validation.BeanPropertyBindingResult(req, UserRequest.class.getName());
                    validator.validate(req, errors);

                    if (errors.hasErrors()) {
                        return Mono.error(new IllegalArgumentException(errors.getAllErrors().get(0).getDefaultMessage()));
                    }
                    return Mono.just(req);
                })
                .doOnNext(req -> log.info("Petición recibida para registrar usuario con correo {}", req.getEmail()))
                .map(userMapper::toDomain)
                .flatMap(registerUserUseCase::save)
                .map(userMapper::toResponse)
                .flatMap(saved -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of(
                                "message", "Usuario creado exitosamente",
                                "user", saved
                        )))
                .onErrorResume(e -> {
                    log.error("Error en registro de usuario", e);
                    return ServerResponse.badRequest().bodyValue(Map.of("error", e.getMessage()));
                });
    }
}
