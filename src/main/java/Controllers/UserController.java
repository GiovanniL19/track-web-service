package Controllers;

import Handlers.CouchDatabase;
import Handlers.Parse;
import Models.User;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Created by giovannilenguito on 19/02/2017.
 */
@Path("/users")
public class UserController {
    private CouchDatabase cDb;
    private Parse parse;

    @POST
    public Response postUser(String data) {
        parse = new Parse();
        cDb = new CouchDatabase();

        //Parse user
        User newUser = parse.toUser(data);
        if(newUser == null){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not save users").header("Access-Control-Allow-Origin", "*").build();
        }else{
            return Response.status(Response.Status.CREATED).entity(cDb.postUser(newUser).getId()).header("Access-Control-Allow-Origin", "*").build();
        }
    }
}
