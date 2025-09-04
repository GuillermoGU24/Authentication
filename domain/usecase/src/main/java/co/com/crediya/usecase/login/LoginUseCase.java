package co.com.crediya.usecase.login;

import co.com.crediya.model.auth.gateways.AuthUserRepository;
import co.com.crediya.model.auth.gateways.PasswordEncoder;
import co.com.crediya.model.auth.gateways.TokenService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase {

    private final AuthUserRepository repo;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;


    public Mono<String> login(String email, String rawPassword) {
        return repo.findByEmail(email)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("credentials: Invalid email or password")))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
                        return Mono.error(new IllegalArgumentException("credentials: Invalid email or password"));
                    }
                    return Mono.just(tokenService.generate(user));
                });
    }
}
