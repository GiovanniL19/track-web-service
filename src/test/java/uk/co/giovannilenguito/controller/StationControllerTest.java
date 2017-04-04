package uk.co.giovannilenguito.controller;

import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

        Response responseAllStations = (Response) getAllStations.invoke(stationController, null);


        Response expectedResponse = Response.ok(responseNearbyStations.toString(), MediaType.APPLICATION_JSON).build();

        Assert.assertEquals(expectedResponse.getStatus(), responseNearbyStations.getStatus());
        Assert.assertEquals(expectedResponse.getStatus(), responseAllStations.getStatus());
    }

    @Test
    public void getMessage() throws Exception {
    }

}