package co.com.crediya.model.user.gateways;

import co.com.crediya.model.auth.AuthUser;
import co.com.crediya.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserRepository {
    Mono<User> save(User user);
    Mono<Boolean> existsByEmail(String email);
    Mono<User> findByDocument(String document);
    Mono<User> findAuthUserById(Integer id);
    Flux<User> findByDocumentIn(List<String> documents);

}