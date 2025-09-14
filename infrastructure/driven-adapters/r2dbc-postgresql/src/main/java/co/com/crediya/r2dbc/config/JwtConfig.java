package co.com.crediya.r2dbc.config;

import co.com.crediya.model.auth.gateways.AuthUserRepository;
import co.com.crediya.model.auth.gateways.TokenService;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.r2dbc.jwt.TokenServiceJwt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public TokenService tokenService(
            @Value("${jwt.secret-base64}") String secret,
            @Value("${jwt.ttl-seconds}") long ttl,
            UserRepository userRepository) {
        return new TokenServiceJwt(TokenServiceJwt.keyFrom(secret), ttl, userRepository);
    }

}
