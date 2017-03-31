package uk.co.giovannilenguito;

import com.sun.net.httpserver.HttpServer;
import uk.co.giovannilenguito.controller.*;
import uk.co.giovannilenguito.filter.ResponseFilter;
import uk.co.giovannilenguito.filter.RequestFilter;
import org.apache.log4j.Logger;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import uk.co.giovannilenguito.helper.DatabaseHelper;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

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
    final private static Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static HttpServer httpServer;

    public static void main(String[] args) throws IOException {
        ResourceConfig resourceConfig = new ResourceConfig(getEndpoints());

        httpServer = JdkHttpServerFactory.createHttpServer(SERVER_URI, resourceConfig);
        LOGGER.info("Running server on " + SERVER_URI);
        System.out.println("Running server on " + SERVER_URI);
    }


    @GET
    @Produces("text/plain")
    public String getWelcomeMessage() {
        return "Hello, this is the track web service which implements all the logic and CRUD operations.";
    }


    private static Set<Class<?>> getEndpoints(){
        System.out.println("Setting up server");
        LOGGER.info("Setting up server");
        final Set<Class<?>> endpoints = new HashSet<Class<?>>();

        //Add uk.co.giovannilenguito.filter
        endpoints.add(RequestFilter.class);
        endpoints.add(ResponseFilter.class);

        //Add classes with endpoints
        endpoints.add(Main.class);
        endpoints.add(JourneyController.class);
        endpoints.add(StationController.class);
        endpoints.add(TrainController.class);
        endpoints.add(UserController.class);
        return endpoints;
    }

}
