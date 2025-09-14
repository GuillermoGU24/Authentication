package co.com.crediya.api.dto;

import co.com.crediya.model.Rol.Rol;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserResponse {
    private int idUser;
    private String name;
    private String lastName;
    private String email;
    private String document;
    private String phone;
    private Rol rol;
    private Long baseSalary;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String address;
}
