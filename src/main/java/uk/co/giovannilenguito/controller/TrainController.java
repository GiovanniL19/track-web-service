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

    private Response findTrains(final String crs, final String filterCrs, final String rows) {
        parserFactory = new ParserFactory();

        try {
            //Initialise instance
            soapRequestHelper = new SoapRequestHelper();

            //Create message
            final SOAPMessage message = soapRequestHelper.createBoardWithDetailsMessage("GetDepBoardWithDetailsRequest", rows, crs.toUpperCase(), filterCrs.equalsIgnoreCase("*") ? "" : filterCrs.toUpperCase(), "", "", "");

            //Get data from national rail
            final SOAPMessage response = soapRequestHelper.execute(message);

            final JSONObject json = parserFactory.departureBoardServices(response, "GetDepBoardWithDetailsResponse");
            if(json != null) {
                return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
            }else{
                return Response.ok("{trains:[]}", MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception ex) {
            final String message = parserFactory.errorMessage(ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    private Response getDepartureBoard(final String crs) {
        parserFactory = new ParserFactory();

        try {
            //Initialise instance
            soapRequestHelper = new SoapRequestHelper();

            //Create message
            SOAPMessage message = soapRequestHelper.createBoardWithDetailsMessage("GetDepBoardWithDetailsRequest", ROWS, crs.toUpperCase(), "", "", "", "");

            //Get data from national rail
            SOAPMessage response = soapRequestHelper.execute(message);

            JSONObject json = parserFactory.departureBoardServices(response, "GetDepBoardWithDetailsResponse");

            if(json != null) {
                return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
            }else{
                return Response.ok("{trains:[]}", MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception ex) {
            LOGGER.warn(ex);
            String message = parserFactory.errorMessage(ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    private Response getArrivalBoard(final String crs) {
        parserFactory = new ParserFactory();
        try {
            //Initialise instance
            soapRequestHelper = new SoapRequestHelper();

            //Create message
            SOAPMessage message = soapRequestHelper.createBoardWithDetailsMessage("GetArrBoardWithDetailsRequest", ROWS, crs.toUpperCase(), "", "", "", "");

            //Get data from national rail
            SOAPMessage response = soapRequestHelper.execute(message);

            JSONObject json = parserFactory.arrivalBoardServices(response, "GetArrBoardWithDetailsResponse");
            if(json != null) {
                return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
            }else{
                return Response.ok("{trains:[]}", MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception ex) {
            LOGGER.warn(ex);
            String message = parserFactory.errorMessage(ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

    @GET
    public Response getTrains(@QueryParam(value = "origin") final String crs,
                              @QueryParam("destination") final String filterCrs,
                              @DefaultValue("10") @QueryParam("rows") final String rows,
                              @DefaultValue("") @QueryParam("type") final String type,
                              @DefaultValue("") @QueryParam("location") final String location,
                              @QueryParam("lng") final String lng,
                              @QueryParam("lat") final String lat,
                              @QueryParam("user") final String userID) {

        if (type.equalsIgnoreCase("departure")) {
            //Get departure board
            return getDepartureBoard(location);
        } else if (type.equalsIgnoreCase("arrival")) {
            //Get arrival board
            return getArrivalBoard(location);
        } else {
            //Create journey for recommendations
            if (!lng.equals("") && !lat.equals("")) {
                final JourneyController journeyController = new JourneyController();
                journeyController.createJourney(lng, lat, crs, filterCrs, userID);
            }

            //Return trains
            return findTrains(crs, filterCrs, rows);
        }
    }
}
