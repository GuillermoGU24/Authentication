package co.com.crediya.usecase.registeruser;

import co.com.crediya.model.Rol.gateways.RolRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final RolRepository rolRepository;

    public Mono<Boolean> existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Mono<User> save(User user) {
        user.validateForRegistration();

        return rolRepository.findById(user.getRol().getId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("role: Role does not exist")))
                .then(existsByEmail(user.getEmail()))
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

    public Mono<User> findAuthUserById(Integer id) {
        return userRepository.findAuthUserById(id);
    }

}