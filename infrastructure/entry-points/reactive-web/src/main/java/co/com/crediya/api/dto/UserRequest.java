package co.com.crediya.api.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Formato de correo inválido")
    private String email;

    @NotBlank(message = "El documento de identidad es obligatorio")
    private String document;

    @NotBlank(message = "El teléfono es obligatorio")
    private String phone;

    @NotNull(message = "El rol es obligatorio")
    private Integer idRol;

    @NotNull(message = "El salario base es obligatorio")
    private Long baseSalary;
}
