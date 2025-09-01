package co.com.crediya.usecase.registeruser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterUserUseCase {
    private final UserRepository userRepository;

    public Mono<Boolean> existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Mono<User> save(User user) {
        user.validateForRegistration();
        return existsByEmail(user.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("email: Email is already registered"));
                    }
                    return userRepository.save(user);
                });
    }

    public Mono<User> findByDocument(String document) {
        return userRepository.findByDocument(document);
    }

}