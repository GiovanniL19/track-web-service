package Handlers;

import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.util.List;

import Models.Station;
import Models.User;
import com.google.gson.JsonArray;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

/**
 * Created by giovannilenguito on 10/02/2017.
 */
public class Parse {
    private JSONObject train;

    private JSONArray getCallingPoints(JSONArray allCallingPoints){
        JSONArray callingPoints = new JSONArray();
        for (int j = 0; j < allCallingPoints.length(); j++) {
            JSONObject location = allCallingPoints.getJSONObject(j);

            JSONObject locationFormatted = new JSONObject();
            locationFormatted.put("crs", location.get("lt4:crs").toString());
            locationFormatted.put("et", location.get("lt4:et").toString());
            locationFormatted.put("st", location.get("lt4:st").toString());
            locationFormatted.put("name", location.get("lt4:locationName").toString());

            callingPoints.put(locationFormatted);

            if(j+1 == allCallingPoints.length()){
                train.put("arrivalStatus", location.get("lt4:et"));
                train.put("arrivalTime", location.get("lt4:st"));
            }
        }

        return callingPoints;
    }

    public JSONObject boardServices(SOAPMessage xml, String type) throws Exception{
        //Sets up transformer
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        //Get xml content and puts in source
        Source sourceContent = xml.getSOAPPart().getContent();

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final StreamResult result = new StreamResult(byteArrayOutputStream);

        transformer.transform(sourceContent, result);

        //Convert result into json
        JSONObject rawJson = XML.toJSONObject(byteArrayOutputStream.toString());
        JSONObject envelope = rawJson.getJSONObject("soap:Envelope");
        JSONObject body = envelope.getJSONObject("soap:Body");
        JSONObject response = body.getJSONObject(type);
        JSONObject results = response.getJSONObject("GetStationBoardResult");
        JSONObject trainServices = results.getJSONObject("lt5:trainServices");

        JSONArray allServices = new JSONArray();

        try {
            System.out.println("Get services");
            JSONArray services = trainServices.getJSONArray("lt5:service");

            for (int i = 0; i < services.length(); i++) {
                JSONObject service = services.getJSONObject(i);

                //Create origin station
                JSONObject origin = service.getJSONObject("lt5:origin");
                JSONObject oLocation = origin.getJSONObject("lt4:location");

                JSONObject originFormatted = new JSONObject();
                originFormatted.put("crs", oLocation.get("lt4:crs").toString());
                originFormatted.put("name", oLocation.get("lt4:locationName").toString());

                //Create destination station
                JSONObject destinationFormatted = new JSONObject();

                JSONObject destination = service.getJSONObject("lt5:destination");
                JSONObject dLocation = destination.getJSONObject("lt4:location");

                destinationFormatted.put("crs", dLocation.get("lt4:crs").toString());
                destinationFormatted.put("name", dLocation.get("lt4:locationName").toString());


                train = new JSONObject();

                //Create train json object for client
                train.put("origin", originFormatted);
                train.put("destination", destinationFormatted);
                train.put("std", service.get("lt4:std"));
                train.put("etd", service.get("lt4:etd"));

                try {
                    train.put("platform", service.get("lt4:platform"));
                } catch (Exception ex) {
                    train.put("platform", "Awaiting");
                }

                train.put("operator", service.get("lt4:operator"));
                train.put("operatorCode", service.get("lt4:operatorCode"));
                train.put("id", service.get("lt4:serviceID"));
                train.put("type", "train");


                //Set up calling pints
                JSONObject callingAt = service.getJSONObject("lt5:subsequentCallingPoints");
                JSONObject list = callingAt.getJSONObject("lt4:callingPointList");
                try {
                    JSONArray allCallingPoints = list.getJSONArray("lt4:callingPoint");
                    train.put("callingPoints", this.getCallingPoints(allCallingPoints));
                } catch (Exception e) {
                    JSONObject callingPoint = list.getJSONObject("lt4:callingPoint");

                    JSONObject locationFormatted = new JSONObject();
                    locationFormatted.put("crs", callingPoint.get("lt4:crs").toString());
                    locationFormatted.put("et", callingPoint.get("lt4:et").toString());
                    locationFormatted.put("st", callingPoint.get("lt4:st").toString());
                    locationFormatted.put("name", callingPoint.get("lt4:locationName").toString());

                    train.put("callingPoints", locationFormatted);

                    train.put("arrivalStatus", callingPoint.get("lt4:et"));
                    train.put("arrivalTime", callingPoint.get("lt4:st"));
                }

                allServices.put(train);
            }
            JSONObject trains = new JSONObject();
            trains.put("trains", allServices);

            return trains;
        }catch(Exception ex){
            System.out.println(ex.getMessage());

            System.out.println("Get service");
            JSONObject service = trainServices.getJSONObject("lt5:service");

            //Create origin station
            JSONObject origin = service.getJSONObject("lt5:origin");
            JSONObject oLocation = origin.getJSONObject("lt4:location");

            JSONObject originFormatted = new JSONObject();
            originFormatted.put("crs", oLocation.get("lt4:crs").toString());
            originFormatted.put("name", oLocation.get("lt4:locationName").toString());



            //Create destination station
            JSONObject destination = service.getJSONObject("lt5:destination");
            JSONObject dLocation = destination.getJSONObject("lt4:location");

            JSONObject destinationFormatted = new JSONObject();
            destinationFormatted.put("crs", dLocation.get("lt4:crs").toString());
            destinationFormatted.put("name", dLocation.get("lt4:locationName").toString());

            train = new JSONObject();

            JSONObject callingAt = service.getJSONObject("lt5:subsequentCallingPoints");
            JSONObject list = callingAt.getJSONObject("lt4:callingPointList");
            JSONArray allCallingPoints = list.getJSONArray("lt4:callingPoint");


            //Create train json object for client
            train.put("origin", originFormatted);
            train.put("destination", destinationFormatted);
            train.put("std", service.get("lt4:std"));
            train.put("etd", service.get("lt4:etd"));

            try{
                train.put("platform", service.get("lt4:platform"));
            }catch (Exception pEx){
                System.out.println(pEx.getMessage());
                System.out.println("Setting platform to awaiting");
                train.put("platform", "Awaiting");
            }

            train.put("operator", service.get("lt4:operator"));
            train.put("operatorCode", service.get("lt4:operatorCode"));
            train.put("id", service.get("lt4:serviceID"));
            train.put("type", "train");

            train.put("callingPoints", this.getCallingPoints(allCallingPoints));

            allServices.put(train);
            JSONObject trains = new JSONObject();
            trains.put("trains", allServices);
            return trains;
        }
    }

