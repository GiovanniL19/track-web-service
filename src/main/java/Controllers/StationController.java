package Controllers;

import Handlers.CouchDatabase;
import Handlers.Parse;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by giovannilenguito on 10/02/2017.
 */
// The Java class will be hosted at the URI path "/station"
@Path("/stations")
public class StationController {
    @GET
    @Produces("application/json")
    public Response getDepBoard() {
        try{
            CouchDatabase cDb = new CouchDatabase();

            Parse parse = new Parse();
            JSONObject response = parse.stationToJson(cDb.getAllStations());

            cDb.closeConnection();
            return Response.ok(response.toString(), MediaType.APPLICATION_JSON).header("Access-Control-Allow-Origin", "*").build();
        }catch(Exception ex){
            System.out.println(ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not retrieve stations").header("Access-Control-Allow-Origin", "*").build();
        }
    }

}
