package co.com.crediya.r2dbc;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.r2dbc.entity.UserEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserEntity,
        String,
        UserReactiveRepository
        > implements UserRepository {
    private final TransactionalOperator tx;


    public UserReactiveRepositoryAdapter(UserReactiveRepository repository,
                                         ObjectMapper mapper,
                                         TransactionalOperator tx) {
        super(repository, mapper, entity -> mapper.map(entity, User.class));
        this.tx = tx;
    }

    @Override
    public Mono<User> save(User user) {
        return tx.transactional(
                super.save(user)
        );
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Mono<User> findByDocument(String document) {
        return repository.findByDocument(document)
                .map(this::toEntity)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("document: User not found")));
    }
}
