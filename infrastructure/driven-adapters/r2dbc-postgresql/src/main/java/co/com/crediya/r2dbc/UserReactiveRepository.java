package co.com.crediya.r2dbc;

import co.com.crediya.r2dbc.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserReactiveRepository extends ReactiveCrudRepository<UserEntity, String>, ReactiveQueryByExampleExecutor<UserEntity> {
    @Query("SELECT EXISTS (SELECT 1 FROM usuario WHERE email = :email)")
    Mono<Boolean> existsByEmail(String email);

    @Query("SELECT * FROM usuario WHERE documento_identidad = :document")
    Mono<UserEntity> findByDocument(String document);
}
