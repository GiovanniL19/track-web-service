package Controllers;

import Handlers.Parse;
import Handlers.SoapRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.soap.SOAPMessage;

/**
 * Created by giovannilenguito on 10/02/2017.
 */
// The Java class will be hosted at the URI path "/station"
@Path("/station")
public class Station {
    @GET
    @Path("/getDepartureBoard/{origin}/{des}/{rows}")
    @Produces("application/json")
    public Response getDepBoard(@PathParam("origin") String crs, @PathParam("des") String filterCrs, @PathParam("rows") String rows) {
        try {
            //Initialise instance
            SoapRequest soapRequest = new SoapRequest();

            //Create message
            SOAPMessage message = soapRequest.createBoardWithDetailsMessage(rows, crs.toUpperCase(), filterCrs.equalsIgnoreCase("*") ? "" : filterCrs.toUpperCase(),"","","");

            //Get data from national rail
            SOAPMessage response = soapRequest.execute(message);

            //Parse
            Parse parse = new Parse();
            if(rows.equals("0") || rows.equals("1")){
                JSONObject json = parse.boardWithASingleService(response, "GetDepBoardWithDetailsResponse");
                return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
            }else{
                JSONArray json = parse.boardWithMultipleServices(response, "GetDepBoardWithDetailsResponse");
                return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("500: Server Error, please check origin and destination is correct").build();
        }
    }
}
