package uk.co.giovannilenguito.controller;

import uk.co.giovannilenguito.factory.ParserFactory;
import uk.co.giovannilenguito.helper.SoapRequestHelper;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.soap.SOAPMessage;

/**
 * Created by giovannilenguito on 10/02/2017.
 */
// The Java class will be hosted at the URI path "/trains"
@Path("/trains")
public class TrainController {
    final private Logger LOGGER = Logger.getLogger(TrainController.class.getName());
    final private String ROWS = "10";
    private ParserFactory parserFactory;
    private SoapRequestHelper soapRequestHelper;

    @GET
    public Response getTrains(@QueryParam(value="origin") String crs, @QueryParam("destination") String filterCrs, @DefaultValue("10") @QueryParam("rows") String rows, @DefaultValue("") @QueryParam("type") String type, @DefaultValue("") @QueryParam("location") String location, @QueryParam("lng") String lng, @QueryParam("lat") String lat,  @QueryParam("user") String userID) {
        if(type.equalsIgnoreCase("departure")){
            return getDepartureBoard(location);
        }else if(type.equalsIgnoreCase("arrival")){
            return getArrivalBoard(location);
        }else{
            //Create context
            if(!lng.equals("") && !lat.equals("")) {
                LOGGER.info("Saving context");
                JourneyController journeyController = new JourneyController();
                journeyController.createJourney(lng, lat, crs, filterCrs, userID);
            }
            //Return trains
            return findTrains(crs, filterCrs, rows);
        }
    }

    public Response findTrains(String crs, String filterCrs, String rows){
        parserFactory = new ParserFactory();

        try {
            //Initialise instance
            soapRequestHelper = new SoapRequestHelper();

            //Create message
            SOAPMessage message = soapRequestHelper.createBoardWithDetailsMessage("GetDepBoardWithDetailsRequest", rows, crs.toUpperCase(), filterCrs.equalsIgnoreCase("*") ? "" : filterCrs.toUpperCase(), "", "", "");

            //Get data from national rail
            SOAPMessage response = soapRequestHelper.execute(message);

            JSONObject json = parserFactory.departureBoardServices(response, "GetDepBoardWithDetailsResponse");
            return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            String message = parserFactory.errorMessage(ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    public Response getDepartureBoard(String crs){
        parserFactory = new ParserFactory();

        try {
            //Initialise instance
            soapRequestHelper = new SoapRequestHelper();

            //Create message
            SOAPMessage message = soapRequestHelper.createBoardWithDetailsMessage("GetDepBoardWithDetailsRequest", ROWS, crs.toUpperCase(), "","","","");

            //Get data from national rail
            SOAPMessage response = soapRequestHelper.execute(message);

            JSONObject json = parserFactory.departureBoardServices(response, "GetDepBoardWithDetailsResponse");
            return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            LOGGER.warn(ex);
            String message = parserFactory.errorMessage(ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    public Response getArrivalBoard(String crs){
        parserFactory = new ParserFactory();
        try {
            //Initialise instance
            soapRequestHelper = new SoapRequestHelper();

            //Create message
            SOAPMessage message = soapRequestHelper.createBoardWithDetailsMessage("GetArrBoardWithDetailsRequest", ROWS, crs.toUpperCase(), "","","","");

            //Get data from national rail
            SOAPMessage response = soapRequestHelper.execute(message);

            JSONObject json = parserFactory.arrivalBoardServices(response, "GetArrBoardWithDetailsResponse");
            return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            LOGGER.warn(ex);
            String message = parserFactory.errorMessage(ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }
}
