package controllers;

import handlers.Parse;
import handlers.SoapRequest;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.soap.SOAPMessage;

/**
 * Created by giovannilenguito on 10/02/2017.
 */
// The Java class will be hosted at the URI path "/station"
@Path("/trains")
public class TrainController {
    @GET
    @Produces("application/json")
    public Response getTrains(@QueryParam(value="origin") String crs, @QueryParam("destination") String filterCrs, @DefaultValue("10") @QueryParam("rows") String rows) {
        try {
            //Initialise instance
            SoapRequest soapRequest = new SoapRequest();

            //Create message
            SOAPMessage message = soapRequest.createBoardWithDetailsMessage("GetDepBoardWithDetailsRequest", rows, crs.toUpperCase(), filterCrs.equalsIgnoreCase("*") ? "" : filterCrs.toUpperCase(),"","","");

            //Get data from national rail
            SOAPMessage response = soapRequest.execute(message);

            //Parse
            Parse parse = new Parse();
            JSONObject json = parse.boardServices(response, "GetDepBoardWithDetailsResponse");
            return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            //ex.printStackTrace();

            String message;
            if(ex.getMessage().equals("JSONObject[\"lt5:trainServices\"] not found.")) {
                message = "No Services Running";
            }else if(ex.getMessage().equals("JSONObject[\"GetDepBoardWithDetailsResponse\"] not found.")){
                message = "";
            }else{
                message = ex.getMessage();
            }

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }
}
