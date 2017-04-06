package uk.co.giovannilenguito.helper;

import uk.co.giovannilenguito.model.Journey;
import uk.co.giovannilenguito.model.Station;
import uk.co.giovannilenguito.model.User;
import org.apache.log4j.Logger;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by giovannilenguito on 09/02/2017.
 */
public class DatabaseHelper {
    /*
    * Database Helper
    * For the web service to connect to CouchDB, the database will need to be running on the host provided
    * with the correct username and password.
    */

    final private static Logger LOGGER = Logger.getLogger(DatabaseHelper.class.getName());

    final private String DATABASE_NAME = "track";
    final private String LOCAL_HOST = "localhost";
    final private String PROTOCOL = "http";
    final private String USERNAME = "admin";
    final private String PASSWORD = "9999567890";
    final private int PORT = 5984;

    CouchDbClient databaseClient;

    //Initialise Instance
    public DatabaseHelper(){
        try{
            //Configure Connection
            CouchDbProperties properties = new CouchDbProperties().setDbName(DATABASE_NAME).setProtocol(PROTOCOL).setHost(LOCAL_HOST).setPort(PORT).setUsername(USERNAME).setPassword(PASSWORD);
            //Create instance with properties
            databaseClient = new CouchDbClient(properties);
        }catch(Exception ex){
            LOGGER.warn(ex);
        }
    }

    //Journeys CRUD
    public Journey findJourney(final String combinedString){
        List<Journey> list = databaseClient.view("journeys/combined").includeDocs(true).startKey(combinedString).endKey(combinedString).query(Journey.class);

        if(list.size() != 0) {
            return list.get(0);
        }else{
            return null;
        }
    }

    public List<Journey> getAllJourneysByKey(final String user_id, final String city, final int hour, final String day){
        List<Journey> list = new ArrayList<>();
        List<Journey> list1;
        List<Journey> list2;

        String key;
        String secondKey;

        if(user_id == null){
            key = city + hour + day;
            int secondHour = 0;
            if(hour + 1 == 25){
                secondHour = 0;
            }else{
                secondHour = hour + 1;
            }

            secondKey = city + secondHour + day;

            list1 = databaseClient.view("journeys/journeyByCityHourDay").includeDocs(true).startKey(key).endKey(key).query(Journey.class);

            list2 = databaseClient.view("journeys/journeyByCityHourDay").includeDocs(true).startKey(secondKey).endKey(secondKey).query(Journey.class);
            Stream.of(list1, list2).forEach(list::addAll);
        }else{
            key = user_id + city + hour + day;
            int secondHour = 0;
            if(hour + 1 == 25){
                secondHour = 0;
            }else{
                secondHour = hour + 1;
            }

            secondKey = user_id + city + secondHour + day;

            list1 = databaseClient.view("journeys/journeyByUserCityHourDay").includeDocs(true).startKey(key).endKey(key).query(Journey.class);

            list2 = databaseClient.view("journeys/journeyByUserCityHourDay").includeDocs(true).startKey(secondKey).endKey(secondKey).query(Journey.class);
            Stream.of(list1, list2).forEach(list::addAll);
        }

        return list;
    }

    public Response postJourney(final Journey journey){
        //Save journey
        return databaseClient.save(journey);
    }

    public Response putJourney(final Journey journey){
        //Update journey
        return databaseClient.update(journey);
    }

    public Journey getJourney(final String id, final String key){
        //Get journey
        List<Journey> list;
        if(key != null){
            list = databaseClient.view("likedJourneys/combined").includeDocs(true).startKey(key).endKey(key).query(Journey.class);
        }else{
            return databaseClient.find(Journey.class, id);
        }

        if(list.size() == 0){
            return null;
        }else{
            return list.get(0);
        }
    }

    public Response deleteJourney(final Journey journey){
        //Delete journey
        return databaseClient.remove(journey);
    }

    //Station CRUD
    public List<Station> getAllStations(){
        final List<Station> list = databaseClient.view("stations/stationsByName").includeDocs(true).query(Station.class);
        return list;
    }

    public Station getStation(String name, String id, String crs){
        Station station = new Station();
        if(name != null){
            List<Station> list = databaseClient.view("stations/stationsByName").includeDocs(true).startKey(name).endKey(name).query((Class) station.getClass());
            if(!list.isEmpty()) {
                return list.get(0);
            }else{
                return null;
            }
        }else if(crs != null){
            List<Station> list = databaseClient.view("stations/stationsByCrs").includeDocs(true).startKey(crs).endKey(crs).query((Class) station.getClass());
            if(!list.isEmpty()) {
                return list.get(0);
            }else{
                return null;
            }
        }else{
            return databaseClient.find(Station.class, id);
        }
    }

    //User CRUD
    public boolean doesEmailExist(final String email){
        int found = databaseClient.view("users/usersByUsername").startKey(email).endKey(email).query(Station.class).size();
        return found >= 1;

    }

    public User getUser(final String username, final String email, final String id){
        List<User> list;
        if(username != null){
            list = databaseClient.view("users/usersByUsername").includeDocs(true).startKey(username).endKey(username).query(User.class);
        }else if(email != null){
            list = databaseClient.view("users/usersByEmail").includeDocs(true).startKey(email).endKey(email).query(User.class);
        }else{
            return databaseClient.find(User.class, id);
        }

        if(list.size() == 0){
            return null;
        }else{
            return list.get(0);
        }
    }

    public Response postUser(final User user){
        //Save user
        return databaseClient.save(user);
    }

    public String putUser(final User user){
        //Update user
        return databaseClient.update(user).getRev();
    }

    public Response deleteUser(final User user){
        //Remove user
        return databaseClient.remove(user);
    }

    //Close Connection (Needs to be called once response has been sent to client)
    public void closeConnection(){
        LOGGER.info("Database connection closed");
        databaseClient.shutdown();
    }
}
