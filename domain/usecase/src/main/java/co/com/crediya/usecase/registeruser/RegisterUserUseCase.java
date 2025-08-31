package co.com.crediya.usecase.registeruser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;

    public Mono<User> save(User user) {
        user.validateForRegistration();

        return userRepository.existsByEmail(user.getEmail())
                .filter(exists -> exists)
                .flatMap(exists -> Mono.error(new IllegalArgumentException("email: Email is already registered")))
                .cast(User.class)
                .switchIfEmpty(userRepository.save(user));
    }
}