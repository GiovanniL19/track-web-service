package uk.co.giovannilenguito.helper;

import uk.co.giovannilenguito.model.Journey;
import uk.co.giovannilenguito.model.Station;
import uk.co.giovannilenguito.model.User;
import org.apache.log4j.Logger;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.Response;

import java.util.List;

/**
 * Created by giovannilenguito on 09/02/2017.
 */
public class DatabaseHelper {

    final private static Logger LOGGER = Logger.getLogger(DatabaseHelper.class.getName());
    /*
    // For the web service to connect to CouchDB, the database will need to be running on the host provided with the correct username and password.
    //
    */
    private final String DATABASE_NAME = "track";
    private final String LOCAL_HOST = "localhost";
    private final String PROTOCOL = "http";
    private final String USERNAME = "admin";
    private final String PASSWORD = "9999567890";
    private final int PORT = 5984;

    CouchDbClient databaseClient;

    //Initialise Instance
    public DatabaseHelper(){
        try{
            //Configure Connection
            CouchDbProperties properties = new CouchDbProperties().setDbName(DATABASE_NAME).setProtocol(PROTOCOL).setHost(LOCAL_HOST).setPort(PORT).setUsername(USERNAME).setPassword(PASSWORD);
            //Create instance with properties
            databaseClient = new CouchDbClient(properties);
            System.out.println("Connection successful");
        }catch(Exception ex){
            LOGGER.warn(ex);
            System.out.println("Connection failed");
        }
    }

    //Close Connection (Needs to be called once response has been sent to client)
    public void closeConnection(){
        LOGGER.info("Database connection closed");
        databaseClient.shutdown();
    }

    //Journeys CRUD
    public Journey findJourney(String combinedString){
        List<Journey> list = databaseClient.view("journeys/combined").includeDocs(true).startKey(combinedString).endKey(combinedString).query(Journey.class);
        if(list.size() != 0) {
            return list.get(0);
        }else{
            return null;
        }
    }

    public List<Journey> getAllJourneysByUser(String id){
        List<Journey> list = databaseClient.view("journeys/journeysByUser").includeDocs(true).startKey(id).endKey(id).query(Journey.class);
        return list;
    }

    public List<Journey> getAllJourneysByKey(String user_id, String city, int hour, String day){
        List<Journey> list;

        if(user_id == null) {
            String startKey = city + hour + day;
            int secondHour = hour + 1;
            if(secondHour == 25){
                secondHour = 0;
            }
            String endKey = city + secondHour  + day;
            list = databaseClient.view("journeys/journeyByCityHour").includeDocs(true).startKey(startKey).endKey(endKey).query(Journey.class);
        }else{
            int secondHour = hour + 1;
            if(secondHour == 25){
                secondHour = 0;
            }

            String startKey = user_id + city + hour + day;
            String endKey = user_id + city + secondHour + day;

            list = databaseClient.view("journeys/journeyByUserCityHour").includeDocs(true).startKey(startKey).endKey(endKey).query(Journey.class);
        }
        return list;
    }

    public Response postJourney(Journey journey){
        //Save journey
        return databaseClient.save(journey);
    }

    public Response putJourney(Journey journey){
        //Update journey
        return databaseClient.update(journey);
    }

    //Station CRUD
    public List<Station> getAllStations(){
        List<Station> list = databaseClient.view("stations/stationsByName").includeDocs(true).query(Station.class);
        return list;
    }

    public Station getStation(String name, String id){
        if(name != null){
            List<Station> list = databaseClient.view("stations/stationsByName").includeDocs(true).startKey(name).endKey(name).query(Station.class);
            if(!list.isEmpty()) {
                return list.get(0);
            }else{
                return null;
            }
        }else{
            return databaseClient.find(Station.class, id);
        }
    }

    public Response putStation(Station station){
        //Update station
        return databaseClient.update(station);
    }

    //User CRUD
    public boolean doesEmailExist(String email){
        int found = databaseClient.view("users/usersByUsername").startKey(email).endKey(email).query(Station.class).size();
        System.out.println(found);
        if (found >= 1){
            return true;
        }else{
            return false;
        }

    }

    public User getUser(String username, String email, String id){
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

    public Response postUser(User user){
        //Save user
        return databaseClient.save(user);
    }

    public Response putUser(User user){
        //Update user
        return databaseClient.update(user);
    }

    public Response deleteUser(User user){
        //Remove user
        return databaseClient.remove(user);
    }


    //USED FOR DEBUGGING
    public void deleteAllJourneys(){
        List<Journey> list = databaseClient.view("journeys/journeysByUser").includeDocs(true).query(Journey.class);
        for(int i = 0; i < list.size(); i++){
            databaseClient.remove(list.get(i));
        }
    }
}
