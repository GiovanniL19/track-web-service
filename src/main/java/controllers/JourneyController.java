package controllers;

import handlers.CouchDatabase;
import models.Journey;
import models.User;
import sun.plugin2.main.client.CALayerProvider;

import javax.ejb.Asynchronous;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by giovannilenguito on 10/02/2017.
 */

@Asynchronous
public class JourneyController {
    final private static Logger logger = Logger.getLogger(JourneyController.class.getName());

    public void createJourney(String lng, String lat, String from, String to, String userID){
        try {
            LocationController locationController = new LocationController();
            CouchDatabase couchDatabase = new CouchDatabase();

            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            String city = locationController.getCity(lat, lng);

            String combined = city + hourOfDay + to + from;
            Journey foundJourney = couchDatabase.findJourney(combined);
            if(foundJourney != null){
                foundJourney.setCount(foundJourney.getCount() + 1);
                couchDatabase.putJourney(foundJourney);
            }else {
                Journey journey = new Journey();
                journey.setType("journey");
                journey.setHour(hourOfDay);

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
                String journeyId = couchDatabase.postJourney(journey).getId().toString();
                logger.info("Saved journey");

                if (!userID.isEmpty()) {
                    //Update user
                    User user = couchDatabase.getUser(null, null, userID);
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
                    couchDatabase.putUser(user);
                }
            }
                //Close connection
                couchDatabase.closeConnection();
        }catch(Exception ex){
            System.out.println("Journey controller error:");
            logger.warning(ex.toString());
        }
    }


}
