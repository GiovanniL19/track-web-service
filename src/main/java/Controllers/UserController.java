package Controllers;

import Handlers.CouchDatabase;
import Handlers.Parse;
import Models.User;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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

        JSONObject requestJson = new JSONObject(data);
        //Parse user
        User newUser = parse.toUser(requestJson);
        if(newUser == null){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not save users").header("Access-Control-Allow-Origin", "*").build();
        }else{
            requestJson.put("id", cDb.postUser(newUser).getId());
            try{
                String id = requestJson.getString("id");
                JSONObject user = new JSONObject();
                user.put("user", requestJson);
                return Response.status(Response.Status.CREATED).entity(user.toString()).header("Access-Control-Allow-Origin", "*").build();
            }catch (Exception ex){
                System.out.println(ex);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("There was an error").header("Access-Control-Allow-Origin", "*").build();
            }
        }
    }

    @GET
    @Path("/check/exists/{email}")
    @Produces("application/json")
    public Response checkEmail(@PathParam("email") String email){
        cDb = new CouchDatabase();

        boolean result = false;
        result = cDb.doesEmailExist(email);

        //Create json response
        JSONObject response = new JSONObject();
        response.put("exist", result);

        return Response.ok(response.toString(), MediaType.APPLICATION_JSON).header("Access-Control-Allow-Origin", "*").build();
    }
}
