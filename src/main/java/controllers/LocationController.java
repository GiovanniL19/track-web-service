package controllers;

import handlers.MapsWebService;
import handlers.Parse;
import models.Station;
import org.json.JSONArray;

import java.net.URL;
import java.util.List;

/**
 * Created by giovannilenguito on 27/02/2017.
 */
public class LocationController {
    final private String API_KEY = "AIzaSyCo-EDpiiMlqgbVjY3K_xCiWo-ubsvPYRg";
    final private String RADIUS = "5000";

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

    public JSONArray getNearestStation(String lng, String lat){
        MapsWebService mapsWebService = null;
        Parse parse = new Parse();
        try {
            //Make connection
            mapsWebService = new MapsWebService(new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng + "&radius=" + RADIUS + "&type=train_station&key=" + API_KEY));
            //Make request
            String response = mapsWebService.get();
            //Parse response
            return parse.toStationsArray(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(mapsWebService != null) {
                mapsWebService.disconnect();
            }
        }
    }
}
