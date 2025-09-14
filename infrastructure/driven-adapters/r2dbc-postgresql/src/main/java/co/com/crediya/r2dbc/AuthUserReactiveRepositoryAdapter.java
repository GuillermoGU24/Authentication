package co.com.crediya.r2dbc;

import co.com.crediya.model.Rol.Rol;
import co.com.crediya.model.auth.AuthUser;
import co.com.crediya.model.auth.gateways.AuthUserRepository;
import co.com.crediya.r2dbc.entity.AuthUserEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
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

    private final TransactionalOperator tx;

    public AuthUserReactiveRepositoryAdapter(AuthUserReactiveRepository repository,
                                             ObjectMapper mapper,
                                             TransactionalOperator tx) {
        super(repository, mapper, AuthUserReactiveRepositoryAdapter::toDomain);
        this.tx = tx;
    }

    @Override
    public Mono<AuthUser> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(AuthUserReactiveRepositoryAdapter::toDomain)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("credentials: User not found")));
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
