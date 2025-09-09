package co.com.crediya.r2dbc.jwt;

import co.com.crediya.model.Rol.Rol;
import co.com.crediya.model.auth.AuthUser;
import co.com.crediya.model.auth.gateways.TokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class TokenServiceJwt implements TokenService {

    private final SecretKey key;
    private final long ttlSeconds;

    public TokenServiceJwt(SecretKey key, long ttlSeconds) {
        this.key = key;
        this.ttlSeconds = ttlSeconds;
    }

    @Override
    public String generate(AuthUser user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(user.getDocument())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ttlSeconds)))
                .addClaims(Map.of(
                        "uid", user.getIdUser(),
                        "email", user.getEmail(),
                        "rolId", user.getRol().getId(),
                        "rolNombre", user.getRol().getName()
                ))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    @Override
    public AuthUser validate(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            Claims c = jws.getPayload();

            AuthUser u = new AuthUser();
            u.setIdUser(((Number) c.get("uid")).intValue());
            u.setEmail((String) c.get("email"));
            u.setDocument(c.getSubject());

            // Manejo defensivo del rol
            Number rolId = (Number) c.get("rolId");
            String rolNombre = (String) c.get("rolNombre");

            if (rolId != null && rolNombre != null) {
                u.setRol(new Rol(rolId.intValue(), rolNombre, null));
            } else {
                throw new IllegalArgumentException("authorization: Token does not contain role information");
            }

            return u;
        } catch (JwtException e) {
            throw new IllegalArgumentException("authorization: Invalid or expired token");
        }
    }


    public static SecretKey keyFrom(String base64) {
        return Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(base64));
    }
}
