package controllers;

import handlers.CouchDatabase;
import handlers.Parse;
import models.Station;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * Created by giovannilenguito on 10/02/2017.
 */
// The Java class will be hosted at the URI path "/station"
@Path("/stations")
public class StationController {
    final private static Logger logger = Logger.getLogger(StationController.class.getName());

    @GET
    @Produces("application/json")
    public Response getAllStations() {

        try{
            CouchDatabase cDb = new CouchDatabase();

            Parse parse = new Parse();
            JSONObject response = parse.stationToJson(cDb.getAllStations());

            cDb.closeConnection();
            return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
        }catch(Exception ex){
            logger.warning(ex.toString());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not retrieve stations").build();
        }
    }
}
