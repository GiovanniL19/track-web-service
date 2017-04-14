package uk.co.giovannilenguito.controller;

import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Created by giovannilenguito on 04/04/2017.
 */
public class TrainControllerTest {
    @Test
    public void getTrains() throws Exception {
        TrainController trainController = new TrainController();

        Method getDepartureBoard = trainController.getClass().getDeclaredMethod("getDepartureBoard", String.class);
        if(!getDepartureBoard.isAccessible()){
            getDepartureBoard.setAccessible(true);
        }

        Method getArrivalBoard = trainController.getClass().getDeclaredMethod("getArrivalBoard", String.class);
        if(!getArrivalBoard.isAccessible()){
            getArrivalBoard.setAccessible(true);
        }

        Method findTrains = trainController.getClass().getDeclaredMethod("findTrains", String.class, String.class, String.class);
        if(!findTrains.isAccessible()){
            findTrains.setAccessible(true);
        }

        Response departureBoardResponse = (Response) getDepartureBoard.invoke(trainController, "EUS");
        Response arrivalBoardResponse = (Response) getArrivalBoard.invoke(trainController, "EUS");
        Response findTrainsResponse = (Response) findTrains.invoke(trainController, "EUS", "LIV", "10");

        Response expectedResponse = Response.ok(MediaType.APPLICATION_JSON).build();


        Assert.assertEquals(expectedResponse.getStatus(), departureBoardResponse .getStatus());
        Assert.assertEquals(expectedResponse.getStatus(), arrivalBoardResponse .getStatus());
        Assert.assertEquals(expectedResponse.getStatus(), findTrainsResponse .getStatus());
    }
}