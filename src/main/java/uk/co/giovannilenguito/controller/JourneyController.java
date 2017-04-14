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

    public JourneyController() {
        parserFactory = new ParserFactory();
    }

    private String getDayOfWeek() {
        //Gets date from calendar
        final Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        //Get day of week from calendar
        final int day = calendar.get(Calendar.DAY_OF_WEEK);

        //Returns day are string
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

    private int getHourOfDay() {
        //Get date
        final Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        //return hour of day
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    @Asynchronous
    public void createJourney(final String lng, final String lat, final String from, final String to, final String userID) {
        locationHelper = new LocationHelper();
        databaseHelper = new DatabaseHelper();

        //Creates new journey
        try {
            int hour = getHourOfDay();
            final String day = getDayOfWeek();
            final String city = locationHelper.getCity(lat, lng);

            final String combined = city + hour + day + to + from;

            Journey foundJourney = databaseHelper.findJourney(combined);

            if (foundJourney != null) {
                //If journey already exists, add one to the count
                foundJourney.setCount(foundJourney.getCount() + 1);
                databaseHelper.putJourney(foundJourney);
            } else {
                //Create new journey POJO
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
                final String journeyId = databaseHelper.postJourney(journey).getId().toString();
                LOGGER.info("Saved journey");

                //Add new journey to users history
                if (!userID.isEmpty()) {
                    //Update user
                    User user = databaseHelper.getUser(null, null, userID);
                    //Update user
                    List<String> journeys;

                    if (user.getJourneyHistory() == null) {
                        journeys = new ArrayList();
                    } else {
                        journeys = user.getJourneyHistory();
                    }

                    journeys.add(journeyId);
                    user.setJourneyHistory(journeys);

                    //Save user
                    databaseHelper.putUser(user);
                }
            }
        } catch (Exception ex) {
            LOGGER.warn(ex);
        } finally {
            //Close connection
            databaseHelper.closeConnection();
        }
    }

    @GET
    @JWTRequired
    public Response getRecommendations(@DefaultValue("")
                                       @QueryParam(value = "user") final String user,
                                       @QueryParam(value = "longitude") final String longitude,
                                       @QueryParam(value = "latitude") final String latitude) {
        try {
            recommendationController = new RecommendationController();
            locationHelper = new LocationHelper();

            final String city = locationHelper.getCity(latitude, longitude);

            //Get recommended journeys
            final JSONArray journeys = recommendationController.getToday(user, city, getHourOfDay(), getDayOfWeek(), false);

            return Response.ok(parserFactory.journeysResponse(journeys), MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            LOGGER.warn(ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Could not get recommended journeys").build();
        }
    }

    @GET
    @Path("{id}")
    public Response getJourney(@PathParam("id") final String id) {
        databaseHelper = new DatabaseHelper();

        //Get journey by id
        final Journey journey = databaseHelper.getJourney(id, null);
        databaseHelper.closeConnection();

        if (journey != null) {
            return Response.status(Response.Status.CREATED).entity(parserFactory.journeyResponse(journey)).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("There was an error").build();
        }
    }

    @GET
    @Path("check/{from}/{to}/{user}")
    public Response checkJourneyExistence(@PathParam("from") String from,
                                          @PathParam("to") String to,
                                          @PathParam("user") final String user) {
        databaseHelper = new DatabaseHelper();

        from = from.substring(0, 1).toUpperCase() + from.substring(1).toLowerCase();
        to = to.substring(0, 1).toUpperCase() + to.substring(1).toLowerCase();


        //Makes key and fixes capitals
        final String key = from + to + user;

        //Gets journey by key
        Journey journey = databaseHelper.getJourney(null, key);
        databaseHelper.closeConnection();

        if (journey != null) {
            return Response.status(Response.Status.OK).entity(parserFactory.checkResponse(journey, true)).build();
        } else {
            return Response.status(Response.Status.OK).entity(parserFactory.checkResponse(journey, false)).build();
        }
    }

    @DELETE
    @Path("{id}")
    @JWTRequired
    public Response deleteJourney(@PathParam("id") final String id) {
        databaseHelper = new DatabaseHelper();

        //Gets journey
        final Journey journey = databaseHelper.getJourney(id, null);

        //Deletes journey
        final org.lightcouch.Response response = databaseHelper.deleteJourney(journey);

        databaseHelper.closeConnection();
        if (response.getError() == null) {
            return Response.status(Response.Status.OK).entity("{}").build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response.getError()).build();
        }
    }

    @POST
    @JWTRequired
    public Response postJourney(final String data) {
        databaseHelper = new DatabaseHelper();

        final JSONObject dataJson = new JSONObject(data);
        //Parse json to pojo
        Journey journey = parserFactory.toJourney(dataJson);

        //Post new journey
        final org.lightcouch.Response databaseResponse = databaseHelper.postJourney(journey);

        //Update and send response
        journey.setType("liked");
        journey.setId(databaseResponse.getId());
        journey.set_id(databaseResponse.getId());

        databaseHelper.closeConnection();
        if (databaseResponse.getError() == null) {
            return Response.status(Response.Status.CREATED).entity(parserFactory.journeyResponse(journey)).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("There was an error").build();
        }
    }
}
