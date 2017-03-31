package uk.co.giovannilenguito.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import uk.co.giovannilenguito.annotation.JWTRequired;
import uk.co.giovannilenguito.factory.ParserFactory;
import uk.co.giovannilenguito.helper.DatabaseHelper;
import uk.co.giovannilenguito.helper.LocationHelper;
import uk.co.giovannilenguito.model.Journey;
import uk.co.giovannilenguito.model.User;
import org.apache.log4j.Logger;

import javax.ejb.Asynchronous;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by giovannilenguito on 10/02/2017.
 */
// The Java class will be hosted at the URI path "/journeys"
@Path("/journeys")
public class JourneyController {
    final private Logger LOGGER = Logger.getLogger(JourneyController.class.getName());
    private ParserFactory parserFactory;
    private RecommendationController recommendationController;
    private DatabaseHelper databaseHelper;
    private LocationHelper locationHelper;

    private String getDayOfWeek(){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_WEEK);

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

    private int getHourOfDay(){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    @Asynchronous
    public void createJourney(String lng, String lat, String from, String to, String userID){
        try {
            locationHelper = new LocationHelper();
            databaseHelper = new DatabaseHelper();

            int hour = getHourOfDay();
            String day = getDayOfWeek();
            String city = locationHelper.getCity(lat, lng);

            String combined = city + hour + day + to + from;
            Journey foundJourney = databaseHelper.findJourney(combined);
            if(foundJourney != null){
                foundJourney.setCount(foundJourney.getCount() + 1);
                databaseHelper.putJourney(foundJourney);
            }else {
                Journey journey = new Journey();
                journey.setType("journey");
                journey.setHour(hour);
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

    @GET
    public Response getRecommendations(@DefaultValue("") @QueryParam(value="user") String user, @QueryParam(value="longitude") String longitude, @QueryParam(value="latitude") String latitude){
        try{
            recommendationController = new RecommendationController();
            locationHelper = new LocationHelper();

            String city = locationHelper.getCity(latitude, longitude);

            JSONArray journeys = recommendationController.getTodayByUser(user, city, getHourOfDay(), getDayOfWeek());

            JSONObject response = new JSONObject();
            response.put("journeys", journeys);

            return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
        }catch(Exception ex){
            LOGGER.warn(ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not get recommended journeys").build();
        }
    }

    @GET
    @Path("{id}")
    public Response getJourney(@PathParam("id") String id) {
        databaseHelper = new DatabaseHelper();
        Journey journey = databaseHelper.getJourney(id, null);

        if(journey != null){
            JSONObject response = new JSONObject();
            response.put("journey", new JSONObject(journey));

            return Response.status(Response.Status.CREATED).entity(response.toString()).build();
        }else{
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("There was an error").build();
        }
    }

    @GET
    @Path("check/{from}/{to}/{user}")
    public Response checkJourneyExistence(@PathParam("from") String from, @PathParam("to") String to, @PathParam("user") String user) {
        String key = from + to + user;
        databaseHelper = new DatabaseHelper();
        Journey journey = databaseHelper.getJourney(null, key);
        databaseHelper.closeConnection();

        JSONObject response = new JSONObject();
        if(journey != null){
            response.put("found", true);
            response.put("id", journey.getId());
            return Response.status(Response.Status.OK).entity(response.toString()).build();
        }else{
            response.put("found", false);
            return Response.status(Response.Status.OK).entity(response.toString()).build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response deleteJourney(@PathParam("id") String id) {
        databaseHelper = new DatabaseHelper();

        Journey journey = databaseHelper.getJourney(id, null);
        org.lightcouch.Response response = databaseHelper.deleteJourney(journey);
        databaseHelper.closeConnection();

        if(response.getError() == null){
            return Response.status(Response.Status.OK).entity("{}").build();
        }else{
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response.getError()).build();
        }
    }

    @POST
    public Response postJourney(String data) {
        parserFactory = new ParserFactory();
        databaseHelper = new DatabaseHelper();
        JSONObject dataJson = new JSONObject(data);
        Journey journey = parserFactory.toJourney(dataJson);

        journey.setType("liked");
        org.lightcouch.Response databaseResponse = databaseHelper.postJourney(journey);
        journey.setId(databaseResponse.getId());
        journey.set_id(databaseResponse.getId());

        JSONObject response = new JSONObject();
        response.put("journey", new JSONObject(journey));

        databaseHelper.closeConnection();
        if (databaseResponse.getError() == null) {
            return Response.status(Response.Status.CREATED).entity(response.toString()).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("There was an error").build();
        }
    }

//    //DEBUGGING ONLY
//    @GET
//    @Path("delete/all")
//    public String deleteAllJourneys() {
//        //FOR DEBUGGING
//        databaseHelper = new DatabaseHelper();
//        databaseHelper.deleteAllJourneys();
//        databaseHelper.closeConnection();
//        return "All journeys deleted";
//    }
}
