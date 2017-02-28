package handlers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by giovannilenguito on 28/02/2017.
 */
public class MapsWebService {
    private HttpURLConnection connection;
    private URL url;

    public MapsWebService(URL url) throws Exception{
        this.url = url;

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
    }

    public String get(){
        try {
            //Make request
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.close();


            //Get response
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
            this.disconnect();
        }
    }

    public void disconnect(){
        if(connection != null){
            connection.disconnect();
        }
    }

    public HttpURLConnection getConnection() {
        return connection;
    }
}
