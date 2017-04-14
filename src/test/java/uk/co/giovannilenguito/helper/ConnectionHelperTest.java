package uk.co.giovannilenguito.helper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * Created by giovannilenguito on 11/04/2017.
 */
public class ConnectionHelperTest {
    private HttpURLConnection connection;

    @Before
    public void setUp() throws Exception {
        URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=51.5074,0.1278&key=AIzaSyCo-EDpiiMlqgbVjY3K_xCiWo-ubsvPYRg");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
    }

    @Test
    public void get() throws Exception {
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
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            Assert.fail();
        } finally {
            this.disconnect();
        }
    }

    @Test
    public void disconnect() throws Exception {
        if (connection != null) {
            connection.disconnect();
        }
    }

}