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

    public UserController() {
        parserFactory = new ParserFactory();
    }

    private String buildToken(final JSONObject credentials) {
        //Build JWT (JSON Web Token)
        return Jwts.builder().setSubject(credentials.getString("username")).signWith(SignatureAlgorithm.HS512, "track").compact();
    }

    @POST
    @Path("/auth")
    public Response authenticate(final String data) {
        databaseHelper = new DatabaseHelper();
        final JSONObject credentials = new JSONObject(data);
        //Authenticate the user credientials
        try {
            final String username = credentials.getString("username");
            //Get user
            User user = databaseHelper.getUser(username, null, null);

            //If no user found, user will be null
            if (user == null) {
                return Response.status(Response.Status.OK).entity("Incorrect Username").build();
            } else {
                //Check user password matches
                if (user.getPassword().equals(credentials.getString("password"))) {
                    LOGGER.info("Authentication successful, setting token and sending response...");

                    //Save last login
                    final int dateTime = (int) (new Date().getTime() / 1000);
                    user.setLastLogin(dateTime);
                    databaseHelper.putUser(user);

                    //Create token
                    final String token = buildToken(credentials);

                    return Response.ok(parserFactory.toToken(token, user), MediaType.APPLICATION_JSON).build();
                } else {
                    databaseHelper.closeConnection();
                    return Response.status(Response.Status.OK).entity("Incorrect Password").build();
                }
            }
        } catch (Exception ex) {
            LOGGER.warn(ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Incorrect request format").build();
        } finally {
            databaseHelper.closeConnection();
        }
    }

    @GET
    @Path("/{id}")
    @JWTRequired
    public Response getUser(@PathParam("id") final String id) {
        databaseHelper = new DatabaseHelper();

        //Get user
        try {
            User foundUser = databaseHelper.getUser(null, null, id);

            if (foundUser == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            } else {
                foundUser.setId(id);
                foundUser.setRev(foundUser.get_rev());

                return Response.status(Response.Status.OK).entity(parserFactory.userResponse(foundUser)).build();
            }
        } catch (Exception ex) {
            LOGGER.warn(ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not get user").build();
        } finally {
            databaseHelper.closeConnection();
        }
    }

    @PUT
    @Path("{id}")
    public Response putUser(String data, @PathParam("id") final String id) {
        JSONObject user = new JSONObject(data);
        user.put("id", id);

        databaseHelper = new DatabaseHelper();
        User userObject = parserFactory.toUser(user, id);

        //Get user
        User foundUser = databaseHelper.getUser(null, null, id);

        //Set latest rev (revision)
        userObject.set_rev(foundUser.get_rev());
        userObject.setRev(foundUser.getRev());

        String rev = databaseHelper.putUser(userObject);
        databaseHelper.closeConnection();

        if (!rev.equals(userObject.getRev())) {
            userObject.set_rev(rev);
            userObject.setRev(rev);

            return Response.status(Response.Status.OK).entity(parserFactory.userResponse(userObject)).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unable to update user").build();
        }

    }

    @DELETE
    @Path("{id}")
    public Response deleteUser(@PathParam("id") final String id) {
        databaseHelper = new DatabaseHelper();

        final User user = databaseHelper.getUser(null, null, id);
        final org.lightcouch.Response response = databaseHelper.deleteUser(user);

        databaseHelper.closeConnection();
        if (response.getError() == null) {
            return Response.status(Response.Status.OK).entity("{}").build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response.getError()).build();
        }
    }

    @POST
    public Response postUser(final String data) {
        databaseHelper = new DatabaseHelper();

        final JSONObject requestJson = new JSONObject(data);

        //Parse json user to POJO
        User newUser = parserFactory.toUser(requestJson, null);
        if (newUser == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not save user").build();
        } else {
            //Save user
            final String id = databaseHelper.postUser(newUser).getId();
            newUser.set_id(id);
            newUser.setId(id);

            try {
                return Response.status(Response.Status.CREATED).entity(parserFactory.userResponse(newUser)).build();
            } catch (Exception ex) {
                LOGGER.warn(ex);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("There was an error").build();
            } finally {
                databaseHelper.closeConnection();
            }
        }
    }

    @GET
    @Path("/check/exists/{email}")
    @Produces("application/json")
    public Response checkEmail(@PathParam("email") final String email) {
        databaseHelper = new DatabaseHelper();

        final boolean result = databaseHelper.doesEmailExist(email);
        databaseHelper.closeConnection();

        return Response.ok(parserFactory.existResponse(result), MediaType.APPLICATION_JSON).build();
    }
}
