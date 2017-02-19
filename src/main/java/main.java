import com.sun.net.httpserver.HttpServer;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
/**
 * Created by giovannilenguito on 09/02/2017.
 */
// The Java class will be hosted at the URI path "/"
@Path("/")
public class main {
    @GET
    @Produces("text/plain")
    public String getMessage() {
        return "Hello, this is the track web service which implements all the logic and CRUD operations.";
    }

    public static void main(String[] args) throws IOException {
        //Set up the http server
        HttpServer server = HttpServerFactory.create("http://localhost:3002/");

        server.start();

        //Print messages
        System.out.println("Server running");
        System.out.println("Visit: http://localhost:3002/");
        System.out.println("Hit return to stop...");
        System.in.read();
        System.out.println("Stopping server");
        server.stop(0);
        System.out.println("Server stopped");
    }
}
