package co.com.crediya.model.Rol.gateways;

import co.com.crediya.model.Rol.Rol;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RolRepository {
    Mono<Rol> findById(Integer id);
    Flux<Rol> findAll();
}
