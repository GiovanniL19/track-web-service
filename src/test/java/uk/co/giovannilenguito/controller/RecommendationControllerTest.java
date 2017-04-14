package uk.co.giovannilenguito.controller;

import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;
import uk.co.giovannilenguito.helper.DatabaseHelper;
import uk.co.giovannilenguito.model.Journey;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by giovannilenguito on 04/04/2017.
 */
public class RecommendationControllerTest {
    @Test
    public void getTodayByUser() throws Exception {
        DatabaseHelper databaseHelper = new DatabaseHelper();
        RecommendationController recommendationController = new RecommendationController();

        Method sortArray = recommendationController.getClass().getDeclaredMethod("sortArray", List.class);
        sortArray.setAccessible(true);

        //Get from couchdb all documents with match user id, city and hour key
        List<Journey> journeys = databaseHelper.getAllJourneysByKey("288b922dabaf400091cdc29155aef5d6", "Bonhill Street", 10, "Friday");

        //Sort array high count to low
        journeys = (List<Journey>) sortArray.invoke(recommendationController, journeys);

        databaseHelper.closeConnection();

        Method buildResponse = recommendationController.getClass().getDeclaredMethod("buildResponse", int.class, List.class);
        if(!buildResponse.isAccessible()){
            buildResponse.setAccessible(true);
        }

        JSONArray response = (JSONArray) buildResponse.invoke(recommendationController, 5, journeys);
        Assert.assertEquals(1, response.length());
    }

}