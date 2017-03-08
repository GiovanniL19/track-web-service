package uk.co.giovannilenguito.controller;

import uk.co.giovannilenguito.factory.ParserFactory;
import uk.co.giovannilenguito.helper.ConnectionHelper;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import java.net.URL;

/**
 * Created by giovannilenguito on 27/02/2017.
 */
public class LocationController {
    final private Logger LOGGER = Logger.getLogger(LocationController.class.getName());

    final private String API_KEY = "AIzaSyCo-EDpiiMlqgbVjY3K_xCiWo-ubsvPYRg";
    final private String RADIUS = "10000";

    public String getCity(String lat, String lng){
        ConnectionHelper connectionHelper = null;
        ParserFactory parserFactory = new ParserFactory();
        try {
            //Make connection
            connectionHelper = new ConnectionHelper(new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&key=" + API_KEY));
            //Make request
            String response = connectionHelper.get();
            //ParserFactory response
            return parserFactory.toCity(response);
        } catch (Exception ex) {
            LOGGER.warn(ex);
            return null;
        } finally {
            if(connectionHelper != null) {
                connectionHelper.disconnect();
            }
        }
    }

    public JSONArray getNearestStation(String longitude, String latitude){
        ConnectionHelper connectionHelper = null;
        ParserFactory parserFactory = new ParserFactory();
        try {
            //Make connection
            connectionHelper = new ConnectionHelper(new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=" + RADIUS + "&type=train_station&key=" + API_KEY));
            //Make request
            String connectionResponse = connectionHelper.get();
            //ParserFactory response
            return parserFactory.toStationsArray(connectionResponse);
        } catch (Exception ex) {
            LOGGER.warn(ex);
            return null;
        } finally {
            if(connectionHelper != null) {
                connectionHelper.disconnect();
            }
        }
    }
}
