package co.com.crediya.r2dbc;

import co.com.crediya.model.Rol.Rol;
import co.com.crediya.model.auth.AuthUser;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.r2dbc.entity.UserEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(UserReactiveRepositoryAdapter.class);

    private final TransactionalOperator tx;

    public UserReactiveRepositoryAdapter(UserReactiveRepository repository,
                                         ObjectMapper mapper,
                                         TransactionalOperator tx) {
        super(repository, mapper, entity -> mapper.map(entity, User.class));
        this.tx = tx;
    }

    @Override
    public Mono<User> save(User user) {
        log.info("Saving user with email: {}", user.getEmail());
        return tx.transactional(super.save(user))
                .doOnSuccess(saved -> log.debug("User saved successfully with id: {}", saved.getIdUser()))
                .doOnError(err -> log.error("Error saving user with email: {}", user.getEmail(), err));
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        log.debug("Checking if user exists with email: {}", email);
        return repository.existsByEmail(email)
                .doOnNext(exists -> {
                    if (exists) {
                        log.info("User exists with email: {}", email);
                    } else {
                        log.info("No user found with email: {}", email);
                    }
                });
    }

    @Override
    public Mono<User> findByDocument(String document) {
        log.debug("Searching user by document: {}", document);
        return repository.findByDocument(document)
                .map(this::toEntity)
                .doOnNext(u -> log.info("User found with document: {}", document))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("No user found with document: {}", document);
                    return Mono.error(new IllegalArgumentException("document: User not found"));
                }));
    }

    @Override
    public Mono<User> findAuthUserById(Integer id) {
        log.debug("Searching user by id: {}", id);
        return repository.findUserWithRolById(id)
                .map(entity -> {
                    log.info("User with role found for id: {}", id);
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
                })
                .doOnError(err -> log.error("Error finding user by id: {}", id, err));
    }

    @Override
    public Flux<User> findByDocumentIn(List<String> documents) {
        log.debug("Searching users by documents list: {}", documents);
        return repository.findByDocumentIn(documents)
                .map(entity -> {
                    log.debug("Mapping user entity with document: {}", entity.getDocument());
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
                })
                .doOnComplete(() -> log.info("Finished searching users by documents list"));
    }
}
