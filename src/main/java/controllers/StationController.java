package controllers;

import handlers.CouchDatabase;
import handlers.Parse;
import handlers.SoapRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.soap.SOAPMessage;
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
    public Response getAllStations(@QueryParam(value="lng") String lng, @QueryParam(value="lat") String lat) {
        if(lng != null && lat != null){
            return getNearestStation(lng, lat);
        }else{
            try {
                CouchDatabase couchDatabase = new CouchDatabase();

                Parse parse = new Parse();
                JSONObject response = parse.stationToJson(couchDatabase.getAllStations());

                couchDatabase.closeConnection();
                return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
            } catch (Exception ex) {
                logger.warning(ex.toString());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not retrieve stations").build();
            }
        }
    }

    @GET
    @Path("/message")
    @Produces("application/json")
    public Response getMessage(@QueryParam(value="station") String crs) {
        final String rows = "10";
        try {
            SoapRequest soapRequest = new SoapRequest();
            SOAPMessage soapMessage = soapRequest.createBoardWithDetailsMessage("GetDepBoardWithDetailsRequest", rows, crs.toUpperCase(), "","","","");
            SOAPMessage response = soapRequest.execute(soapMessage);

            Parse parse = new Parse();
            JSONObject json = parse.stationMessage(response, "GetDepBoardWithDetailsResponse");
            return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            logger.warning(ex.toString());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("There was an error").build();
        }
    }

    public Response getNearestStation(String longitude, String latitude){
        LocationController locationController = new LocationController();
        JSONArray stations = locationController.getNearestStation(longitude, latitude);
        if(stations == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not retrieve stations or no stations nearby").build();
        }else {
            JSONObject response = new JSONObject();
            response.put("stations", stations);
            return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
        }
    }
}
