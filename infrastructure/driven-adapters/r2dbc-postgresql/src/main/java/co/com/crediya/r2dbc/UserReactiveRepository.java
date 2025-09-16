package co.com.crediya.r2dbc;

import co.com.crediya.model.user.User;
import co.com.crediya.r2dbc.entity.AuthUserEntity;
import co.com.crediya.r2dbc.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserReactiveRepository extends ReactiveCrudRepository<UserEntity, String>, ReactiveQueryByExampleExecutor<UserEntity> {
    @Query("SELECT EXISTS (SELECT 1 FROM usuario WHERE email = :email)")
    Mono<Boolean> existsByEmail(String email);

    @Query("SELECT * FROM usuario WHERE documento_identidad = :document")
    Mono<UserEntity> findByDocument(String document);

    @Query("""
    SELECT u.id_usuario,
           u.email,
           u.nombre,
           u.apellido,
           u.documento_identidad,
           u.telefono,
           u.salario_base,
           u.fecha_nacimiento,
           u.direccion,
           u.id_rol,
           r.nombre as rol_nombre,
           r.descripcion as rol_descripcion
    FROM usuario u
    INNER JOIN rol r ON u.id_rol = r.id_rol
    WHERE u.id_usuario = :id
    """)
    Mono<UserEntity> findUserWithRolById(Integer id);

    @Query("""
    SELECT u.id_usuario,
           u.email,
           u.nombre,
           u.apellido,
           u.documento_identidad,
           u.telefono,
           u.salario_base,
           u.fecha_nacimiento,
           u.direccion,
           u.id_rol,
           r.nombre as rol_nombre,
           r.descripcion as rol_descripcion
    FROM usuario u
    INNER JOIN rol r ON u.id_rol = r.id_rol
    WHERE u.documento_identidad IN (:documents)
    """)
    Flux<UserEntity> findByDocumentIn(List<String> documents);

}
