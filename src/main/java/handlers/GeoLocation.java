package handlers;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by giovannilenguito on 27/02/2017.
 */
public class GeoLocation {
    //nearest station: https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=53.010151799999996,-2.1804978&radius=500&type=train_station&key=AIzaSyCo-EDpiiMlqgbVjY3K_xCiWo-ubsvPYRg
    private HttpURLConnection connection;
    final private String API_KEY = "AIzaSyCo-EDpiiMlqgbVjY3K_xCiWo-ubsvPYRg";

    private String getRequest(URL url){
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.close();

            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }

            bufferedReader.close();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(connection != null){connection.disconnect();}
        }
    }

    public String getCity(String lat, String lng){
        Parse parse = new Parse();
        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&key=" + API_KEY);
            return parse.toCity(getRequest(url));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
