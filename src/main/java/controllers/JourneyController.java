package controllers;

import handlers.CouchDatabase;
import models.Journey;
import models.User;

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
            logger.info("Creating journey object");
            LocationController locationController = new LocationController();
            CouchDatabase couchDatabase = new CouchDatabase();
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            Journey journey = new Journey();

            journey.setType("journey");

            //Get the hour
            calendar.setTime(date);
            journey.setHour(calendar.get(Calendar.HOUR_OF_DAY));

            //Get city
            journey.setCity(locationController.getCity(lat, lng));
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

            if(!userID.isEmpty()) {
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

                //Close connection
                couchDatabase.closeConnection();
        }catch(Exception ex){
            System.out.println("Journey controller error:");
            logger.warning(ex.toString());
        }
    }


}
