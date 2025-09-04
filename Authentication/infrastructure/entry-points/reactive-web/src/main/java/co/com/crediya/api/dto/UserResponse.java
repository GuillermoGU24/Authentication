package co.com.crediya.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private int idUser;
    private String name;
    private String lastName;
    private String email;
    private String document;
    private String phone;
    private int idRol;
    private Long baseSalary ;
}
