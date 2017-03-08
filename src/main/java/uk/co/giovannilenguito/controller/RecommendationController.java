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
    //
    //
    */

    private List<Journey> sortArray(List<Journey> journeys){
        Collections.sort(journeys, new Comparator<Journey>(){
            @Override
            public int compare(Journey o1, Journey o2) {
                return Integer.valueOf(o2.getCount()).compareTo(o1.getCount());
            }
        });

        return journeys;
    }

    private JSONArray buildResponse(int numberOfObjects, List<Journey> journeys){
        JSONArray response = new JSONArray();
        //Get top 5 results by count property
        for (int i = 0; i < numberOfObjects; i++) {
            if(i < journeys.size()) {
                JSONObject journey = new JSONObject(journeys.get(i));
                response.put(journey);
            }
        }
        return response;
    }

    public JSONArray getTodayByUser(String user_id, String city, int hour, String day){
        DatabaseHelper databaseHelper = new DatabaseHelper();

        //Get from couchdb all documents with match user id, city and hour key
        List<Journey> journeys = databaseHelper.getAllJourneysByKey(user_id, city, hour, day);

        //Sort array high count to low
        journeys = sortArray(journeys);

        databaseHelper.closeConnection();
        //return json array of journeys (routes)
        return buildResponse(5, journeys);
    }

    public JSONArray getGenericJourneysToday(String city, int hour, String day){
        DatabaseHelper databaseHelper = new DatabaseHelper();

        //Get from couchdb all documents with match user id, city and hour key
        List<Journey> journeys = databaseHelper.getAllJourneysByKey(null, city, hour, day);

        //Sort array high count to low
        journeys = sortArray(journeys);

        databaseHelper.closeConnection();
        //return json array of journeys (routes)
        return buildResponse(5, journeys);
    }
}
