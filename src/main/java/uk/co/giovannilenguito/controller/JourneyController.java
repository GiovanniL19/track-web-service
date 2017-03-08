package uk.co.giovannilenguito.controller;

import uk.co.giovannilenguito.helper.DatabaseHelper;
import uk.co.giovannilenguito.model.Journey;
import uk.co.giovannilenguito.model.User;
import org.apache.log4j.Logger;

import javax.ejb.Asynchronous;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by giovannilenguito on 10/02/2017.
 */

@Asynchronous
public class JourneyController {
    final private Logger LOGGER = Logger.getLogger(JourneyController.class.getName());

    private String getDayOfWeek(int day){
        switch (day) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return null;
        }
    }

    public void createJourney(String lng, String lat, String from, String to, String userID){
        try {
            LocationController locationController = new LocationController();
            DatabaseHelper databaseHelper = new DatabaseHelper();

            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            String day = getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
            String city = locationController.getCity(lat, lng);

            String combined = city + hourOfDay + to + from;
            Journey foundJourney = databaseHelper.findJourney(combined);
            if(foundJourney != null){
                foundJourney.setCount(foundJourney.getCount() + 1);
                databaseHelper.putJourney(foundJourney);
            }else {
                Journey journey = new Journey();
                journey.setType("journey");
                journey.setHour(hourOfDay);
                journey.setDay(day);

                //Get city
                journey.setCity(city);
                journey.setLatitude(lat);
                journey.setLongitude(lng);

                //Set stations
                journey.setFromCRS(from);
                journey.setToCRS(to);

                //Set user
                journey.setUser(userID);

                //Save journey
                String journeyId = databaseHelper.postJourney(journey).getId().toString();
                LOGGER.info("Saved journey");

                if (!userID.isEmpty()) {
                    //Update user
                    User user = databaseHelper.getUser(null, null, userID);
                    //Update user
                    List<String> journeys;

                    if (user.getJourneyHistory() == null) {
                        journeys = new ArrayList<String>();
                    } else {
                        journeys = user.getJourneyHistory();
                    }

                    journeys.add(journeyId);
                    user.setJourneyHistory(journeys);

                    //Save user
                    databaseHelper.putUser(user);
                }
            }
                //Close connection
                databaseHelper.closeConnection();
        }catch(Exception ex){
            LOGGER.warn(ex);
        }
    }
}
