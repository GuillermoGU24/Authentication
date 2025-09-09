package co.com.crediya.r2dbc;


import co.com.crediya.model.Rol.Rol;
import co.com.crediya.model.Rol.gateways.RolRepository;
import co.com.crediya.r2dbc.entity.RolEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RolReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Rol,
        RolEntity,
        Integer,
        RolReactiveRepository
        > implements RolRepository {

    public RolReactiveRepositoryAdapter(RolReactiveRepository repository,
                                        ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, Rol.class));
    }



}