    public JSONObject stationToJson(List<Station> stations){
        JSONArray jsonStations = new JSONArray();

        for(int i = 0; i < stations.size(); i++){
            JSONObject station = new JSONObject();
            station.put("id", stations.get(i).get_id());
            station.put("type", stations.get(i).getType());
            station.put("name", stations.get(i).getName());
            station.put("crs", stations.get(i).getCrs());
            station.put("viewCount", stations.get(i).getViewCount());

            jsonStations.put(station);
        }

        JSONObject response = new JSONObject();
        response.put("stations", jsonStations);

        return response;
    }

    public User toUser(JSONObject data){
        try {
            JSONObject json = data.getJSONObject("user");
            User user = new User();
            //Object
            user.setType(json.getString("type"));

            //Personal information
            user.setFirstName(json.getString("firstName"));
            user.setLastName(json.getString("lastName"));
            user.setEmail(json.getString("email"));

            //Account information
            user.setDateCreated(json.getInt("dateCreated"));
            user.setUsername(json.getString("username"));
            user.setPassword(json.getString("password"));

//            //Get toStations
//            JSONArray toStations = json.getJSONArray("toStations");
//
//            List<String> toStationsArray = null;
//
//            for (int i = 0; i <= toStations.length(); i++) {
//                toStationsArray.add(toStations.getString(i));
//            }
//
//            user.setToStations(toStationsArray);
//
//
//            //Get fromStations
//            JSONArray fromStations = json.getJSONArray("fromStations");
//
//            List<String> fromStationsArray = null;
//
//            for (int i = 0; i <= fromStations.length(); i++) {
//                fromStationsArray.add(fromStations.getString(i));
//            }
//
//            user.setFromStations(fromStationsArray);
//
//
//            //Get journey history
//            JSONArray journeyHistory = json.getJSONArray("journeyHistory");
//
//            List<String> journeyHistoryArray = null;
//
//            for (int i = 0; i <= journeyHistory.length(); i++) {
//                journeyHistoryArray.add(journeyHistory.getString(i));
//            }
//
//            user.setJourneyHistory(journeyHistoryArray);
//
//
//            //Get starred journey
//            JSONArray starredJourney = json.getJSONArray("starredJourney");
//
//            List<String> starredJourneyArray = null;
//
//            for (int i = 0; i <= starredJourney.length(); i++) {
//                starredJourneyArray.add(starredJourney.getString(i));
//            }
//
//            user.setStarredJourneys(starredJourneyArray);


            //Return the user
            System.out.println("Returning parsed user");
            return user;
        }catch(Exception ex){
            //Error
            System.out.println(ex);
            return null;
        }
    }
}
