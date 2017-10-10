package ca.idlevice.rds.handlers;

import ca.idlevice.rds.repositories.WorldRepository;
import ca.idlevice.rds.world.World;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class WorldHandler {

    private final WorldRepository worldRepository;

    public WorldHandler(WorldRepository worldRepository)
    {
        this.worldRepository = worldRepository;
    }

    public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
        return ServerResponse
                .ok()
                .contentType(APPLICATION_JSON)
                .body(
                        worldRepository.findAll(), World.class
                );
    }

    public Mono<ServerResponse> getId(ServerRequest serverRequest) {

        String id = serverRequest.pathVariable("id");
        return ServerResponse
                .ok()
                .contentType(APPLICATION_JSON)
                .body(
                        worldRepository.findById(id), World.class
                );
    }
}