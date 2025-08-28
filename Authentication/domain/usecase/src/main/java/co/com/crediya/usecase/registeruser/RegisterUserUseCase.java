package co.com.crediya.usecase.registeruser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;

    public Mono<User> save(User user) {
        if (user.getBaseSalary() == null ||
                user.getBaseSalary() < 0 || user.getBaseSalary() > 15000000) {
            return Mono.error(new IllegalArgumentException("El salario está fuera del rango permitido"));
        }
        return userRepository.existsByEmail(user.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Correo ya registrado"));
                    }
                    return userRepository.save(user);
                });
    }
}
