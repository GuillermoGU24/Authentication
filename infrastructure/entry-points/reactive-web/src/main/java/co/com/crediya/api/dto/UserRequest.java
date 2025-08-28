package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String lastName;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico es inválido")
    private String email;

    @NotBlank(message = "El documento de identidad es obligatorio")
    @Pattern(regexp = "\\d{6,12}", message = "El documento debe contener entre 6 y 12 dígitos")
    private String document;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9+\\-\\s]{7,15}$", message = "El formato del teléfono es inválido")
    private String phone;

    @NotNull(message = "El rol es obligatorio")
    @Min(value = 1, message = "El rol debe ser mayor a 0")
    private Integer idRol;

    @NotNull(message = "El salario base es obligatorio")
    @Min(value = 0, message = "El salario base no puede ser negativo")
    @Max(value = 15000000, message = "El salario base no puede exceder 15,000,000")
    private Long baseSalary;

    // CAMPOS NUEVOS
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(min = 10, max = 200, message = "La dirección debe tener entre 10 y 200 caracteres")
    private String direccion;
}