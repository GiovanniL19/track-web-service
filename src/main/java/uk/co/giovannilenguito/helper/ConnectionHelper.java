package uk.co.giovannilenguito.helper;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by giovannilenguito on 28/02/2017.
 */
public class ConnectionHelper {
    /*
    *
    * Connection Helper
    * Used to connect to a web service
    *
    * By Giovanni Lenguito
    *
    */
    final private Logger LOGGER = Logger.getLogger(ConnectionHelper.class.getName());
    private HttpURLConnection connection;

    public ConnectionHelper(URL url) throws Exception {
        //Sets up connection
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
    }

    public String get() {
        try {
            //Make request
            final DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.close();


            //Get response
            final InputStream inputStream = connection.getInputStream();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final StringBuilder response = new StringBuilder();

            //Build string
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }

            bufferedReader.close();

            return response.toString();
        } catch (Exception ex) {
            LOGGER.warn(ex);
            return null;
        } finally {
            this.disconnect();
        }
    }

    public void disconnect() {
        if (connection != null) {
            connection.disconnect();
        }
    }
}
