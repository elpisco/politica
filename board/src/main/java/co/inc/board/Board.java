package co.inc.board;

import io.dropwizard.Application;
import io.dropwizard.java8.Java8Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.inc.board.infrastructure.config.BoardConfig;
import co.inc.board.infrastructure.config.MongoConfig;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.MongoClient;

public class Board extends Application<BoardConfig> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Board.class);

    @Override
    public void initialize(Bootstrap<BoardConfig> bootstrap) {

        bootstrap.addBundle(new Java8Bundle());
    }

    private void addCORSSupport(Environment environment) {

        Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS,PATCH");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        filter.setInitParameter("allowCredentials", "true");
    }

    private ObjectMapper configureJackson(Environment environment) {

        ObjectMapper objectMapper = environment.getObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
    }
    
    private MongoClient getMongoClient(BoardConfig boardConfig) {
		MongoConfig mongoConfig = boardConfig.getMongoConfig();
        MongoClient mongoClient = new MongoClient(mongoConfig.getDbName());
        
        return mongoClient;
	}

    @Override
    public void run(BoardConfig boardConfig, Environment environment) throws Exception {

        // add CORS support.
        addCORSSupport(environment);
        
        // Configure Jackson serialization and deserialization.
        ObjectMapper objectMapper = configureJackson(environment);
        
        MongoClient mongoClient = getMongoClient(boardConfig);
    }

    public static void main(String[] args) {

        try {
            
            Board board = new Board();
            board.run(args);

        } catch (Exception e) {

            LOGGER.error("main", e);
        }
    }
}
