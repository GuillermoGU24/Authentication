package co.com.crediya.api;

import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class AuthRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/login", method = RequestMethod.POST,
                    operation = @Operation(summary = "Login by email/password", operationId = "login")
            )
    })
    public RouterFunction<ServerResponse> authRoutes(AuthHandler h) {
        return route(POST("/api/v1/login"), h::login)
                .andRoute(GET("/api/v1/me"), h::me);
    }
}
