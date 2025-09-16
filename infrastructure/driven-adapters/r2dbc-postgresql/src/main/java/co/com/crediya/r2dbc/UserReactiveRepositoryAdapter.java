package co.com.crediya.r2dbc;

import co.com.crediya.model.Rol.Rol;
import co.com.crediya.model.auth.AuthUser;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.r2dbc.entity.UserEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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

    @Override
    public Mono<User> findAuthUserById(Integer id) {
        return repository.findUserWithRolById(id)
                .map(entity -> {
                    Rol rol = new Rol(entity.getIdRol(), entity.getRolName(), entity.getRolDescription());
                    User user = new User();
                    user.setIdUser(entity.getIdUser());
                    user.setName(entity.getName());
                    user.setLastName(entity.getLastName());
                    user.setEmail(entity.getEmail());
                    user.setDocument(entity.getDocument());
                    user.setPhone(entity.getPhone());
                    user.setBaseSalary(entity.getBaseSalary());
                    user.setBirthDate(entity.getBirthDate());
                    user.setAddress(entity.getAddress());
                    user.setRol(rol);
                    return user;
                });
    }

    @Override
    public Flux<User> findByDocumentIn(List<String> documents) {
        return repository.findByDocumentIn(documents)
                .map(entity -> {
                    Rol rol = new Rol(entity.getIdRol(),
                            entity.getRolName(),
                            entity.getRolDescription());
                    User user = new User();
                    user.setIdUser(entity.getIdUser());
                    user.setName(entity.getName());
                    user.setLastName(entity.getLastName());
                    user.setEmail(entity.getEmail());
                    user.setDocument(entity.getDocument());
                    user.setPhone(entity.getPhone());
                    user.setBaseSalary(entity.getBaseSalary());
                    user.setBirthDate(entity.getBirthDate());
                    user.setAddress(entity.getAddress());
                    user.setRol(rol);
                    return user;
                });
    }

}
