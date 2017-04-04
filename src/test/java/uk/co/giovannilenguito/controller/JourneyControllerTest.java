package uk.co.giovannilenguito.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import uk.co.giovannilenguito.helper.DatabaseHelper;
import uk.co.giovannilenguito.model.Journey;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by giovannilenguito on 04/04/2017.
 */
public class JourneyControllerTest {


    @Test
    public void createJourney() throws Exception {
        JourneyController journeyController = new JourneyController();

        Journey journey = new Journey();
        journey.setType("journey");

        //Reflection
        Method getHour = journeyController.getClass().getDeclaredMethod("getHourOfDay", null);
        getHour.setAccessible(true);

        int hourOfDay = (int) getHour.invoke(journeyController, null);

        journey.setHour(hourOfDay);

        //Reflection
        Method getDayOfWeek = journeyController.getClass().getDeclaredMethod("getDayOfWeek", null);
        getDayOfWeek.setAccessible(true);

        String dayOfWeek = (String) getDayOfWeek.invoke(journeyController, null);
        journey.setDay(dayOfWeek);


        //Change expected result accordingly
        Assert.assertEquals(11, journey.getHour());
        Assert.assertEquals("Tuesday", journey.getDay());

    }

    @Test
    public void getRecommendations() throws Exception {
        RecommendationController recommendationController = new RecommendationController();

        JSONArray journeys = recommendationController.getTodayByUser("null", "Bonhill Street", 10, "Friday");
        Assert.assertEquals(1, journeys.length());
    }

    @Test
    public void getJourney() throws Exception {
        DatabaseHelper databaseHelper = new DatabaseHelper();
        Journey journey = databaseHelper.getJourney("7a4dcfce647e44d9800707f8a7085861", null);
        Assert.assertEquals("Bonhill Street", journey.getCity());
    }

    @Test
    public void checkJourneyExistence() throws Exception {
        DatabaseHelper databaseHelper = new DatabaseHelper();
        boolean exists = false;
        String key = "London Euston" + "Stoke-on-Trent" + "288b922dabaf400091cdc29155aef5d6";
        Journey journey = databaseHelper.getJourney(null, key);
        if(journey != null){
            exists = true;
        }
        Assert.assertEquals(true, exists);
    }
}