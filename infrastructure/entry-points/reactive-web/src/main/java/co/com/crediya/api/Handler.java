package co.com.crediya.api;

import co.com.crediya.api.dto.DocumentsRequest;
import co.com.crediya.api.dto.UserRequest;
import co.com.crediya.api.dto.UserResponse;
import co.com.crediya.api.mapper.UserMapper;
import co.com.crediya.api.util.ValidationUtil;
import co.com.crediya.model.auth.AuthUser;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.registeruser.RegisterUserUseCase;
import co.com.crediya.usecase.user.FindUserUseCase;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {


    private final RegisterUserUseCase registerUserUseCase;
    private final FindUserUseCase findUserUseCase;
    private final UserMapper userMapper;
    private final jakarta.validation.Validator validator;

    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequest.class)
                .flatMap(req -> ValidationUtil.validate(req, validator))
                .doOnNext(req -> log.info("Request received to register user with email: {}", req.getEmail()))
                .map(userMapper::toDomain)
                .flatMap(registerUserUseCase::save)
                .map(userMapper::toResponse)
                .flatMap(saved -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of(
                                "status", 201,
                                "message", "User successfully created"
                        )));


    }

    public Mono<ServerResponse> listenGetUserByDocument(ServerRequest serverRequest) {
        String document = serverRequest.pathVariable("document");
        log.info("Request to get user by document: {}", document);

        return registerUserUseCase.findByDocument(document)
                .map(userMapper::toResponse)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> listenGetUsersByDocuments(ServerRequest request) {
        return request.bodyToMono(DocumentsRequest.class)
                // Si el body está vacío o no se pudo mapear → DocumentsRequest null
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Request body is required")))
                .flatMapMany(req -> {
                    if (req.documents() == null || req.documents().isEmpty()) {
                        return Flux.error(new IllegalArgumentException("Document list cannot be empty"));
                    }
                    return findUserUseCase.findByDocumentIn(req.documents());
                })
                .map(userMapper::toResponse)
                .collectList()
                .flatMap(users -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(users))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .bodyValue(Map.of("error", e.getMessage())));
    }


}