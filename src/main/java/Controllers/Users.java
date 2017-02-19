package Controllers;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by giovannilenguito on 19/02/2017.
 */
// The Java class will be hosted at the URI path "/users"
@Path("/users")
public class Users {

    @POST
    @Produces("application/json")
    public Response postUser() {
        System.out.println("here");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{'message': 'here'}").header("Access-Control-Allow-Origin", "*").build();
    }
}
