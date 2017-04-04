package uk.co.giovannilenguito.controller;

import uk.co.giovannilenguito.annotation.JWTRequired;
import uk.co.giovannilenguito.factory.ParserFactory;
import uk.co.giovannilenguito.helper.DatabaseHelper;
import uk.co.giovannilenguito.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

/**
 * Created by giovannilenguito on 19/02/2017.
 */
@Path("/users")
public class UserController {
    final private Logger LOGGER = Logger.getLogger(UserController.class.getName());
    private ParserFactory parserFactory;
    private DatabaseHelper databaseHelper;

    private String buildToken(JSONObject credentials) {
        return Jwts.builder().setSubject(credentials.getString("username")).signWith(SignatureAlgorithm.HS512, "track").compact();
    }

    public UserController() {
        parserFactory = new ParserFactory();
    }

    @POST
    @Path("/auth")
    public Response authenticate(String data) {
        JSONObject credentials = new JSONObject(data);
        try {
            //Initialise database connection
            databaseHelper = new DatabaseHelper();

            final String username = credentials.getString("username");
            //Get user
            final User user = databaseHelper.getUser(username, null, null);

            if (user == null) {
                return Response.status(Response.Status.OK).entity("Incorrect Username").build();
            } else {
                if (user.getPassword().equals(credentials.getString("password"))) {
                    LOGGER.info("Authentication successful, setting token and sending response...");

                    //Save last login
                    int dateTime = (int) (new Date().getTime()/1000);
                    user.setLastLogin(dateTime);
                    databaseHelper.putUser(user);
                    databaseHelper.closeConnection();

                    //Create token
                    final String token = buildToken(credentials);

                    return Response.ok(parserFactory.toToken(token, user), MediaType.APPLICATION_JSON).build();
                }else{
                    databaseHelper.closeConnection();
                    return Response.status(Response.Status.OK).entity("Incorrect Password").build();
                }
            }
        }catch (Exception ex){
            LOGGER.warn(ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Incorrect request format").build();
        }
    }

    @GET
    @Path("/{id}")
    @JWTRequired
    public Response getUser(@PathParam("id") String id) {
        databaseHelper = new DatabaseHelper();
        try {
            User foundUser = databaseHelper.getUser(null, null, id);

            if(foundUser == null){
                return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            }else{
                foundUser.setId(id);
                foundUser.setRev(foundUser.get_rev());

                return Response.status(Response.Status.OK).entity(parserFactory.userResponse(foundUser)).build();
            }
        }catch(Exception ex){
            LOGGER.info("FAILED");
            LOGGER.warn(ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not get user").build();
        }finally {
            databaseHelper.closeConnection();
        }
    }

    @PUT
    @Path("{id}")
    public Response putUser(String data, @PathParam("id") String id) {
        JSONObject user = new JSONObject(data);
        databaseHelper = new DatabaseHelper();

        User userObject = parserFactory.toUser(user, id);

        String rev = databaseHelper.putUser(userObject);
        if(rev != null){
            userObject.set_rev(rev);
            userObject.setRev(rev);

            return Response.status(Response.Status.OK).entity(parserFactory.userResponse(userObject)).build();
        }else{
            databaseHelper.closeConnection();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unable to update user").build();
        }

    }

    @DELETE
    @Path("{id}")
    public Response deleteUser(@PathParam("id") String id) {
        databaseHelper = new DatabaseHelper();

        User user = databaseHelper.getUser(null, null, id);
        org.lightcouch.Response response = databaseHelper.deleteUser(user);
        databaseHelper.closeConnection();
        if(response.getError() == null){
            return Response.status(Response.Status.OK).entity("{}").build();
        }else{
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response.getError()).build();
        }
    }

    @POST
    public Response postUser(String data) {
        databaseHelper = new DatabaseHelper();

        JSONObject requestJson = new JSONObject(data);
        //ParserFactory user
        User newUser = parserFactory.toUser(requestJson, null);
        if(newUser == null){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not save user").build();
        }else{
            String id = databaseHelper.postUser(newUser).getId();
            newUser.set_id(id);
            newUser.setId(id);

            databaseHelper.closeConnection();
            try{
                return Response.status(Response.Status.CREATED).entity(parserFactory.userResponse(newUser)).build();
            }catch (Exception ex){
                LOGGER.warn(ex);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("There was an error").build();
            }
        }
    }

    @GET
    @Path("/check/exists/{email}")
    @Produces("application/json")
    public Response checkEmail(@PathParam("email") String email){
        databaseHelper = new DatabaseHelper();

        final boolean result = databaseHelper.doesEmailExist(email);
        databaseHelper.closeConnection();

        return Response.ok(parserFactory.existResponse(result), MediaType.APPLICATION_JSON).build();
    }
}
