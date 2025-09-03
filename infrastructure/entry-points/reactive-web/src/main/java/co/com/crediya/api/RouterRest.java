package co.com.crediya.api;


import co.com.crediya.api.dto.UserRequest;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Tag(name = "Usuarios", description = "Operaciones sobre usuarios")
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    method = RequestMethod.POST,
                    operation = @Operation(
                            operationId = "registerUser",
                            summary = "Register a new user",
                            description = "Creates a new user in the system from the submitted data",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    required = true,
                                    description = "User data to be registered",
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = UserRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "User successfully registered",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = UserResponse.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid request data",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    examples = {
                                                            @ExampleObject(
                                                                    name = "Error de validación",
                                                                    value = "{\n" +
                                                                            "  \"status\": 400,\n" +
                                                                            "  \"error\": \"Validation failed\",\n" +
                                                                            "  \"details\": [\n" +
                                                                            "    { \"field\": \"email\", \"message\": \"Invalid email format\" },\n" +
                                                                            "    { \"field\": \"document\", \"message\": \"Document must contain between 6 and 12 digits\" }\n" +
                                                                            "  ]\n" +
                                                                            "}"
                                                            )
                                                    }
                                            )
                                    )
                            }
                    )

            )

    })


    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/usuarios"), handler::listenSaveUser)
                .andRoute(GET("/api/v1/usuarios/document/{document}"), handler::listenGetUserByDocument);

    }


}
