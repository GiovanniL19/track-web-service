package handlers;

import models.Journey;
import models.Station;
import models.User;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.Response;

import java.util.List;

/**
 * Created by giovannilenguito on 09/02/2017.
 */
public class CouchDatabase {
    /*
    // For the web service to connect to CouchDB, the database will need to be running on the host provided with the correct username and password.
    // Developed by Giovanni Lenguito
    */
    private final String DATABASE_NAME = "track";
    private final String LOCAL_HOST = "localhost";
    private final String PROTOCOL = "http";
    private final String USERNAME = "admin";
    private final String PASSWORD = "9999567890";
    private final int PORT = 5984;

    CouchDbClient databaseClient;

    //Initialise Instance
    public CouchDatabase(){
        try{
            //Configure Connection
            CouchDbProperties properties = new CouchDbProperties().setDbName(DATABASE_NAME).setProtocol(PROTOCOL).setHost(LOCAL_HOST).setPort(PORT).setUsername(USERNAME).setPassword(PASSWORD);
            //Create instance with properties
            databaseClient = new CouchDbClient(properties);
            System.out.println("Connection successful");
        }catch(Exception ex){
            System.out.println("Connection failed");
        }
    }

    public CouchDatabase(String host, String username, String password, int port, String protocol, String databaseName){
        try{
            //Configure Connection
            CouchDbProperties properties = new CouchDbProperties().setDbName(databaseName).setProtocol(protocol).setHost(host).setPort(port).setUsername(username).setPassword(password);
            //Create instance with properties
            databaseClient = new CouchDbClient(properties);
            System.out.println("Connection successful");
        }catch(Exception ex){
            System.out.println("Connection failed");
        }
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

    public List<Station> getAllJourneysByUser(String id){
        List<Station> list = databaseClient.view("journeys/journeysByUser").includeDocs(true).startKey(id).endKey(id).query(Station.class);
        return list;
    }

    //Journey Post and Put
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
            return list.get(0);
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

    //Close Connection (Needs to be called once response has been sent to client)
    public void closeConnection(){
        databaseClient.shutdown();
    }
}
