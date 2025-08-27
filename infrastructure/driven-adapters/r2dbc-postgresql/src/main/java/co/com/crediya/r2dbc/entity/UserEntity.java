package co.com.crediya.r2dbc.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    @Column("id_user")
    private int idUser;

    private String name;

    @Column("last_name")
    private String lastName;

    private String email;
    private String document;
    private String phone;

    @Column("id_rol")
    private int idRol;

    @Column("base_salary")
    private Long baseSalary;
}
