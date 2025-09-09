package co.com.crediya.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("rol")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RolEntity {
    @Id
    @Column("id_rol")
    private Integer rolId;

    @Column("nombre")
    private String rolNombre;

    @Column("descripcion")
    private String rolDescripcion;
}