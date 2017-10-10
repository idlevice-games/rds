package ca.idlevice.rds.repositories;


import ca.idlevice.rds.world.World;
        import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static javafx.scene.input.KeyCode.T;

public interface WorldRepository extends ReactiveMongoRepository<World, String>
{


}
