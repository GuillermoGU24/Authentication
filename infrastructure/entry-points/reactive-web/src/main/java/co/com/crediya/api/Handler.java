package co.com.crediya.api;

import co.com.crediya.api.dto.DocumentsRequest;
import co.com.crediya.api.dto.UserRequest;
import co.com.crediya.api.mapper.UserMapper;
import co.com.crediya.api.util.ValidationUtil;
import co.com.crediya.usecase.registeruser.RegisterUserUseCase;
import co.com.crediya.usecase.user.FindUserUseCase;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final RegisterUserUseCase registerUserUseCase;
    private final FindUserUseCase findUserUseCase;
    private final UserMapper userMapper;
    private final Validator validator;

    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        log.debug("Handling request to register a new user");

        return serverRequest.bodyToMono(UserRequest.class)
                .flatMap(req -> ValidationUtil.validate(req, validator))
                .doOnNext(req -> log.info("Registering user with email: {}", req.getEmail()))
                .map(userMapper::toDomain)
                .flatMap(registerUserUseCase::save)
                .doOnSuccess(user -> log.debug("User saved with id: {}", user.getIdUser()))
                .map(userMapper::toResponse)
                .flatMap(saved -> {
                    log.info("User [{}] successfully registered", saved.getEmail());
                    return ServerResponse.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of(
                                    "status", 201,
                                    "message", "User successfully created"
                            ));
                })
                .doOnError(e -> log.error("Error while registering user: {}", e.getMessage(), e));
    }

    public Mono<ServerResponse> listenGetUserByDocument(ServerRequest serverRequest) {
        String document = serverRequest.pathVariable("document");
        log.info("Handling request to get user by document [{}]", document);

        return registerUserUseCase.findByDocument(document)
                .map(userMapper::toResponse)
                .doOnNext(user -> log.debug("User found with document [{}]", document))
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("No user found with document [{}]", document);
                    return ServerResponse.notFound().build();
                }))
                .doOnError(e -> log.error("Error while fetching user by document [{}]: {}", document, e.getMessage(), e));
    }

    public Mono<ServerResponse> listenGetUsersByDocuments(ServerRequest request) {
        log.debug("Handling request to fetch users by documents");

        return request.bodyToMono(DocumentsRequest.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Request body is required")))
                .flatMapMany(req -> {
                    if (req.documents() == null || req.documents().isEmpty()) {
                        log.warn("Received empty document list in request");
                        return Flux.error(new IllegalArgumentException("Document list cannot be empty"));
                    }
                    log.info("Fetching users with documents: {}", req.documents());
                    return findUserUseCase.findByDocumentIn(req.documents());
                })
                .map(userMapper::toResponse)
                .collectList()
                .doOnSuccess(users -> log.debug("Found [{}] users", users.size()))
                .flatMap(users -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(users))
                .onErrorResume(e -> {
                    log.error("Error while fetching users by documents: {}", e.getMessage(), e);
                    return ServerResponse.status(HttpStatus.BAD_REQUEST)
                            .bodyValue(Map.of("error", e.getMessage()));
                });
    }
}
