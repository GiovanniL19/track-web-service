package controllers;

import handlers.Parse;
import handlers.SoapRequest;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.soap.SOAPMessage;
import java.util.logging.Logger;

/**
 * Created by giovannilenguito on 10/02/2017.
 */
// The Java class will be hosted at the URI path "/trains"
@Path("/trains")
public class TrainController {
    final private static Logger logger = Logger.getLogger(StationController.class.getName());

    @GET
    @Produces("application/json")
    public Response getTrains(@QueryParam(value="origin") String crs, @QueryParam("destination") String filterCrs, @DefaultValue("10") @QueryParam("rows") String rows, @DefaultValue("") @QueryParam("type") String type, @DefaultValue("") @QueryParam("location") String location, @QueryParam("lng") String lng, @QueryParam("lat") String lat,  @QueryParam("user") String userID) {
        if(type.equalsIgnoreCase("departure")){
            return getDepartureBoard(location);
        }else if(type.equalsIgnoreCase("arrival")){
            return getArrivalBoard(location);
        }else{
            //Create context
            if(!lng.equals("") && !lat.equals("")) {
                logger.info("Saving context");
                JourneyController journeyController = new JourneyController();
                journeyController.createJourney(lng, lat, crs, filterCrs, userID);
            }
            //Return trains
            return findTrains(crs, filterCrs, rows);
        }
    }

    public Response findTrains(String crs, String filterCrs, String rows){
        try {
            //Initialise instance
            SoapRequest soapRequest = new SoapRequest();

            //Create message
            SOAPMessage message = soapRequest.createBoardWithDetailsMessage("GetDepBoardWithDetailsRequest", rows, crs.toUpperCase(), filterCrs.equalsIgnoreCase("*") ? "" : filterCrs.toUpperCase(), "", "", "");

            //Get data from national rail
            SOAPMessage response = soapRequest.execute(message);

            //Parse
            Parse parse = new Parse();
            JSONObject json = parse.departureBoardServices(response, "GetDepBoardWithDetailsResponse");
            return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            String message = getErrorMessage(ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    public Response getDepartureBoard(String crs){
        final String rows = "10";
        try {
            //Initialise instance
            SoapRequest soapRequest = new SoapRequest();

            //Create message
            SOAPMessage message = soapRequest.createBoardWithDetailsMessage("GetDepBoardWithDetailsRequest", rows, crs.toUpperCase(), "","","","");

            //Get data from national rail
            SOAPMessage response = soapRequest.execute(message);

            //Parse
            Parse parse = new Parse();
            JSONObject json = parse.departureBoardServices(response, "GetDepBoardWithDetailsResponse");
            return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            String message = getErrorMessage(ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    public Response getArrivalBoard(String crs){
        final String rows = "10";
        try {
            //Initialise instance
            SoapRequest soapRequest = new SoapRequest();

            //Create message
            SOAPMessage message = soapRequest.createBoardWithDetailsMessage("GetArrBoardWithDetailsRequest", rows, crs.toUpperCase(), "","","","");

            //Get data from national rail
            SOAPMessage response = soapRequest.execute(message);

            //Parse
            Parse parse = new Parse();
            JSONObject json = parse.arrivalBoardServices(response, "GetArrBoardWithDetailsResponse");
            return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            String message = getErrorMessage(ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    private String getErrorMessage(Exception ex){
        logger.warning(ex.toString());

        String message;
        if(ex.getMessage().equals("JSONObject[\"lt5:trainServices\"] not found.")) {
            message = "No Services Running";
        }else if(ex.getMessage().equals("JSONObject[\"GetDepBoardWithDetailsResponse\"] not found.")){
            message = "";
        }else{
            message = ex.getMessage();
        }

        return message;
    }
}
