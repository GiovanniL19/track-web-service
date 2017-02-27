package controllers;

import handlers.GeoLocation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * Created by giovannilenguito on 10/02/2017.
 */
// The Java class will be hosted at the URI path "/contexts"
@Path("/contexts")
public class ContextController {
    final private static Logger logger = Logger.getLogger(ContextController.class.getName());

    @POST
    @Produces("application/json")
    public Response getMessage(@QueryParam(value="station") String crs) {
        GeoLocation geoLocation = new GeoLocation();
        //TODO: Get actualy long and lat values
        String city = geoLocation.getCity("53.010151799999996", "-2.1804978");
        //TODO: Create context POJO and save to CouchDB, then send the object created in response

        return Response.ok("Saved context", MediaType.APPLICATION_JSON).build();
    }


}
