package co.com.crediya.model.user.gateways;

import co.com.crediya.model.auth.AuthUser;
import co.com.crediya.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> save(User user);
    Mono<Boolean> existsByEmail(String email);
    Mono<User> findByDocument(String document);
    Mono<User> findAuthUserById(Integer id);
}