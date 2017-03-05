package uk.co.giovannilenguito.factory;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.util.List;

import uk.co.giovannilenguito.helper.DatabaseHelper;
import uk.co.giovannilenguito.model.Station;
import uk.co.giovannilenguito.model.User;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

/**
 * Created by giovannilenguito on 10/02/2017.
 */
public class ParserFactory {
    final private Logger LOGGER = Logger.getLogger(ParserFactory.class.getName());
    private JSONObject train;

    private JSONObject getServices(SOAPMessage xml, String type) throws SOAPException, TransformerException {
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

        return trainServices;
    };

    private JSONObject getOrigin(JSONObject service){
        //Create origin station
        JSONObject origin = service.getJSONObject("lt5:origin");
        JSONObject oLocation = origin.getJSONObject("lt4:location");

        JSONObject originFormatted = new JSONObject();
        originFormatted.put("crs", oLocation.get("lt4:crs").toString());
        originFormatted.put("name", oLocation.get("lt4:locationName").toString());

        return originFormatted;
    }

    private JSONObject getDestination(JSONObject service){
        //Create destination station
        JSONObject destinationFormatted = new JSONObject();

        JSONObject destination = service.getJSONObject("lt5:destination");
        JSONObject dLocation = destination.getJSONObject("lt4:location");

        destinationFormatted.put("crs", dLocation.get("lt4:crs").toString());
        destinationFormatted.put("name", dLocation.get("lt4:locationName").toString());
        return destinationFormatted;
    }

    private JSONArray getCallingPoints(JSONArray allCallingPoints){
        JSONArray callingPoints = new JSONArray();
        for (int j = 0; j < allCallingPoints.length(); j++) {
            JSONObject location = allCallingPoints.getJSONObject(j);

            JSONObject locationFormatted = new JSONObject();
            locationFormatted.put("crs", location.get("lt4:crs").toString());
            if(!location.isNull("lt4:et")) {
                locationFormatted.put("et", location.get("lt4:et").toString());
            }

            if(!location.isNull("lt4:st")) {
                locationFormatted.put("st", location.get("lt4:st").toString());
            }
            locationFormatted.put("name", location.get("lt4:locationName").toString());

            callingPoints.put(locationFormatted);

            if(j+1 == allCallingPoints.length()){
                train.put("arrivalStatus", location.get("lt4:et"));
                train.put("arrivalTime", location.get("lt4:st"));
            }
        }

        return callingPoints;
    }

    private JSONArray getPreviousCallingPoints(JSONArray allCallingPoints){
        JSONArray callingPoints = new JSONArray();
        for (int j = 0; j < allCallingPoints.length(); j++) {
            JSONObject location = allCallingPoints.getJSONObject(j);
            JSONObject locationFormatted = new JSONObject();
            locationFormatted.put("crs", location.getString("lt4:crs"));

            if(!location.isNull("lt4:at")){
                locationFormatted.put("at", location.getString("lt4:at"));
            }
            locationFormatted.put("st", location.getString("lt4:st"));
            locationFormatted.put("name", location.getString("lt4:locationName"));

            callingPoints.put(locationFormatted);
        }

        return callingPoints;
    }

