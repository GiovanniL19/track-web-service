package Handlers;

import Models.Station;
import Models.User;
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


    //Route CRUD
    public List<Station> getAllRoutesByUser(String id){
        List<Station> list = databaseClient.view("routes/routesByUser").includeDocs(true).startKey(id).query(Station.class);
        return list;
    }



    //Station CRUD
    public List<Station> getAllStations(){
        List<Station> list = databaseClient.view("stations/stationsByName").includeDocs(true).query(Station.class);
        return list;
    }

    public Station getStation(String name, String id){
        if(name != null){
            List<Station> list = databaseClient.view("stations/stationsByName").includeDocs(true).startKey(name).query(Station.class);
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
    public Station getUser(String username, String email, String id){
        if(username != null){
            List<Station> list = databaseClient.view("users/usersByUsername").includeDocs(true).startKey(username).query(Station.class);
            return list.get(0);
        }else if(email != null){
            List<Station> list = databaseClient.view("users/usersByEmail").includeDocs(true).startKey(email).query(Station.class);
            return list.get(0);
        }else{
            return databaseClient.find(Station.class, id);
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
