package uk.co.giovannilenguito.factory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import uk.co.giovannilenguito.helper.DatabaseHelper;
import uk.co.giovannilenguito.model.Journey;
import uk.co.giovannilenguito.model.Station;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by giovannilenguito on 04/04/2017.
 */
public class ParserFactoryTest {
    List<String> files = new ArrayList<>();

    final private ParserFactory PARSER;
    String XML1;

    public ParserFactoryTest() {
        PARSER = new ParserFactory();
    }

    @Before
    public void setUp() throws Exception {
        ParserFactory parserFactory = new ParserFactory();
        File xmlFile1 = new File("src/test/java/uk/co/giovannilenguito/data/getTrainsXML1.xml");

        Reader fileReader = new FileReader(xmlFile1);

        BufferedReader bufferedReader= new BufferedReader(fileReader);
        StringBuilder stringBuilder= new StringBuilder();
        String line = bufferedReader.readLine();

        while (line != null) {
            stringBuilder.append(line).append("\n");
            line = bufferedReader.readLine();
        }

        XML1 = stringBuilder.toString();
        bufferedReader.close();
    }

    @Test
    public void departureBoardServices() throws Exception {
        //Possibility 1
        final JSONObject json = PARSER.departureBoardServices(XML1, "GetDepBoardWithDetailsResponse");
        Assert.assertNotNull(json);
    }

    @Test
    public void arrivalBoardServices() throws Exception {
    }

    @Test
    public void stationMessage() throws Exception {
    }

    @Test
    public void stationsToJson() throws Exception {
        List<Station> stationList = new ArrayList();
        Station dummyStation = new Station();
        dummyStation.setName("Test");
        dummyStation.setCrs("TST");
        dummyStation.setType("Station");
        dummyStation.set_id("123S");
        dummyStation.setViewCount(1);

        stationList.add(dummyStation);

        JSONArray jsonArray = new JSONArray();

        JSONObject object = new JSONObject();
        for(Iterator<Station> iterator = stationList.iterator(); iterator.hasNext();){
            Station station = iterator.next();

            object.put("id", station.get_id());
            object.put("type", station.getType());
            object.put("name", station.getName());
            object.put("crs", station.getCrs());
            object.put("viewCount", station.getViewCount());

            jsonArray.put(object);
        }
        int expectedLength = 1;

        Assert.assertEquals(object, jsonArray.get(0));
        Assert.assertEquals(expectedLength, jsonArray.length());

    }