    public JSONObject departureBoardServices(SOAPMessage xml, String type) throws Exception{
        JSONObject trainServices = getServices(xml, type);
        JSONArray allServices = new JSONArray();

        final Object serviceObject = new JSONTokener(trainServices.get("lt5:service").toString()).nextValue();
        if(serviceObject instanceof JSONArray) {
            LOGGER.info("Get departure services");
            JSONArray services = (JSONArray) serviceObject;

            for (int i = 0; i < services.length(); i++) {
                JSONObject service = services.getJSONObject(i);
                JSONObject originFormatted = getOrigin(service);
                JSONObject destinationFormatted = getDestination(service);

                train = new JSONObject();

                //Create train json object for client
                train.put("origin", originFormatted);
                train.put("destination", destinationFormatted);
                train.put("std", service.get("lt4:std"));
                train.put("etd", service.get("lt4:etd"));

                if(!service.isNull("lt4:platform")) {
                    train.put("platform", service.get("lt4:platform"));
                }else{
                    train.put("platform", "Awaiting");
                }

                train.put("operator", service.get("lt4:operator"));
                train.put("operatorCode", service.get("lt4:operatorCode"));
                train.put("id", service.get("lt4:serviceID"));
                train.put("type", "train");


                //Set up calling pints
                JSONObject callingAt = service.getJSONObject("lt5:subsequentCallingPoints");
                JSONObject list = callingAt.getJSONObject("lt4:callingPointList");

                final Object callingPointObject = new JSONTokener(list.get("lt4:callingPoint").toString()).nextValue();

                if(callingPointObject instanceof JSONArray) {
                    JSONArray allCallingPoints = (JSONArray) callingPointObject;
                    train.put("callingPoints", this.getCallingPoints(allCallingPoints));
                }else if(callingPointObject instanceof JSONObject) {
                    JSONObject callingPoint = (JSONObject) callingPointObject;

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
        }else {
            JSONObject service = (JSONObject) serviceObject;
            JSONObject originFormatted = getOrigin(service);
            JSONObject destinationFormatted = getDestination(service);

            train = new JSONObject();
            JSONObject callingAt = service.getJSONObject("lt5:subsequentCallingPoints");
            JSONObject list = callingAt.getJSONObject("lt4:callingPointList");
            JSONArray allCallingPoints = list.getJSONArray("lt4:callingPoint");

            //Create train json object for client
            train.put("origin", originFormatted);
            train.put("destination", destinationFormatted);
            train.put("std", service.get("lt4:std"));
            train.put("etd", service.get("lt4:etd"));

            if(!service.isNull("lt4:platform")) {
                train.put("platform", service.get("lt4:platform"));
            }else {
                LOGGER.info("Setting platform to awaiting");
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

    public JSONObject arrivalBoardServices(SOAPMessage xml, String type) throws Exception{
        JSONObject trainServices = getServices(xml, type);
        JSONArray allServices = new JSONArray();

        final Object serviceObject = new JSONTokener(trainServices.get("lt5:service").toString()).nextValue();
        if(serviceObject instanceof JSONArray) {
            LOGGER.info("Get arrival services");
            JSONArray services = (JSONArray) serviceObject;

            for (int i = 0; i < services.length(); i++) {
                JSONObject service = services.getJSONObject(i);

                train = new JSONObject();
                train.put("operatorCode", service.get("lt4:operatorCode"));

                JSONObject originFormatted = getOrigin(service);
                JSONObject destinationFormatted = getDestination(service);

                train.put("id", service.get("lt4:serviceID"));

                //Create train json object for client
                train.put("origin", originFormatted);
                train.put("destination", destinationFormatted);

                train.put("sta", service.get("lt4:sta"));
                train.put("eta", service.get("lt4:eta"));

                if (!service.isNull("lt4:platform")) {
                    train.put("platform", service.get("lt4:platform"));
                } else {
                    train.put("platform", "Awaiting");
                }

                train.put("operator", service.get("lt4:operator"));
                train.put("type", "train");

                //Set up calling pints
                if(!service.isNull("lt5:previousCallingPoints")) {
                    JSONObject callingAt = service.getJSONObject("lt5:previousCallingPoints");
                    JSONObject list = callingAt.getJSONObject("lt4:callingPointList");
                    final Object callingPointObject = new JSONTokener(list.get("lt4:callingPoint").toString()).nextValue();
                    if (callingPointObject instanceof JSONArray) {
                        JSONArray allCallingPoints = (JSONArray) callingPointObject;
                        train.put("callingPoints", this.getPreviousCallingPoints(allCallingPoints));
                    } else {
                        JSONObject callingPoint = (JSONObject) callingPointObject;

                        JSONObject locationFormatted = new JSONObject();
                        locationFormatted.put("crs", callingPoint.get("lt4:crs").toString());

                        if (!callingPoint.isNull("lt4:at")) {
                            locationFormatted.put("at", callingPoint.get("lt4:at").toString());
                        }

                        locationFormatted.put("st", callingPoint.get("lt4:st").toString());
                        locationFormatted.put("name", callingPoint.get("lt4:locationName").toString());

                        train.put("callingPoints", locationFormatted);
                    }
                }

                allServices.put(train);
            }
            JSONObject trains = new JSONObject();
            trains.put("trains", allServices);

            return trains;
        }else {
            LOGGER.info("Get arrival service");
            JSONObject service = (JSONObject) serviceObject;

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

            if(!service.isNull("lt5:subsequentCallingPoints")) {
                JSONObject callingAt = service.getJSONObject("lt5:subsequentCallingPoints");
                JSONObject list = callingAt.getJSONObject("lt4:callingPointList");
                JSONArray allCallingPoints = list.getJSONArray("lt4:callingPoint");
                train.put("callingPoints", this.getCallingPoints(allCallingPoints));
            }


            //Create train json object for client
            train.put("origin", originFormatted);
            train.put("destination", destinationFormatted);

            if(!service.isNull("lt4:std")) {
                train.put("std", service.get("lt4:std"));
            }else if(!service.isNull("lt4:sta")) {
                train.put("sta", service.get("lt4:sta"));
            }

            if(!service.isNull("lt4:etd")) {
                train.put("etd", service.get("lt4:etd"));
            }else if(!service.isNull("lt4:eta")) {
                train.put("eta", service.get("lt4:eta"));
            }

            train.put("platform", service.get("lt4:platform"));
            train.put("operator", service.get("lt4:operator"));
            train.put("operatorCode", service.get("lt4:operatorCode"));
            train.put("id", service.get("lt4:serviceID"));
            train.put("type", "train");

            allServices.put(train);
            JSONObject trains = new JSONObject();
            trains.put("trains", allServices);
            return trains;
        }
    }

    public JSONObject stationMessage(SOAPMessage xml, String type) throws Exception{
        //Sets up transformer
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        //Get xml content and puts in source
        Source sourceContent = xml.getSOAPPart().getContent();

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final StreamResult result = new StreamResult(byteArrayOutputStream);
        transformer.transform(sourceContent, result);

        JSONObject messageFormatted = new JSONObject();

        //Convert result into json
        JSONObject rawJson = XML.toJSONObject(byteArrayOutputStream.toString());
        JSONObject envelope = rawJson.getJSONObject("soap:Envelope");
        JSONObject body = envelope.getJSONObject("soap:Body");
        JSONObject response = body.getJSONObject(type);
        JSONObject results = response.getJSONObject("GetStationBoardResult");
        if(!results.isNull("lt4:nrccMessages")) {
            JSONObject messages = results.getJSONObject("lt4:nrccMessages");

            final Object messageObject = new JSONTokener(messages.get("lt:message").toString()).nextValue();
            if (messageObject instanceof String) {
                messageFormatted.put("message", messages.getString("lt:message"));
            } else if (messageObject instanceof JSONArray) {

                JSONArray messageArray = (JSONArray) messageObject;
                String message = "";
                for (int i = 0; i < messageArray.length(); i++) {
                    if (i == 0) {
                        message = messageArray.get(i).toString();
                    } else {
                        message = message + " " + messageArray.get(i).toString();
                    }
                }
                messageFormatted.put("message", message);
            }
        }else{
            messageFormatted.put("message", "");
        }

        return messageFormatted;
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
            user.setImage(json.getString("image"));

            //Get toStations
            JSONArray toStations = json.getJSONArray("toStations");

            List<String> toStationsArray = null;

            for (int i = 0; i <= toStations.length(); i++) {
                toStationsArray.add(toStations.getString(i));
            }

            user.setToStations(toStationsArray);


            //Get fromStations
            JSONArray fromStations = json.getJSONArray("fromStations");

            List<String> fromStationsArray = null;

            for (int i = 0; i <= fromStations.length(); i++) {
                fromStationsArray.add(fromStations.getString(i));
            }

            user.setFromStations(fromStationsArray);


            //Get journey history
            JSONArray journeyHistory = json.getJSONArray("journeyHistory");

            List<String> journeyHistoryArray = null;

            for (int i = 0; i <= journeyHistory.length(); i++) {
                journeyHistoryArray.add(journeyHistory.getString(i));
            }

            user.setJourneyHistory(journeyHistoryArray);


            //Get starred journey
            JSONArray starredJourney = json.getJSONArray("starredJourney");

            List<String> starredJourneyArray = null;

            for (int i = 0; i <= starredJourney.length(); i++) {
                starredJourneyArray.add(starredJourney.getString(i));
            }

            user.setStarredJourneys(starredJourneyArray);


            //Return the user
            LOGGER.info("Returning parsed user");
            return user;
        }catch(Exception ex){
            //Error
            LOGGER.warn(ex);
            return null;
        }
    }

    public String toCity(String location){
        try {
            JSONObject locationObject = new JSONObject(location);
            JSONArray results = locationObject.getJSONArray("results");
            JSONObject firstResult = results.getJSONObject(0);
            JSONArray address = firstResult.getJSONArray("address_components");
            JSONObject locality = address.getJSONObject(2);
            String city = locality.getString("long_name");

            return city;
        }catch(Exception ex){
            LOGGER.warn(ex);
            return "";
        }
    }

    public JSONArray toStationsArray(String raw){
        try {
            JSONObject json = new JSONObject(raw);
            JSONArray results = json.getJSONArray("results");

            JSONArray stations = new JSONArray();
            for (int i = 0; i < results.length(); i++) {
                JSONObject station = new JSONObject();
                //Get station from database
                DatabaseHelper databaseHelper = new DatabaseHelper();
                Station foundStation = databaseHelper.getStation(results.getJSONObject(i).getString("name"), null);

                if(foundStation != null) {
                    station.put("id", foundStation.getId());
                    station.put("name", foundStation.getName());
                    station.put("crs", foundStation.getCrs());
                    station.put("viewCount", foundStation.getViewCount());
                    stations.put(station);
                }
            }

            return stations;
        }catch(Exception ex){
            LOGGER.warn(ex);
            return null;
        }

    }

    public String errorMessage(Exception ex){
        LOGGER.warn(ex);

        String message;
        if(ex.getMessage().equals("JSONObject[\"lt5:trainServices\"] not found.")) {
            message = "No Trains Running";
        }else if(ex.getMessage().equals("JSONObject[\"GetDepBoardWithDetailsResponse\"] not found.")){
            message = "";
        }else{
            message = ex.getMessage();
        }

        return message;
    }
}
