package co.com.crediya.model.auth.gateways;

import co.com.crediya.model.auth.AuthUser;
import reactor.util.context.ContextView;

public interface TokenService {
    String generate(AuthUser user);
    AuthUser validate(String token);
    default String traceId(ContextView ctx) {
        return ctx.getOrDefault("traceId", "no-trace");
    }
}
