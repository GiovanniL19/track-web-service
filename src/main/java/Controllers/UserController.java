package Controllers;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Created by giovannilenguito on 19/02/2017.
 */
@Path("/users")
public class UserController {
    @POST
    public Response getClichedMessage() {
        System.out.println("hello");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not save users").header("Access-Control-Allow-Origin", "*").build();
    }
}
