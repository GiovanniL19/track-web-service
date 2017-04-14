package uk.co.giovannilenguito.helper;

import org.junit.Assert;
import org.junit.Test;
import uk.co.giovannilenguito.factory.ParserFactory;

import java.net.URL;


/**
 * Created by giovannilenguito on 11/04/2017.
 */
public class LocationHelperTest {
    private ParserFactory parserFactory;
    private ConnectionHelper connectionHelper;

    @Test
    public void getCity() throws Exception {
        String expected = "London";
        parserFactory = new ParserFactory();
        try {
            //Make connection
            connectionHelper = new ConnectionHelper(new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=51.5074,0.1278&key=AIzaSyCo-EDpiiMlqgbVjY3K_xCiWo-ubsvPYRg"));

            //Make request
            final String response = connectionHelper.get();

            //ParserFactory response
            Assert.assertEquals(expected, parserFactory.toCity(response).toString());
        } catch (Exception ex) {
            Assert.fail();
        } finally {
            Assert.assertNotNull(connectionHelper);
            if (connectionHelper != null) {
                connectionHelper.disconnect();
            }
        }
    }

    @Test
    public void getNearestStation() throws Exception {
        parserFactory = new ParserFactory();
        int expected = 9;
        try {
            //Make connection
            connectionHelper = new ConnectionHelper(new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=51.5074,0.1278&radius=10000&type=train_station&key=AIzaSyCo-EDpiiMlqgbVjY3K_xCiWo-ubsvPYRg"));

            //Make request
            final String connectionResponse = connectionHelper.get();

            //ParserFactory response
            int length = parserFactory.destinationStationsToJSONArray(connectionResponse).length();
            Assert.assertEquals(expected, length);

        } catch (Exception ex) {
            Assert.fail();
        } finally {
            Assert.assertNotNull(connectionHelper);
            if (connectionHelper != null) {
                connectionHelper.disconnect();
            }
        }
    }

}