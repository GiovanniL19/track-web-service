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

    private String buildToken(JSONObject credentials) {
        return Jwts.builder().setSubject(credentials.getString("username")).signWith(SignatureAlgorithm.HS512, "track").compact();
    }

    @POST
    @Path("/auth")
    public Response authenticate(String data) {
        JSONObject credentials = new JSONObject(data);
        try {
            //Initialise database connection
            DatabaseHelper cDb = new DatabaseHelper();

            final String username = credentials.getString("username");
            //Get user
            final User user = cDb.getUser(username, null, null);

            if (user == null) {
                return Response.status(Response.Status.OK).entity("Incorrect Username").build();
            } else {
                if (user.getPassword().equals(credentials.getString("password"))) {
                    LOGGER.info("Authentication successful, setting token and sending response...");

                    //Create token
                    final String token = buildToken(credentials);
                    //Build response
                    JSONObject response = new JSONObject();
                    response.put("token", token);
                    response.put("user", user.get_id());

                    //Save last login
                    int dateTime = (int) (new Date().getTime()/1000);
                    user.setLastLogin(dateTime);
                    cDb.putUser(user);
                    //Close connection to couchdb
                    cDb.closeConnection();
                    return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
                }else{
                    //Close connection to couchdb
                    cDb.closeConnection();
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
        DatabaseHelper databaseHelper = new DatabaseHelper();
        try {
            User foundUser = databaseHelper.getUser(null, null, id);

            if(foundUser == null){
                return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            }else{
                foundUser.setId(id);
                foundUser.setRev(foundUser.get_rev());

                JSONObject userJson = new JSONObject(foundUser);

                JSONObject user = new JSONObject();
                user.put("user", userJson);
                return Response.status(Response.Status.OK).entity(user.toString()).build();
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
        DatabaseHelper databaseHelper = new DatabaseHelper();
        ParserFactory parserFactory = new ParserFactory();

        User userObject = parserFactory.toUser(user, id);

        String rev = databaseHelper.putUser(userObject);
        if(rev != null){
            userObject.set_rev(rev);
            userObject.setRev(rev);

            JSONObject response = new JSONObject();
            JSONObject updatedUser = new JSONObject(userObject);
            response.put("user", updatedUser);
            databaseHelper.closeConnection();
            return Response.status(Response.Status.OK).entity(response.toString()).build();
        }else{
            databaseHelper.closeConnection();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unable to update user").build();
        }

    }

    @DELETE
    @Path("{id}")
    public Response deleteUser(@PathParam("id") String id) {
        DatabaseHelper databaseHelper = new DatabaseHelper();

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
        parserFactory = new ParserFactory();
        DatabaseHelper databaseHelper = new DatabaseHelper();

        JSONObject requestJson = new JSONObject(data);
        //ParserFactory user
        User newUser = parserFactory.toUser(requestJson, null);
        if(newUser == null){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not save user").build();
        }else{
            requestJson.put("id", databaseHelper.postUser(newUser).getId());
            databaseHelper.closeConnection();
            try{
                JSONObject user = new JSONObject();
                user.put("user", requestJson);
                return Response.status(Response.Status.CREATED).entity(user.toString()).build();
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
        DatabaseHelper databaseHelper = new DatabaseHelper();

        final boolean result = databaseHelper.doesEmailExist(email);
        databaseHelper.closeConnection();

        //Create json response
        JSONObject response = new JSONObject();
        response.put("exist", result);

        return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
    }
}
