package uk.co.giovannilenguito.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import uk.co.giovannilenguito.helper.DatabaseHelper;
import uk.co.giovannilenguito.model.Journey;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by giovannilenguito on 08/03/2017.
 */
public class RecommendationController {
    /*
    // Recommendation and Prediction Engine
    // Uses data mining techniques on all users
    */

    private List<Journey> sortArray(final List<Journey> journeys){
        Collections.sort(journeys, (o1, o2) -> Integer.valueOf(o2.getCount()).compareTo(o1.getCount()));
        return journeys;
    }

    private JSONArray buildResponse(final int numberOfObjects, final List<Journey> journeys){
        JSONArray response = new JSONArray();

        //Get top 5 results by count property
        for (int i = 0; i < numberOfObjects; i++) {
            if(i < journeys.size()) {
                JSONObject toStation;
                JSONObject fromStation;

                DatabaseHelper databaseHelper = new DatabaseHelper();
                toStation = new JSONObject(databaseHelper.getStation(null, null, journeys.get(i).getToCRS()));
                fromStation = new JSONObject(databaseHelper.getStation(null, null, journeys.get(i).getFromCRS()));

                //Close connection
                databaseHelper.closeConnection();

                JSONObject journey = new JSONObject(journeys.get(i));
                journey.put("to", toStation);
                journey.put("from", fromStation);
                response.put(journey);
            }
        }
        return response;
    }

    public JSONArray getToday(String user_id, String city, int hour, String day, boolean byUser){
        DatabaseHelper databaseHelper = new DatabaseHelper();

        List<Journey> journeys;

        if(byUser){
            //Get from couchdb all documents with match user id, city and hour key
            journeys = databaseHelper.getAllJourneysByKey(user_id, city, hour, day);
        }else{
            //Get from couchdb all documents with match, city and hour key
            journeys = databaseHelper.getAllJourneysByKey(null, city, hour, day);
        }

        //Sort array high count to low
        journeys = sortArray(journeys);

        databaseHelper.closeConnection();
        //return json array of journeys (routes)
        return buildResponse(5, journeys);
    }
}
