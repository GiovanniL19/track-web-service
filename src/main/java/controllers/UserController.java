package controllers;

import handlers.CouchDatabase;
import handlers.Parse;
import interfaces.JWTRequired;
import models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.json.JSONObject;

import javax.crypto.KeyGenerator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.Key;
import java.security.Timestamp;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by giovannilenguito on 19/02/2017.
 */
@Path("/users")
public class UserController {
    final private static Logger logger = Logger.getLogger(UserController.class.getName());
    private Parse parse;

    private String buildToken(JSONObject credentials) {
        Key key = MacProvider.generateKey();
        return Jwts.builder().setSubject(credentials.getString("username")).signWith(SignatureAlgorithm.HS512, key).compact();
    }

    @POST
    @Path("/auth")
    public Response authenticate(String data) {

        JSONObject credentials = new JSONObject(data);
        try {
            //Initialise database connection
            CouchDatabase cDb = new CouchDatabase();

            final String username = credentials.getString("username");
            //Get user
            final User user = cDb.getUser(username, null, null);

            if (user == null) {
                return Response.status(Response.Status.OK).entity("Incorrect Username").build();
            } else {
                if (user.getPassword().equals(credentials.getString("password"))) {
                    logger.info("Authentication successful, setting token and sending response...");

                    //Create token
                    final String token = buildToken(credentials);
                    //Build response
                    JSONObject response = new JSONObject();
                    response.put("token", token);
                    response.put("user", new JSONObject(user));

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
            logger.warning(ex.toString());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Incorrect request format").build();
        }
    }

    @POST
    public Response postUser(String data) {
        parse = new Parse();
        CouchDatabase cDb = new CouchDatabase();

        JSONObject requestJson = new JSONObject(data);
        //Parse user
        User newUser = parse.toUser(requestJson);
        if(newUser == null){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not save users").header("Access-Control-Allow-Origin", "*").build();
        }else{
            requestJson.put("id", cDb.postUser(newUser).getId());
            cDb.closeConnection();
            try{
                JSONObject user = new JSONObject();
                user.put("user", requestJson);
                return Response.status(Response.Status.CREATED).entity(user.toString()).header("Access-Control-Allow-Origin", "*").build();
            }catch (Exception ex){
                logger.warning(ex.toString());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("There was an error").header("Access-Control-Allow-Origin", "*").build();
            }
        }
    }

    @GET
    @Path("/check/exists/{email}")
    @Produces("application/json")
    public Response checkEmail(@PathParam("email") String email){
        CouchDatabase cDb = new CouchDatabase();

        final boolean result = cDb.doesEmailExist(email);
        cDb.closeConnection();

        //Create json response
        JSONObject response = new JSONObject();
        response.put("exist", result);

        return Response.ok(response.toString(), MediaType.APPLICATION_JSON).header("Access-Control-Allow-Origin", "*").build();
    }
}
