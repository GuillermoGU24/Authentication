package co.com.crediya.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("usuario")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AuthUserEntity {
    @Id
    @Column("id_usuario")
    private Integer idUser;
    private String email;
    @Column("clave_hash")
    private String passwordHash;
    @Column("id_rol")
    private Integer idRol; // 1=ADMIN,2=ASESOR,3=CLIENTE
    @Column("rol_nombre")
    private String rolName;
    @Column("rol_descripcion")
    private String rolDescription;
    @Column("documento_identidad")
    private String document;
}
