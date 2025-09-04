package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Schema(description = "Nombre del usuario", example = "Juan")
    private String name;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Correo electrónico", example = "juan.perez@email.com")
    private String email;

    @NotBlank(message = "Document is required")
    @Pattern(regexp = "\\d{6,12}", message = "Document must contain between 6 and 12 digits")
    @Schema(description = "Documento de identidad", example = "123456789")
    private String document;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9+\\-\\s]{7,15}$", message = "Invalid phone format")
    @Schema(description = "Teléfono de contacto", example = "3001234567")
    private String phone;

    @NotNull(message = "Role ID is required")
    @Min(value = 1, message = "Role ID must be greater than 0")
    @Schema(description = "ID del rol", example = "1")
    private Integer idRol;

    @NotNull(message = "Base salary is required")
    @Min(value = 0, message = "Base salary cannot be negative")
    @Max(value = 15000000, message = "Base salary cannot exceed 15,000,000")
    @Schema(description = "Salario base", example = "1500000")
    private Long baseSalary;

    @NotNull(message = "Birthdate is required")
    @Past(message = "Birthdate must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Fecha de nacimiento (yyyy-MM-dd)", example = "1990-05-15")
    private LocalDate birthDate;

    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 200, message = "Address must be between 10 and 200 characters")
    @Schema(description = "Dirección de residencia", example = "Calle 123 #45-67")
    private String address;
}
