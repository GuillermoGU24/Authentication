package co.com.crediya.r2dbc;

import co.com.crediya.r2dbc.entity.AuthUserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AuthUserReactiveRepository extends ReactiveCrudRepository<AuthUserEntity, Integer>, ReactiveQueryByExampleExecutor<AuthUserEntity> {
    @Query("SELECT * FROM usuario WHERE email = :email")
    Mono<AuthUserEntity> findByEmail(String email);

}
