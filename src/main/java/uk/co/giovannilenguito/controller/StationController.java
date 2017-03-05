package uk.co.giovannilenguito.controller;

import uk.co.giovannilenguito.factory.ParserFactory;
import uk.co.giovannilenguito.helper.DatabaseHelper;
import uk.co.giovannilenguito.helper.SoapRequestHelper;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.soap.SOAPMessage;

/**
 * Created by giovannilenguito on 10/02/2017.
 */
// The Java class will be hosted at the URI path "/stations"
@Path("/stations")
public class StationController {
    final private Logger LOGGER = Logger.getLogger(StationController.class.getName());

    @GET
    @Produces("application/json")
    public Response getAllStations(@QueryParam(value="lng") String lng, @QueryParam(value="lat") String lat) {
        if(lng != null && lat != null){
            LocationController locationController = new LocationController();
            JSONArray stations = locationController.getNearestStation(lng, lat);
            if(stations != null){
                JSONObject response = new JSONObject();
                response.put("stations", stations);
                return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
            }else{
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not retrieve stations").build();
            }
        }else{
            try {
                DatabaseHelper databaseHelper = new DatabaseHelper();

                ParserFactory parserFactory = new ParserFactory();
                JSONObject response = parserFactory.stationToJson(databaseHelper.getAllStations());

                databaseHelper.closeConnection();
                return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
            } catch (Exception ex) {
                LOGGER.warn(ex);
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
            SoapRequestHelper soapRequestHelper = new SoapRequestHelper();
            SOAPMessage soapMessage = soapRequestHelper.createBoardWithDetailsMessage("GetDepBoardWithDetailsRequest", rows, crs.toUpperCase(), "","","","");
            SOAPMessage response = soapRequestHelper.execute(soapMessage);

            ParserFactory parserFactory = new ParserFactory();
            JSONObject json = parserFactory.stationMessage(response, "GetDepBoardWithDetailsResponse");
            return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            LOGGER.warn(ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("There was an error").build();
        }
    }
}