    @Test
    public void destinationStationsToJSONArray() throws Exception {
        String raw = "{\n" +
                "   \"html_attributions\" : [],\n" +
                "   \"results\" : [\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 53.0080216,\n" +
                "               \"lng\" : -2.1809245\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 53.0092975302915,\n" +
                "                  \"lng\" : -2.17974425\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 53.00659956970851,\n" +
                "                  \"lng\" : -2.18350725\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/train-71.png\",\n" +
                "         \"id\" : \"f0583339273baf0283e297aac423b981f705b984\",\n" +
                "         \"name\" : \"Stoke-on-Trent\",\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 2916,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/115667899378922008733/photos\\\"\\u003eAdrian Lunsong\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CoQBdwAAAIAM_jmz8Cp-uKXHiyigbRi-Sq_1TyWEXwIT4gRNEVN36sOc597jddsjQ7OOfZKa9FpTGDTTvaYHHPj30TOLA2dlq7FvgbRVkx_bUNJx81PMdt15uTpKpPPHwgGgrFO8UiUDKqZ859nmk4wgGOSuDM_BdzsR2-XTxs5TH2HPLulEEhDUJgkAhBiPN2Y2jXNKmHT8GhQqYfIgcuukN-DmpXFADFiCR-Rkeg\",\n" +
                "               \"width\" : 3889\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJcT6knTZoekgRG9F7QEBgK4A\",\n" +
                "         \"rating\" : 3.7,\n" +
                "         \"reference\" : \"CmRSAAAA1aiuE3AwO0fb0yNydcwpjTwKnIBuh1DjJdTsiQTOnqsIAxO8N5Sc4gZ8Z9_PVdxuV75ovQulcfw6Jh2lRUdEw54-AnYpqtcUV9lCcgL_kYd3NEANxClHwXkOX020QhTfEhCWGQGxgighGlbS4a4fh6UQGhSqBngMgEhVOMgd_jmulEwqU0xb_A\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [\n" +
                "            \"train_station\",\n" +
                "            \"transit_station\",\n" +
                "            \"point_of_interest\",\n" +
                "            \"establishment\"\n" +
                "         ],\n" +
                "         \"vicinity\" : \"Station Road, Staffordshire, Stoke-on-Trent\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 53.04169,\n" +
                "               \"lng\" : -2.21622\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 53.04303898029151,\n" +
                "                  \"lng\" : -2.214871019708498\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 53.04034101970851,\n" +
                "                  \"lng\" : -2.217568980291502\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/train-71.png\",\n" +
                "         \"id\" : \"66dd6bda114521df358fbb96622bdd2ea4a8f5bd\",\n" +
                "         \"name\" : \"Longport\",\n" +
                "         \"place_id\" : \"ChIJH_4DUbdCekgRB3sswD5ixj0\",\n" +
                "         \"rating\" : 5,\n" +
                "         \"reference\" : \"CmRRAAAA7AXvBHa8PJddigZSovbw2uYulYWcAJKJx7myAU75IvmajsOZBf9CK_pWgCmANAbiTj1GBUjxBQCDF_8mIvakoJR6MAMA3l09VV8szxxTXOuup4DqKxNfN1U_ohjhf7JxEhD5rUaSO1Kh78JeQiUzu1pUGhTBWHqIsO0hnRPenfxYMYwRJ2xBig\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [\n" +
                "            \"train_station\",\n" +
                "            \"transit_station\",\n" +
                "            \"point_of_interest\",\n" +
                "            \"establishment\"\n" +
                "         ],\n" +
                "         \"vicinity\" : \"United Kingdom\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 52.98997,\n" +
                "               \"lng\" : -2.13701\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 52.9913189802915,\n" +
                "                  \"lng\" : -2.135661019708498\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 52.9886210197085,\n" +
                "                  \"lng\" : -2.138358980291502\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/train-71.png\",\n" +
                "         \"id\" : \"9ccec551b22daff8b5a33f062c80aef520159874\",\n" +
                "         \"name\" : \"Longton\",\n" +
                "         \"place_id\" : \"ChIJTeUfcZtpekgRUQ6NZkpPrLk\",\n" +
                "         \"rating\" : 4,\n" +
                "         \"reference\" : \"CmRSAAAA0BQRt9B07YrhJdKncctdT1xW7c_FS3oDzUxfPk5xjQsKzl2bnKaEpcuVzCL6839WMNOctOYkG_iS2yx5yKqn3k_718YoeLYI-uICpTLXnqjlULzzO2hd8bcCocfCY30oEhBaOespQz_7nEa83FbVTBnNGhRkx6abw69nIAVikQjVDvmcnKbM3g\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [\n" +
                "            \"train_station\",\n" +
                "            \"transit_station\",\n" +
                "            \"point_of_interest\",\n" +
                "            \"establishment\"\n" +
                "         ],\n" +
                "         \"vicinity\" : \"United Kingdom\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 53.08658,\n" +
                "               \"lng\" : -2.24482\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 53.0879289802915,\n" +
                "                  \"lng\" : -2.243471019708498\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 53.0852310197085,\n" +
                "                  \"lng\" : -2.246168980291502\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/train-71.png\",\n" +
                "         \"id\" : \"f8a565b1ea1b981907189495bd3e27d0e711589b\",\n" +
                "         \"name\" : \"Kidsgrove\",\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 2560,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/117577586874985069976/photos\\\"\\u003eChris Clarke\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CoQBdwAAAAvXezC-vGS_9fqghtjBCddasnsnPdpytNfmZhDdQ1tK-Ku_zwvJbCQpadGGDjRWGt4hrVSZIJZ3uodX5pKAIBOycPeL6e4eGGk1qclZpnihD9rdW3CAnuP0U4zq3ildlzBM-1FYw8sSEju6P8wNPuSM2R5JQq5uZ8P1xlsDB0IeEhBs0jqNp4WMP9354ZMLWW7uGhRWmpnqCiHTKlMnf3fpQgF7Zn_dYg\",\n" +
                "               \"width\" : 1536\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJCfJ8h8hcekgRimBDiiGBZKg\",\n" +
                "         \"rating\" : 3.7,\n" +
                "         \"reference\" : \"CmRSAAAAspGE9EkLmV0XqHAxoZBFaBuLDLA4PnByKpcKCEix1MyHrSObhtsT3SHyzp_flzGMPWSs7O_mdQsxh97jqV_BPPjpWA9gp7kakpli8Wrp3XQWdf3CLtl_xvtJ3a_uQh7XEhBz2j5-odUAjTGQUv8TI2vPGhSQmoHR4Dq1HSDDEKrf_Ab1WWJTaA\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [\n" +
                "            \"train_station\",\n" +
                "            \"transit_station\",\n" +
                "            \"point_of_interest\",\n" +
                "            \"establishment\"\n" +
                "         ],\n" +
                "         \"vicinity\" : \"United Kingdom\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 52.96816,\n" +
                "               \"lng\" : -2.06696\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 52.96950898029149,\n" +
                "                  \"lng\" : -2.065611019708498\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 52.96681101970849,\n" +
                "                  \"lng\" : -2.068308980291502\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/train-71.png\",\n" +
                "         \"id\" : \"ac98bbcd18ae6474846c536c16163d12d9682da6\",\n" +
                "         \"name\" : \"Blythe Bridge\",\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 2448,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/101524276782974723925/photos\\\"\\u003eJustin Williams\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CoQBdwAAANYwqw8zGOxo-vHsg-ttpqYmfyMlHTlBEQwdqNYAa6jhvBYIPrTQVJnthZztIKGfd8QBMhtCJX6SGtzJ33TomVvtmCz1lBBz-jYAbXvN5YFYxDkNU0l3y9KAqDvfk3ctmrkSzConLjHEQK4rt2ulWZdI_ic2_4TqXjtQEoakhOGBEhBU9w5VrRnx8kpEdq-JP03PGhSF1riVQ8xmwJMtfu6ysUEb_HUd2A\",\n" +
                "               \"width\" : 3264\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJEaVcFwprekgRgkSLAa4JIzk\",\n" +
                "         \"rating\" : 4.5,\n" +
                "         \"reference\" : \"CmRRAAAAVZdnNyK-sH_RxQ5_wfsETSKfrSn72hIsDlVAiOLJLTmojj7zK4Q2a9Y7G635a5b8ZLY8EBel8qlM64mMIHgrJGDy2qhENVOI9J7wcyTDtZ6F64hqZ3ejNDhEIYkCZeNFEhA6879TwUZztUQxskrBenPkGhSuza0bE4QR3Xny4VpW2jf0mmSLEg\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [\n" +
                "            \"train_station\",\n" +
                "            \"transit_station\",\n" +
                "            \"point_of_interest\",\n" +
                "            \"establishment\"\n" +
                "         ],\n" +
                "         \"vicinity\" : \"United Kingdom\"\n" +
                "      }\n" +
                "   ],\n" +
                "   \"status\" : \"OK\"\n" +
                "}\n";

        JSONObject json = new JSONObject(raw);
        JSONArray results = json.getJSONArray("results");

        JSONArray stations = new JSONArray();
        DatabaseHelper databaseHelper = new DatabaseHelper();

        for (int i = 0; i < results.length(); i++) {
            JSONObject station = new JSONObject();
            Station found = databaseHelper.getStation(results.getJSONObject(i).getString("name"), null, null);
            Assert.assertNotNull(found);

            if(found != null) {
                station.put("id", found.getId());
                stations.put(station);
            }
        }
        Assert.assertEquals(5, stations.length());

    }

