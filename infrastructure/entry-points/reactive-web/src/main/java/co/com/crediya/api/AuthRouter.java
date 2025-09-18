package co.com.crediya.api;

import co.com.crediya.api.dto.LoginRequest;
import co.com.crediya.api.dto.LoginResponse;
import co.com.crediya.api.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Tag(name = "Autenticación", description = "Operaciones de login y verificación de usuario autenticado")
public class AuthRouter {

    @Bean
    @RouterOperations({

            // POST /api/v1/login
            @RouterOperation(
                    path = "/api/v1/login",
                    method = RequestMethod.POST,
                    operation = @Operation(
                            operationId = "login",
                            summary = "Login by email/password",
                            description = "Authenticates a user with email and password, returning a JWT token.",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    required = true,
                                    description = "Login credentials",
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = LoginRequest.class),
                                            examples = {
                                                    @ExampleObject(
                                                            name = "Login correcto",
                                                            value = "{ \"email\": \"user@example.com\", \"password\": \"123456\" }"
                                                    )
                                            }
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Login successful, returns JWT",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = LoginResponse.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "Invalid credentials",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    examples = @ExampleObject(
                                                            name = "Unauthorized",
                                                            value = "{ \"status\": 401, \"error\": \"Invalid username or password\" }"
                                                    )
                                            )
                                    )
                            }
                    )
            ),

            // GET /api/v1/me
            @RouterOperation(
                    path = "/api/v1/me",
                    method = RequestMethod.GET,
                    operation = @Operation(
                            operationId = "me",
                            summary = "Get logged user info",
                            description = "Returns information about the currently authenticated user (from JWT).",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Authenticated user data",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = UserResponse.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "Unauthorized - missing or invalid token",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    examples = @ExampleObject(
                                                            name = "Unauthorized",
                                                            value = "{ \"status\": 401, \"error\": \"Invalid or expired token\" }"
                                                    )
                                            )
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> authRoutes(AuthHandler h) {
        return route(POST("/api/v1/login"), h::login)
                .andRoute(GET("/api/v1/me"), h::me);
    }
}
