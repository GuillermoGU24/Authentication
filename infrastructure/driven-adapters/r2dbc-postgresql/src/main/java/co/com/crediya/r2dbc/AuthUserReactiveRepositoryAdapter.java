package co.com.crediya.r2dbc;

import co.com.crediya.model.Rol.Rol;
import co.com.crediya.model.auth.AuthUser;
import co.com.crediya.model.auth.gateways.AuthUserRepository;
import co.com.crediya.r2dbc.entity.AuthUserEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class AuthUserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        AuthUser,
        AuthUserEntity,
        Integer,
        AuthUserReactiveRepository
        > implements AuthUserRepository {

    private static final Logger log = LoggerFactory.getLogger(AuthUserReactiveRepositoryAdapter.class);

    private final TransactionalOperator tx;

    public AuthUserReactiveRepositoryAdapter(AuthUserReactiveRepository repository,
                                             ObjectMapper mapper,
                                             TransactionalOperator tx) {
        super(repository, mapper, AuthUserReactiveRepositoryAdapter::toDomain);
        this.tx = tx;
    }

    @Override
    public Mono<AuthUser> findByEmail(String email) {
        log.info("Searching user by email: {}", email);

        return repository.findByEmail(email)
                .doOnNext(user -> log.debug("User entity found for email: {}", email))
                .map(AuthUserReactiveRepositoryAdapter::toDomain)
                .doOnNext(user -> log.debug("Mapped AuthUser domain object: {}", user))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("User not found for email: {}", email);
                    return Mono.error(new IllegalArgumentException("credentials: User not found"));
                }));
    }

    private static AuthUser toDomain(AuthUserEntity e) {
        AuthUser d = new AuthUser();
        d.setIdUser(e.getIdUser());
        d.setEmail(e.getEmail());
        d.setPasswordHash(e.getPasswordHash());
        d.setDocument(e.getDocument());
        d.setRol(new Rol(e.getIdRol(), e.getRolName(), e.getRolDescription()));
        return d;
    }
}