    @Test
    public void toCity() throws Exception {
        String response = "{\n" +
                "   \"results\" : [\n" +
                "      {\n" +
                "         \"address_components\" : [\n" +
                "            {\n" +
                "               \"long_name\" : \"Liberty Court\",\n" +
                "               \"short_name\" : \"Liberty Court\",\n" +
                "               \"types\" : [ \"premise\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"Salisbury Avenue\",\n" +
                "               \"short_name\" : \"Salisbury Ave\",\n" +
                "               \"types\" : [ \"route\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"Stoke-on-Trent\",\n" +
                "               \"short_name\" : \"Stoke\",\n" +
                "               \"types\" : [ \"locality\", \"political\" ]\n" +
                "            }\n" +
                "         ]\n" +
                "       }\n" +
                "   ],\n" +
                "   \"status\" : \"OK\"\n" +
                "}\n";

        JSONObject locationObject = new JSONObject(response);
        JSONArray results = locationObject.getJSONArray("results");
        JSONObject firstResult = results.getJSONObject(0);
        JSONArray address = firstResult.getJSONArray("address_components");
        JSONObject locality = address.getJSONObject(2);
        String city = locality.getString("long_name");

        Assert.assertEquals("Stoke-on-Trent", city);
    }

    @Test
    public void toUser() throws Exception {
    }

