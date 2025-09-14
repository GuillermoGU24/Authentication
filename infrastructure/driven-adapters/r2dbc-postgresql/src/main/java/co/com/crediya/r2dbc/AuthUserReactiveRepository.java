package co.com.crediya.r2dbc;

import co.com.crediya.r2dbc.entity.AuthUserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AuthUserReactiveRepository extends ReactiveCrudRepository<AuthUserEntity, Integer>, ReactiveQueryByExampleExecutor<AuthUserEntity> {
    @Query("""
            SELECT u.id_usuario, u.email, u.clave_hash, u.documento_identidad, u.id_rol,
                   r.nombre as rol_nombre, r.descripcion as rol_descripcion
            FROM usuario u
            INNER JOIN rol r ON u.id_rol = r.id_rol
            WHERE u.email = :email
            """)
    Mono<AuthUserEntity> findByEmail(String email);

}
