package co.com.crediya.r2dbc.jwt;

import co.com.crediya.model.auth.AuthUser;
import co.com.crediya.model.auth.gateways.TokenService;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class TokenServiceJwt implements TokenService {

    private final SecretKey key;
    private final long ttlSeconds;
    private final UserRepository userRepository;

    public TokenServiceJwt(SecretKey key, long ttlSeconds, UserRepository userRepository) {
        this.key = key;
        this.ttlSeconds = ttlSeconds;

        this.userRepository = userRepository;
    }

    @Override
    public String generate(AuthUser user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(user.getIdUser().toString()) // aquí va el UUID
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ttlSeconds)))
                .addClaims(Map.of(
                        "email", user.getEmail(),
                        "rolId", user.getRol().getId(),
                        "rolName", user.getRol().getName()
                ))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }


    @Override
    public Mono<User> validate(String token) {
        if (token == null || token.isBlank()) {
            return Mono.error(new IllegalArgumentException("authorization: Missing token"));
        }

        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            String subject = jws.getPayload().getSubject();
            Integer id = Integer.valueOf(subject);

            return userRepository.findAuthUserById(id)
                    .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")));

        } catch (JwtException e) {
            return Mono.error(new IllegalArgumentException("authorization: Invalid or expired token"));
        }
    }


    public static SecretKey keyFrom(String base64) {
        return Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(base64));
    }
}
