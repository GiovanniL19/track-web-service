package Handlers;

import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.util.List;

import Models.Station;
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

        final ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
        final StreamResult result = new StreamResult(streamOut);

        transformer.transform(sourceContent, result);

        //Convert result into json
        JSONObject rawJson = XML.toJSONObject(streamOut.toString());
        JSONObject envelope = rawJson.getJSONObject("soap:Envelope");
        JSONObject body = envelope.getJSONObject("soap:Body");
        JSONObject response = body.getJSONObject(type);
        JSONObject results = response.getJSONObject("GetStationBoardResult");
        JSONObject trainServices = results.getJSONObject("lt5:trainServices");

        try {
            JSONArray services = trainServices.getJSONArray("lt5:service");

            JSONArray allServices = new JSONArray();

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
            System.out.println(ex);

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
                System.out.println(pEx);
                train.put("platform", "Awaiting");
            }

            train.put("operator", service.get("lt4:operator"));
            train.put("operatorCode", service.get("lt4:operatorCode"));
            train.put("id", service.get("lt4:serviceID"));
            train.put("type", "train");

            train.put("callingPoints", this.getCallingPoints(allCallingPoints));

            return train;
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
}
