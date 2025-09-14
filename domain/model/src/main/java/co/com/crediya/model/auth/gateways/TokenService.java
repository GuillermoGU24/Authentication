package co.com.crediya.model.auth.gateways;

import co.com.crediya.model.auth.AuthUser;
import co.com.crediya.model.user.User;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

public interface TokenService {
    String generate(AuthUser user);
    Mono<User> validate(String token);
    default String traceId(ContextView ctx) {
        return ctx.getOrDefault("traceId", "no-trace");
    }
}