    @Test
    public void toJourney() throws Exception {
        JSONObject data = new JSONObject("{\"journey\":{\"starred\":\"288b922dabaf400091cdc29155aef5d6\",\"count\":0,\"from\":{\"st\":null,\"at\":null,\"crs\":\"LPT\",\"name\":\"Longport\",\"et\":null},\"to\":{\"st\":null,\"at\":null,\"crs\":\"SOT\",\"name\":\"Stoke-on-Trent\",\"et\":null},\"history\":null,\"type\":\"journey\",\"user\":null}}");

        JSONObject json = data.getJSONObject("journey");
        JSONObject to = json.getJSONObject("to");
        JSONObject from = json.getJSONObject("from");

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

        Assert.assertNotNull("LPT", journey.getFrom().getCrs());
    }

    @Test
    public void toToken() throws Exception {
        JSONObject response = new JSONObject();
        response.put("token", "2323232nisndis_Dsmds9d8whd9&_skd89sgdh&Dg");
        response.put("user", 2);

        Assert.assertNotNull(response);
    }

    @Test
    public void journeysResponse() throws Exception {
        JSONObject response = new JSONObject();
        response.put("journeys", "[]");

        Assert.assertNotNull(response);
    }

    @Test
    public void journeyResponse() throws Exception {
        JSONObject response = new JSONObject();
        response.put("journeys", "{}");

        Assert.assertNotNull(response);
    }

    @Test
    public void checkResponse() throws Exception {
        JSONObject response = new JSONObject();
        response.put("found", "{}");
        response.put("id", "12121");

        Assert.assertNotNull(response);
    }

    @Test
    public void stationsResponse() throws Exception {
        JSONObject response = new JSONObject();
        response.put("stations", "[]");

        Assert.assertNotNull(response);
    }

    @Test
    public void userResponse() throws Exception {
        JSONObject response = new JSONObject();
        response.put("user", "{}");

        Assert.assertNotNull(response);
    }

    @Test
    public void existResponse() throws Exception {
        JSONObject response = new JSONObject();
        response.put("exist", "false");

        Assert.assertNotNull(response);
    }

}