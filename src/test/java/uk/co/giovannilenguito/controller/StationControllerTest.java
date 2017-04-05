package uk.co.giovannilenguito.controller;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import uk.co.giovannilenguito.factory.ParserFactory;
import uk.co.giovannilenguito.helper.SoapRequestHelper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.soap.SOAPMessage;
import java.lang.reflect.Method;

/**
 * Created by giovannilenguito on 04/04/2017.
 */
public class StationControllerTest {
    @Test
    public void getStations() throws Exception {
        StationController stationController = new StationController();
        Method getNearbyStations = stationController.getClass().getDeclaredMethod("getNearbyStations", String.class, String.class);
        getNearbyStations.setAccessible(true);

        Response responseNearbyStations = (Response) getNearbyStations.invoke(stationController, "51.7435", "0.0212");


        Method getAllStations = stationController.getClass().getDeclaredMethod("getAllStations");
        getAllStations.setAccessible(true);

        Response responseAllStations = (Response) getAllStations.invoke(stationController);


        Response expectedResponse = Response.ok(MediaType.APPLICATION_JSON).build();

        Assert.assertEquals(expectedResponse.getStatus(), responseNearbyStations.getStatus());
        Assert.assertEquals(expectedResponse.getStatus(), responseAllStations.getStatus());
    }

    @Test
    public void getMessage() throws Exception {
        SoapRequestHelper soapRequestHelper = new SoapRequestHelper();
        ParserFactory parserFactory = new ParserFactory();


        SOAPMessage soapMessage = soapRequestHelper.createBoardWithDetailsMessage("GetDepBoardWithDetailsRequest", "10", "EUS", "","","","");
        SOAPMessage response = soapRequestHelper.execute(soapMessage);

        JSONObject json = parserFactory.getStationMessage(response, "GetDepBoardWithDetailsResponse");
        Assert.assertNotNull(json);
    }

}