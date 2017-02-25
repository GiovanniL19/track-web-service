import controllers.StationController;
import controllers.TrainController;
import controllers.UserController;
import filter.CORSFilter;
import com.sun.net.httpserver.HttpServer;
import filter.JWTFilter;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
/**
 * Created by giovannilenguito on 09/02/2017.
 */
// The Java class will be hosted at the URI path "/"
@Path("/")
public class Main {
    final private static URI SERVER_URI = URI.create("http://localhost:3002/");
    final private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {
        ResourceConfig resourceConfig = new ResourceConfig(getEndpoints());

        HttpServer server = JdkHttpServerFactory.createHttpServer(SERVER_URI, resourceConfig);
        logger.info("Running server on " + SERVER_URI);
    }


    @GET
    @Produces("text/plain")
    public String getMessage() {
        return "Hello, this is the track web service which implements all the logic and CRUD operations.";
    }

    private static Set<Class<?>> getEndpoints(){
        logger.info("Setting up server");
        final Set<Class<?>> endpoints = new HashSet<Class<?>>();

        //Add filters
        endpoints.add(JWTFilter.class);
        endpoints.add(CORSFilter.class);

        //Add classes with endpoints
        endpoints.add(Main.class);
        endpoints.add(StationController.class);
        endpoints.add(TrainController.class);
        endpoints.add(UserController.class);
        return endpoints;
    }

}
