package uk.co.giovannilenguito.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import uk.co.giovannilenguito.helper.DatabaseHelper;
import uk.co.giovannilenguito.model.Journey;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
        Method getHour = journeyController.getClass().getDeclaredMethod("getHourOfDay");
        if(!getHour.isAccessible()){
            getHour.setAccessible(true);
        }

        int hourOfDay = (int) getHour.invoke(journeyController);

        journey.setHour(hourOfDay);

        //Reflection
        Method getDayOfWeek = journeyController.getClass().getDeclaredMethod("getDayOfWeek");
        if(!getDayOfWeek.isAccessible()){
            getDayOfWeek.setAccessible(true);
        }

        String dayOfWeek = (String) getDayOfWeek.invoke(journeyController);
        journey.setDay(dayOfWeek);


        //Change expected result accordingly
        int hour = LocalDateTime.now().getHour();
        String hourFormatted = String.valueOf(hour).split(":")[0];
        int expectedHour = Integer.parseInt(hourFormatted);

        Calendar calendar = Calendar.getInstance();
        String today = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        Assert.assertEquals(expectedHour, journey.getHour());
        Assert.assertEquals(today, journey.getDay());

    }

    @Test
    public void getRecommendations() throws Exception {
        RecommendationController recommendationController = new RecommendationController();

        JSONArray journeys = recommendationController.getToday("null", "Bonhill Street", 10, "Friday", false);
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
        Assert.assertEquals(false, exists);
    }
}