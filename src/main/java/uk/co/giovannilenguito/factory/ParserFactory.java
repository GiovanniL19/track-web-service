package uk.co.giovannilenguito.factory;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.co.giovannilenguito.helper.DatabaseHelper;
import uk.co.giovannilenguito.model.Journey;
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
    /*
    *
    * Parser Factory
    * Parses XML data from SOAP request into JSON
    * Parses POJOs into JSON
    * Parses JSON into POJOs
    *
    * By Giovanni Lenguito
    *
    */

    final private Logger LOGGER = Logger.getLogger(ParserFactory.class.getName());
    private JSONObject train;

    private JSONObject xmlToJson(final String xml, final String type) {
        //Convert STRING into a json object
        JSONObject rawJson = XML.toJSONObject(xml);
        JSONObject envelope = rawJson.getJSONObject("soap:Envelope");
        JSONObject body = envelope.getJSONObject("soap:Body");
        JSONObject response = body.getJSONObject(type);
        JSONObject results = response.getJSONObject("GetStationBoardResult");

        if(!results.isNull("lt5:trainServices")) {
            return results.getJSONObject("lt5:trainServices");
        }else{
            return null;
        }
    }

    private JSONObject serviceXMLToJSON(final SOAPMessage xml, final String type) throws SOAPException, TransformerException {

        //Converts the SOAPMessage into a String
        //Sets up transformer
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final Transformer transformer = transformerFactory.newTransformer();

        //Get xml content and puts in source
        final Source sourceContent = xml.getSOAPPart().getContent();

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final StreamResult result = new StreamResult(byteArrayOutputStream);
        transformer.transform(sourceContent, result);

        //Returns result from xmlToJson
        return xmlToJson(byteArrayOutputStream.toString(), type);
    }

    private JSONObject getTrainServices(Object data, String type) {
        try {
            if (data instanceof SOAPMessage) {
                JSONObject result = this.serviceXMLToJSON((SOAPMessage) data, type);

                if(result == null){
                    return null;
                }else{
                    return result;
                }
            } else if (data instanceof String) {
                return this.xmlToJson((String) data, type);
            } else {
                return null;
            }
        } catch (Exception ex) {
            LOGGER.warn(ex.getMessage());
            return null;
        }
    }

    private JSONObject getOrigin(final JSONObject service) {
        //Gets the origin station information from service json object

        //Puts parsed data into formatted json object
        JSONObject originFormatted = new JSONObject();

        //Create origin station
        final JSONObject origin = service.getJSONObject("lt5:origin");

        if(!origin.isNull("lt4:location")) {
            if(origin.get("lt4:location") instanceof JSONObject){
                final JSONObject oLocation = origin.getJSONObject("lt4:location");
                originFormatted.put("crs", oLocation.get("lt4:crs").toString());
                originFormatted.put("name", oLocation.get("lt4:locationName").toString());

                return originFormatted;
            }else{
                //if location is an array, loop through array and make a single json object from the multiple objects
                final JSONArray locations = origin.getJSONArray("lt4:location");

                String name = "";
                String crs = "";

                for (int i = 0; i < locations.length(); i++) {
                    final JSONObject loc = locations.getJSONObject(i);

                    if (!name.isEmpty()) {
                        name = name + " & " + loc.get("lt4:locationName").toString();
                        crs = crs + ", " + loc.get("lt4:crs").toString();
                    } else {
                        name = loc.get("lt4:locationName").toString();
                        crs = loc.get("lt4:crs").toString();
                    }
                }

                originFormatted.put("crs", crs);
                originFormatted.put("name", name);

                return originFormatted;
            }
        }else{
            return null;
        }

    }

    private JSONObject getDestination(final JSONObject service) {
        //Puts the destination object into json object
        final JSONObject destination = service.getJSONObject("lt5:destination");

        //Create destination station
        JSONObject destinationFormatted = new JSONObject();

        //Checks if location is an object or an array
        final Object locationObject = new JSONTokener(destination.get("lt4:location").toString()).nextValue();
        if (locationObject instanceof JSONObject) {
            //If location if object, put destination information into a json object
            JSONObject dLocation = destination.getJSONObject("lt4:location");
            destinationFormatted.put("crs", dLocation.get("lt4:crs").toString());
            destinationFormatted.put("name", dLocation.get("lt4:locationName").toString());

            return destinationFormatted;
        } else if (locationObject instanceof JSONArray) {
            //if location is an array, loop through array and make a single json object from the multiple objects
            final JSONArray dLocations = destination.getJSONArray("lt4:location");

            String name = "";
            String crs = "";

            for (int i = 0; i < dLocations.length(); i++) {
                final JSONObject dLocation = dLocations.getJSONObject(i);

                if (!name.isEmpty()) {
                    crs = crs + ", " + dLocation.get("lt4:crs").toString();
                    name = name + " & " + dLocation.get("lt4:locationName").toString();
                } else {
                    crs = dLocation.get("lt4:crs").toString();
                    name = dLocation.get("lt4:locationName").toString();
                }
            }

            destinationFormatted.put("crs", crs);
            destinationFormatted.put("name", name);

            return destinationFormatted;
        } else {
            //If no destinations, return null
            return null;
        }
    }

    private JSONObject getPoint(final JSONObject point) {
        //Get a single calling point
        JSONObject callingPoint = new JSONObject();

        //If et, st and at exist, add to formatted json
        if (!point.isNull("lt4:et")) {
            callingPoint.put("et", point.get("lt4:et").toString());
        }
        if (!point.isNull("lt4:st")) {
            callingPoint.put("st", point.get("lt4:st").toString());
        }
        if (!point.isNull("lt4:at")) {
            callingPoint.put("at", point.getString("lt4:at"));
        }

        callingPoint.put("crs", point.get("lt4:crs").toString());
        callingPoint.put("st", point.get("lt4:st").toString());
        callingPoint.put("name", point.get("lt4:locationName").toString());

        if (!callingPoint.isNull("lt4:at")) {
            train.put("arrivalStatus", callingPoint.get("lt4:at"));
        }else if (!callingPoint.isNull("lt4:et")) {
            train.put("arrivalStatus", callingPoint.get("lt4:et"));
        }


        if (!callingPoint.isNull("lt4:st")) {
            train.put("arrivalTime", callingPoint.get("lt4:st"));
        }

        //Returns single calling point
        return callingPoint;
    }

    private JSONArray getPoints(final JSONArray allCallingPoints) {
        //Get multiple calling points from json array
        JSONArray callingPoints = new JSONArray();

        for (int j = 0; j < allCallingPoints.length(); j++) {
            final JSONObject location = allCallingPoints.getJSONObject(j);

            //Create the formatted location from the raw json
            JSONObject locationFormatted = new JSONObject();
            locationFormatted.put("crs", location.get("lt4:crs").toString());
            locationFormatted.put("name", location.get("lt4:locationName").toString());

            //If et, st and at exist, add to formatted json
            if (!location.isNull("lt4:et")) {
                locationFormatted.put("et", location.get("lt4:et").toString());
            }
            if (!location.isNull("lt4:st")) {
                locationFormatted.put("st", location.get("lt4:st").toString());
            }
            if (!location.isNull("lt4:at")) {
                locationFormatted.put("at", location.getString("lt4:at"));
            }

            //Add the calling point to an array of calling points
            callingPoints.put(locationFormatted);

            //Set arrival information on train json object
            if (j + 1 == allCallingPoints.length()) {
                if (!location.isNull("lt4:at")) {
                    train.put("arrivalStatus", location.get("lt4:at"));
                }else if (!location.isNull("lt4:et")) {
                    train.put("arrivalStatus", location.get("lt4:et"));
                }

                if (!location.isNull("lt4:st")) {
                    train.put("arrivalTime", location.get("lt4:st"));
                }
            }
        }

        //Return calling points
        return callingPoints;
    }

    private JSONArray getCallingPoints(final JSONObject callingAt){
        JSONArray callingPoints = new JSONArray();

        final Object callingPointListObject = new JSONTokener(callingAt.get("lt4:callingPointList").toString()).nextValue();
        if (callingPointListObject instanceof JSONArray) {
            //If the train splits, there will ba an array of calling points arrays,
            //for example [[callingPoints],[callingPoints]]

            final JSONArray list = callingAt.getJSONArray("lt4:callingPointList");

            for (int j = 0; j < list.length(); j++) {
                final JSONObject item = list.getJSONObject(j);
                final Object callingPointObject = new JSONTokener(item.get("lt4:callingPoint").toString()).nextValue();

                if (callingPointObject instanceof JSONArray) {
                    //Get multiple calling points
                    callingPoints.put(this.getPoints((JSONArray) callingPointObject));
                } else if (callingPointObject instanceof JSONObject) {
                    //Get single calling point
                    callingPoints.put(this.getPoint((JSONObject) callingPointObject));
                }
            }

            //Update train json object, add calling points
            train.put("trainSplits", true);

        } else if (callingPointListObject instanceof JSONObject) {
            //If the train only has one set of calling points (train does not split)
            final JSONObject list = callingAt.getJSONObject("lt4:callingPointList");

            final Object callingPointObject = new JSONTokener(list.get("lt4:callingPoint").toString()).nextValue();
            if (callingPointObject instanceof JSONArray) {
                //Get multiple calling points
                callingPoints.put(this.getPoints((JSONArray) callingPointObject));
            } else if (callingPointObject instanceof JSONObject) {
                final JSONObject callingPoint = (JSONObject) callingPointObject;

                //Puts the single calling point into an array
                JSONArray arrayWrap = new JSONArray();
                arrayWrap.put(this.getPoint(callingPoint));
                callingPoints.put(arrayWrap);
            }
        }
        return callingPoints;
    }

    private JSONObject getDepartingTrains(final JSONArray services) {
        //Initialise all Services JSON Array
        JSONArray allServices = new JSONArray();

        LOGGER.info("Get departure services");

        for (int i = 0; i < services.length(); i++) {
            //Get current service from for loop iteration
            final JSONObject service = services.getJSONObject(i);
            //Get origin and destination
            final JSONObject originFormatted = getOrigin(service);
            final JSONObject destinationFormatted = getDestination(service);

            //Initialise new train json object
            train = new JSONObject();

            //Create formatted train json object from raw service json
            train.put("origin", originFormatted);
            train.put("destination", destinationFormatted);
            train.put("std", service.get("lt4:std"));
            train.put("etd", service.get("lt4:etd"));
            train.put("operator", service.get("lt4:operator"));
            train.put("operatorCode", service.get("lt4:operatorCode"));
            train.put("id", service.get("lt4:serviceID"));
            train.put("type", "train");

            if (!service.isNull("lt4:platform")) {
                train.put("platform", service.get("lt4:platform"));
            } else {
                train.put("platform", "Awaiting");
            }

            //Set up calling pints
            if (!service.isNull("lt5:subsequentCallingPoints")) {
                final JSONObject callingAt = service.getJSONObject("lt5:subsequentCallingPoints");
                train.put("callingPoints", getCallingPoints(callingAt));
            }

            allServices.put(train);
        }

        //Put all parsed services in json object for client response
        JSONObject trains = new JSONObject();
        trains.put("trains", allServices);

        return trains;
    }

    private JSONObject getDepartingTrain(final JSONObject service) {
        //When only one service has been found:

        //Initialise all Services JSON Array
        JSONArray allServices = new JSONArray();

        //Get origin and destination from service
        final JSONObject originFormatted = getOrigin(service);
        final JSONObject destinationFormatted = getDestination(service);

        //Initialise train json object
        train = new JSONObject();

        //If service contains calling points
        if (!service.isNull("lt5:subsequentCallingPoints")) {
            final JSONObject callingAt = service.getJSONObject("lt5:subsequentCallingPoints");
            train.put("callingPoints", getCallingPoints(callingAt));
        }

        //Complete train json object
        train.put("origin", originFormatted);
        train.put("destination", destinationFormatted);
        train.put("std", service.get("lt4:std"));
        train.put("etd", service.get("lt4:etd"));

        if (!service.isNull("lt4:platform")) {
            train.put("platform", service.get("lt4:platform"));
        } else {
            LOGGER.info("Setting platform to awaiting");
            train.put("platform", "Awaiting");
        }

        train.put("operator", service.get("lt4:operator"));
        train.put("operatorCode", service.get("lt4:operatorCode"));
        train.put("id", service.get("lt4:serviceID"));
        train.put("type", "train");


        allServices.put(train);

        //Puts service into trains json object for client response
        JSONObject trains = new JSONObject();
        trains.put("trains", allServices);

        return trains;
    }

    private JSONObject getArrivingTrains(final JSONArray services) {
        JSONArray allServices = new JSONArray();
        LOGGER.info("Get arrival services");

        for (int i = 0; i < services.length(); i++) {
            final JSONObject service = services.getJSONObject(i);
            final JSONObject originFormatted = getOrigin(service);
            final JSONObject destinationFormatted = getDestination(service);

            //Create train json object
            train = new JSONObject();
            train.put("operatorCode", service.get("lt4:operatorCode"));
            train.put("id", service.get("lt4:serviceID"));
            train.put("origin", originFormatted);
            train.put("destination", destinationFormatted);
            train.put("sta", service.get("lt4:sta"));
            train.put("eta", service.get("lt4:eta"));
            train.put("operator", service.get("lt4:operator"));
            train.put("type", "train");

            if (!service.isNull("lt4:platform")) {
                train.put("platform", service.get("lt4:platform"));
            } else {
                train.put("platform", "Awaiting");
            }

            //Set up calling pints
            if (!service.isNull("lt5:previousCallingPoints")) {
                final JSONObject callingAt = service.getJSONObject("lt5:previousCallingPoints");
                train.put("callingPoints", getCallingPoints(callingAt));
            }

            //Put train in all services array
            allServices.put(train);
        }

        //Put all services array in trains object for client response
        JSONObject trains = new JSONObject();
        trains.put("trains", allServices);

        return trains;
    }

    private JSONObject getArrivingTrain(final JSONObject service) {
        JSONArray allServices = new JSONArray();

        final JSONObject originFormatted = getOrigin(service);
        final JSONObject destinationFormatted = getDestination(service);

        train = new JSONObject();

        //Set up calling pints
        if (!service.isNull("lt5:previousCallingPoints")) {
            final JSONObject callingAt = service.getJSONObject("lt5:previousCallingPoints");
            train.put("callingPoints", getCallingPoints(callingAt));
        }

        //Create train json object for client
        train.put("origin", originFormatted);
        train.put("destination", destinationFormatted);
        train.put("platform", service.get("lt4:platform"));
        train.put("operator", service.get("lt4:operator"));
        train.put("operatorCode", service.get("lt4:operatorCode"));
        train.put("id", service.get("lt4:serviceID"));
        train.put("type", "train");

        if (!service.isNull("lt4:std")) {
            train.put("std", service.get("lt4:std"));
        } else if (!service.isNull("lt4:sta")) {
            train.put("sta", service.get("lt4:sta"));
        }

        if (!service.isNull("lt4:etd")) {
            train.put("etd", service.get("lt4:etd"));
        } else if (!service.isNull("lt4:eta")) {
            train.put("eta", service.get("lt4:eta"));
        }

        //Put train in all services array for client format
        allServices.put(train);

        //Put all services array in trains object for client response
        JSONObject trains = new JSONObject();
        trains.put("trains", allServices);

        return trains;
    }

    public JSONObject departureBoardServices(final Object data, final String type) throws Exception {
        //Convert SOAPMessage or String into JSON object
        final JSONObject trainServices = this.getTrainServices(data, type);

        if(trainServices != null){
            final Object serviceObject = new JSONTokener(trainServices.get("lt5:service").toString()).nextValue();

            if (serviceObject instanceof JSONArray) {
                //If service object is an array, it contains multiple trains
                return this.getDepartingTrains((JSONArray) serviceObject);
            } else {
                //If service object is an object, it contains a single train
                return this.getDepartingTrain((JSONObject) serviceObject);
            }
        }else{
            return null;
        }
    }

    public JSONObject arrivalBoardServices(final Object data, final String type) throws Exception {
        //Convert SOAPMessage or String into JSON object
        final JSONObject trainServices = this.getTrainServices(data, type);

        if(trainServices != null) {
            final Object serviceObject = new JSONTokener(trainServices.get("lt5:service").toString()).nextValue();

            if (serviceObject instanceof JSONArray) {
                return this.getArrivingTrains((JSONArray) serviceObject);
            } else {
                return this.getArrivingTrain((JSONObject) serviceObject);
            }
        }else{
            return null;
        }
    }

    public JSONObject getStationMessage(final SOAPMessage data, final String type) throws Exception {
        JSONObject messageFormatted = new JSONObject();

        //Sets up transformer
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final Transformer transformer = transformerFactory.newTransformer();

        //Get xml content and puts in source
        final Source sourceContent = data.getSOAPPart().getContent();

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final StreamResult result = new StreamResult(byteArrayOutputStream);
        transformer.transform(sourceContent, result);

        //Convert result into json
        final JSONObject rawJson = XML.toJSONObject(byteArrayOutputStream.toString());

        //Get message from rawJson
        final JSONObject envelope = rawJson.getJSONObject("soap:Envelope");
        final JSONObject body = envelope.getJSONObject("soap:Body");
        final JSONObject response = body.getJSONObject(type);
        final JSONObject results = response.getJSONObject("GetStationBoardResult");

        if (!results.isNull("lt4:nrccMessages")) {
            final JSONObject messages = results.getJSONObject("lt4:nrccMessages");
            final Object messageObject = new JSONTokener(messages.get("lt:message").toString()).nextValue();

            if (messageObject instanceof String) {
                messageFormatted.put("message", messages.getString("lt:message"));
            } else if (messageObject instanceof JSONArray) {
                final JSONArray messageArray = (JSONArray) messageObject;

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
        } else {
            messageFormatted.put("message", "");
        }

        return messageFormatted;
    }

    public JSONObject stationsToJson(final List<Station> stations) {
        JSONArray jsonStations = new JSONArray();

        //Loop stations
        for (Iterator<Station> iterator = stations.iterator(); iterator.hasNext(); ) {
            final Station iteration = iterator.next();


            //Create station json object from POJO
            JSONObject station = new JSONObject();
            station.put("id", iteration.get_id());
            station.put("type", iteration.getType());
            station.put("name", iteration.getName());
            station.put("crs", iteration.getCrs());
            station.put("viewCount", iteration.getViewCount());

            jsonStations.put(station);
        }

        //Put stations in stations response array for client
        JSONObject response = new JSONObject();
        response.put("stations", jsonStations);

        return response;
    }

    public JSONArray destinationStationsToJSONArray(final String raw) {
        final DatabaseHelper databaseHelper = new DatabaseHelper();

        try {
            final JSONObject json = new JSONObject(raw);
            final JSONArray results = json.getJSONArray("results");
            final JSONArray stations = new JSONArray();

            for (int i = 0; i < results.length(); i++) {
                //Get station from database
                final Station foundStation = databaseHelper.getStation(results.getJSONObject(i).getString("name"), null, null);

                if (foundStation != null) {
                    JSONObject station = new JSONObject();
                    station.put("id", foundStation.getId());
                    station.put("name", foundStation.getName());
                    station.put("crs", foundStation.getCrs());
                    station.put("viewCount", foundStation.getViewCount());
                    stations.put(station);
                }
            }
            return stations;
        } catch (Exception ex) {
            LOGGER.warn(ex);
            return null;
        }

    }

    public String toCity(final String location) {
        try {
            //Get city from json object
            final JSONObject locationObject = new JSONObject(location);
            final JSONArray results = locationObject.getJSONArray("results");
            final JSONObject firstResult = results.getJSONObject(0);
            final JSONArray address = firstResult.getJSONArray("address_components");
            final JSONObject locality = address.getJSONObject(2);
            final String city = locality.getString("long_name");

            return city;
        } catch (Exception ex) {
            LOGGER.warn(ex);
            return "";
        }
    }

    public User toUser(final JSONObject data, final String id) {
        try {
            final JSONObject json = data.getJSONObject("user");

            User user = new User();
            if (id != null) {
                user.set_id(id);
                user.setId(id);
            }
            final Object revObject = new JSONTokener(json.get("rev").toString()).nextValue();
            if (revObject instanceof String) {
                user.set_rev((String) revObject);
            }

            //Set user properties with json object
            user.setType(json.getString("type"));

            //Personal information
            user.setFirstName(json.getString("firstName"));
            user.setLastName(json.getString("lastName"));
            user.setEmail(json.getString("email"));

            //Account information
            user.setDateCreated(json.getInt("dateCreated"));
            user.setUsername(json.getString("username"));
            user.setPassword(json.getString("password"));

            if (!json.isNull("image")) {
                user.setImage(json.getString("image"));
            }


            //Get toStations
            if (json.has("toStations")) {
                final JSONArray toStations = json.getJSONArray("toStations");
                List<String> destinationStations = new ArrayList<>();

                for (int i = 0; i < toStations.length(); i++) {
                    destinationStations.add(toStations.getString(i));
                }

                user.setToStations(destinationStations);
            }


            //Get fromStations
            if (json.has("fromStations")) {
                final JSONArray fromStations = json.getJSONArray("fromStations");
                List<String> fromStationsArray = new ArrayList<>();

                for (int i = 0; i < fromStations.length(); i++) {
                    fromStationsArray.add(fromStations.getString(i));
                }

                user.setToStations(fromStationsArray);

            }

            //Get journey history
            if (json.has("journeyHistory")) {
                final JSONArray journeyHistory = json.getJSONArray("journeyHistory");
                List<String> journeyHistoryArray = new ArrayList<>();

                for (int i = 0; i < journeyHistory.length(); i++) {
                    journeyHistoryArray.add(journeyHistory.getString(i));
                }

                user.setJourneyHistory(journeyHistoryArray);
            }

            //Get starred journey
            if (json.has("starredJourneys")) {
                final JSONArray starredJourneys = json.getJSONArray("starredJourneys");
                List<String> starredJourneysArray = new ArrayList<>();

                for (int i = 0; i < starredJourneys.length(); i++) {
                    starredJourneysArray.add(starredJourneys.getString(i));
                }

                user.setStarredJourneys(starredJourneysArray);
            }

            //Return the user
            LOGGER.info("Returning parsed user");
            return user;
        } catch (Exception ex) {
            //Error
            LOGGER.warn(ex);
            return null;
        }
    }

    public Journey toJourney(final JSONObject data) {
        try {
            final JSONObject json = data.getJSONObject("journey");
            final JSONObject to = json.getJSONObject("to");
            final JSONObject from = json.getJSONObject("from");

            Journey journey = new Journey();

            Station toObject = new Station();
            toObject.setCrs(to.getString("crs"));
            toObject.setName(to.getString("name"));

            Station fromObject = new Station();
            fromObject.setCrs(from.getString("crs"));
            fromObject.setName(from.getString("name"));

            journey.setTo(toObject);
            journey.setFrom(fromObject);

            journey.setType(json.getString("type"));
            journey.setUser(json.getString("starred"));

            //Return the user
            LOGGER.info("Returning parsed journey");
            return journey;
        } catch (Exception ex) {
            //Error
            LOGGER.warn(ex);
            return null;
        }
    }

    public String toToken(final String token, final User user) {
        //Put token in json response
        JSONObject response = new JSONObject();
        response.put("token", token);
        response.put("user", user.get_id());

        return response.toString();
    }

    public String errorMessage(final Exception ex) {
        //Extract error message from exception
        LOGGER.warn(ex);

        String message = "";
        if(ex.getMessage() != null) {
            if (ex.getMessage().equals("JSONObject[\"lt5:trainServices\"] not found.")) {
                message = "No Trains Running";
            } else if (ex.getMessage().equals("JSONObject[\"GetDepBoardWithDetailsResponse\"] not found.")) {
                message = "";
            } else {
                message = "There was an error";
            }

        }

        return message;
    }


    //Client responses
    public String journeysResponse(final JSONArray journeys) {
        //Put in json response
        JSONObject response = new JSONObject();
        response.put("journeys", journeys);
        return response.toString();
    }

    public String journeyResponse(final Journey journey) {
        //Put in json response
        JSONObject response = new JSONObject();
        response.put("journeys", new JSONObject(journey));
        return response.toString();
    }

    public String checkResponse(final Journey journey, final Boolean found) {
        //Put in json response
        JSONObject response = new JSONObject();
        response.put("found", found);

        if (journey != null) {
            response.put("id", journey.getId());
        }
        return response.toString();
    }

    public String stationsResponse(final JSONArray stations) {
        //Put in json response
        JSONObject response = new JSONObject();
        response.put("stations", stations);
        return response.toString();
    }

    public String userResponse(final User user) {
        //Put in json response
        JSONObject response = new JSONObject();
        response.put("user", new JSONObject(user));
        return response.toString();
    }

    public String existResponse(final boolean result) {
        //Put in json response
        JSONObject response = new JSONObject();
        response.put("exist", result);

        return response.toString();
    }
}
