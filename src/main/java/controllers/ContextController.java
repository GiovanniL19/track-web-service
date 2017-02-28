package controllers;

import handlers.CouchDatabase;
import models.Context;

import javax.ejb.Asynchronous;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by giovannilenguito on 10/02/2017.
 */

@Asynchronous
public class ContextController {
    final private static Logger logger = Logger.getLogger(ContextController.class.getName());

    public void createContext(String lng, String lat, String from, String to, String userID){
        logger.info("Creating context object");
        LocationController locationController = new LocationController();
        CouchDatabase couchDatabase = new CouchDatabase();
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        Context context = new Context();

        context.setType("context");

        //Get the hour
        calendar.setTime(date);
        context.setHour(calendar.get(Calendar.HOUR_OF_DAY));

        //Get city
        context.setCity(locationController.getCity(lat, lng));
        context.setLatitude(lat);
        context.setLongitude(lng);

        //Set stations
        context.setFromCRS(from);
        context.setToCRS(to);

        //Set user
        context.setUser(userID);

        //Save context
        couchDatabase.postContext(context);
        logger.info("Saved context");
    }


}
