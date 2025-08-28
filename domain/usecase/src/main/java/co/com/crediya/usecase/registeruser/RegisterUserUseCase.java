package co.com.crediya.usecase.registeruser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;

    public Mono<User> save(User user) {
        // 1. Validar reglas del dominio
        user.validateForRegistration();

        // 2. Verificar unicidad del email
        return userRepository.existsByEmail(user.getEmail())
                .filter(exists -> exists) // Solo procede si existe
                .flatMap(exists -> Mono.error(new IllegalArgumentException("El correo electrónico ya está registrado")))
                .cast(User.class) // Cast necesario para el tipo
                .switchIfEmpty(userRepository.save(user)); // Si no existe, guarda el usuario
    }
}