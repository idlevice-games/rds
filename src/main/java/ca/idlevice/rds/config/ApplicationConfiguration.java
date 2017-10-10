package ca.idlevice.rds.config;

import ca.idlevice.rds.handlers.WorldHandler;
import ca.idlevice.rds.repositories.WorldRepository;
import ca.idlevice.rds.world.World;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.server.HttpServer;

import static reactor.core.publisher.Mono.just;

@Configuration
@EnableReactiveMongoRepositories(basePackageClasses = WorldRepository.class)
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@AutoConfigureAfter(EmbeddedMongoAutoConfiguration.class)


class ApplicationConfiguration extends AbstractReactiveMongoConfiguration {
    private final Environment environment;

    public ApplicationConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public MongoClient reactiveMongoClient() {
        int port = environment.getProperty("local.mongo.port", Integer.class);
        return MongoClients.create(String.format("mongodb://localhost:%d", port));
    }

    public @Bean
    ReactiveMongoTemplate reactiveMongoTemplate() throws Exception {
        return new ReactiveMongoTemplate(reactiveMongoClient(), getDatabaseName());
    }

    @Override
    protected String getDatabaseName() {
        return "test";
    }

    // Reactive routing
    @Bean
    static RouterFunction<?> routingFunction(WorldHandler worldHandler)
    {
        return RouterFunctions.route(RequestPredicates.GET("/rest/world/all"), worldHandler::getAll)
                .andRoute(RequestPredicates.GET("/rest/world/{id}"), worldHandler::getId);
    }

    @Bean
    HttpServer server(RouterFunction<?> router)
    {
        HttpHandler handler = RouterFunctions.toHttpHandler(router);
        HttpServer httpServer = HttpServer.create(8081);
        httpServer.start(new ReactorHttpHandlerAdapter(handler));
        return httpServer;
    }

    @Component
    class WorldCLR implements CommandLineRunner
    {

        private final WorldRepository worldRepository;
        ReactiveMongoOperations operations;

        public WorldCLR(WorldRepository worldRepository)
        {
            this.worldRepository = worldRepository;
        }


        public void run(String... strings) throws Exception
        {

            operations.collectionExists(World.class)
                    .flatMap(exists -> exists ? operations.dropCollection(World.class) : just(exists))
                    .flatMap(o -> operations.createCollection(World.class))
                    .then()
                    .block();

           worldRepository.saveAll(Flux.just((new World("Earth",11)))).subscribe();


        }

    }


}
