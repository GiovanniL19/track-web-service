package controllers;

import handlers.MapsWebService;
import handlers.Parse;

import java.net.URL;

/**
 * Created by giovannilenguito on 27/02/2017.
 */
public class LocationController {
    final private String API_KEY = "AIzaSyCo-EDpiiMlqgbVjY3K_xCiWo-ubsvPYRg";

    public String getCity(String lat, String lng){
        MapsWebService mapsWebService = null;
        Parse parse = new Parse();
        try {
            //Make connection
            mapsWebService = new MapsWebService(new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&key=" + API_KEY));
            //Make request
            String response = mapsWebService.get();
            //Parse response
            return parse.toCity(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(mapsWebService != null) {
                mapsWebService.disconnect();
            }
        }
    }

    public String getNearestStation(String lat, String lng){
        //nearest station: https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=53.010151799999996,-2.1804978&radius=500&type=train_station&key=AIzaSyCo-EDpiiMlqgbVjY3K_xCiWo-ubsvPYRg
        return null;
    }
}
