package co.com.crediya.r2dbc.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("usuario")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    @Column("id_usuario")
    private int idUser;

    @Column("nombre")
    private String name;

    @Column("apellido")
    private String lastName;

    private String email;

    @Column("documento_identidad")
    private String document;

    @Column("telefono")
    private String phone;

    @Column("id_rol")
    private int idRol;

    @Column("rol_nombre")
    private String rolName;

    @Column("rol_descripcion")
    private String rolDescription;

    @Column("salario_base")
    private Long baseSalary;

    @Column("fecha_nacimiento")
    private LocalDate birthDate;

    @Column("direccion")
    private String address;
}
