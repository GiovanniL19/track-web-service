package Controllers;

import Handlers.Parse;
import Handlers.SoapRequest;
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
public class Trains {
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
            return Response.ok(json.toString(), MediaType.APPLICATION_JSON).header("Access-Control-Allow-Origin", "*").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("500: Server Error, please check origin and destination is correct").header("Access-Control-Allow-Origin", "*").build();
        }
    }
}
