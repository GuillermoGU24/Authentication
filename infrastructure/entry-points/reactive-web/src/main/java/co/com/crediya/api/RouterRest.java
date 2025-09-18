package co.com.crediya.api;

import co.com.crediya.api.dto.DocumentsRequest;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Tag(name = "Usuarios", description = "Operations related to users")
public class RouterRest {

    @Bean
    @RouterOperations({

            // POST /api/v1/usuarios
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
                                            responseCode = "201",
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
                                                                    name = "Validation error",
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
            ),

            // GET /api/v1/usuarios/document/{document}
            @RouterOperation(
                    path = "/api/v1/usuarios/document/{document}",
                    method = RequestMethod.GET,
                    operation = @Operation(
                            operationId = "getUserByDocument",
                            summary = "Get user by document",
                            description = "Fetches a user based on their document number",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "User found",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = UserResponse.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "User not found"
                                    )
                            }
                    )
            ),

            // POST /api/v1/usuarios/documents
            @RouterOperation(
                    path = "/api/v1/usuarios/documents",
                    method = RequestMethod.POST,
                    operation = @Operation(
                            operationId = "getUsersByDocuments",
                            summary = "Get multiple users by documents",
                            description = "Retrieves a list of users by their document numbers",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    required = true,
                                    description = "List of documents",
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = DocumentsRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "List of users found",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = UserResponse.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid request data"
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/usuarios"), handler::listenSaveUser)
                .andRoute(GET("/api/v1/usuarios/document/{document}"), handler::listenGetUserByDocument)
                .andRoute(POST("/api/v1/usuarios/documents"), handler::listenGetUsersByDocuments);
    }
}
