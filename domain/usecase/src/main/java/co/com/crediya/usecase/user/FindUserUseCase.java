package co.com.crediya.usecase.user;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class FindUserUseCase {

    private final UserRepository userRepository;

    public Mono<User> findByDocument(String document) {
        return userRepository.findByDocument(document)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found for document: " + document)));
    }

    public Flux<User> findByDocumentIn(List<String> documents) {
        if (documents == null || documents.isEmpty()) {
            return Flux.error(new IllegalArgumentException("Document list cannot be empty"));
        }
        return userRepository.findByDocumentIn(documents);
    }
}
