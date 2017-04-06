package uk.co.giovannilenguito.factory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import uk.co.giovannilenguito.helper.DatabaseHelper;
import uk.co.giovannilenguito.model.Journey;
import uk.co.giovannilenguito.model.Station;
import uk.co.giovannilenguito.model.User;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by giovannilenguito on 04/04/2017.
 */
public class ParserFactoryTest {
    final private ParserFactory PARSER;
    private List<String> departingXmlStrings;
    private List<String> departingResults;

    private List<String> arrivalXmlStrings;
    private List<String> arrivalResults;

    public ParserFactoryTest() {
        PARSER = new ParserFactory();
    }

    private void getStrings(List<File> files, List<File> resultFiles, String type) throws IOException {
        for(int i = 0; i <= 1; i++){
            List<File> array;

            if(i == 0){
                array = files;
            }else{
                array = resultFiles;
            }

            for(int j = 0; j < array.size(); j++) {
                if (array.get(j) != null && i == 1 || i == 0) {
                    Reader fileReader = new FileReader(array.get(j));
                    BufferedReader bufferedReader = new BufferedReader(fileReader);

                    StringBuilder stringBuilder = new StringBuilder();
                    String line = bufferedReader.readLine();

                    while (line != null) {
                        stringBuilder.append(line).append("\n");
                        line = bufferedReader.readLine();
                    }

                    if(type.equals("dep")) {
                        if (i == 0) {
                            departingXmlStrings.add(stringBuilder.toString());
                        } else {
                            departingResults.add(stringBuilder.toString());
                        }
                    }else{
                        if (i == 0) {
                            arrivalXmlStrings.add(stringBuilder.toString());
                        } else {
                            arrivalResults.add(stringBuilder.toString());
                        }
                    }
                    bufferedReader.close();
                }else{
                    if(type.equals("dep")) {
                        departingResults.add("");
                    }else{
                        arrivalResults.add("");
                    }
                }
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        List<File> files = new ArrayList();
        List<File> resultFiles = new ArrayList();

        //Get data for departing services

        departingXmlStrings = new ArrayList();
        departingResults = new ArrayList();

        files.add(new File("src/test/java/uk/co/giovannilenguito/data/departing/xml/MultipleTrains.xml"));
        files.add(new File("src/test/java/uk/co/giovannilenguito/data/departing/xml/SingleTrain.xml"));
        files.add(new File("src/test/java/uk/co/giovannilenguito/data/departing/xml/MultipleTrainsWithSplit.xml"));
        files.add(new File("src/test/java/uk/co/giovannilenguito/data/departing/xml/SingleTrainWithSplit.xml"));
        files.add(new File("src/test/java/uk/co/giovannilenguito/data/xml/NoTrains.xml"));

        resultFiles.add(new File("src/test/java/uk/co/giovannilenguito/data/departing/expected/MultipleTrainsResult"));
        resultFiles.add(new File("src/test/java/uk/co/giovannilenguito/data/departing/expected/SingleTrainResult"));
        resultFiles.add(new File("src/test/java/uk/co/giovannilenguito/data/departing/expected/MultipleTrainsWithSplitResult"));
        resultFiles.add(new File("src/test/java/uk/co/giovannilenguito/data/departing/expected/SingleTrainWithSplitResult"));
        resultFiles.add(null);


        getStrings(files, resultFiles, "dep");

        //Get data for arrival services
        files.clear();
        resultFiles.clear();

        arrivalXmlStrings = new ArrayList();
        arrivalResults = new ArrayList();

        files.add(new File("src/test/java/uk/co/giovannilenguito/data/arriving/xml/MultipleTrainsArriving.xml"));
        files.add(new File("src/test/java/uk/co/giovannilenguito/data/arriving/xml/SingleTrainArriving.xml"));
        files.add(new File("src/test/java/uk/co/giovannilenguito/data/arriving/xml/MultipleTrainsWithSplitsArriving.xml"));
        files.add(new File("src/test/java/uk/co/giovannilenguito/data/arriving/xml/SingleTrainWithSplitArriving.xml"));
        files.add(new File("src/test/java/uk/co/giovannilenguito/data/xml/NoTrains.xml"));

        resultFiles.add(new File("src/test/java/uk/co/giovannilenguito/data/arriving/expected/MultipleTrainsArrivingResult"));
        resultFiles.add(new File("src/test/java/uk/co/giovannilenguito/data/arriving/expected/SingleTrainArrivingResult"));
        resultFiles.add(new File("src/test/java/uk/co/giovannilenguito/data/arriving/expected/MultipleTrainsWithSplitsArrivingResult"));
        resultFiles.add(new File("src/test/java/uk/co/giovannilenguito/data/arriving/expected/SingleTrainWithSplitArrivingResult"));
        resultFiles.add(null);


        getStrings(files, resultFiles, "arr");
    }

    @Test
    public void departureBoardServices() throws Exception {
        //If arrays are empty, something went wrong
        Assert.assertThat(departingXmlStrings.isEmpty(), is(false));
        Assert.assertThat(departingResults.isEmpty(), is(false));

        //Compare the expected json with the parsed json from xml
        for(int i = 0; i < departingXmlStrings.size(); i++){
            JSONObject json = PARSER.departureBoardServices(departingXmlStrings.get(i), "GetDepBoardWithDetailsResponse");

            if(i == departingXmlStrings.size() - 1){
                //Last array object should be string (the parse should return null)
                Assert.assertNull(json);
            }else{
                Assert.assertEquals(new JSONObject(departingResults.get(i)).toString(), json.toString());
            }
        }
    }

    @Test
    public void arrivalBoardServices() throws Exception {
        //If arrays are empty, something went wrong
        Assert.assertThat(arrivalXmlStrings.isEmpty(), is(false));
        Assert.assertThat(arrivalResults.isEmpty(), is(false));

        //Compare the expected json with the parsed json from xml
        for(int i = 0; i < arrivalXmlStrings.size(); i++){
            JSONObject json = PARSER.arrivalBoardServices(arrivalXmlStrings.get(i), "GetArrBoardWithDetailsResponse");

            if(i == arrivalXmlStrings.size() - 1){
                //Last array object should be string (the parse should return null)
                Assert.assertNull(json);
            }else{
                Assert.assertEquals(new JSONObject(arrivalResults.get(i)).toString(), json.toString());
            }
        }
    }

    @Test
    public void getStationMessage() throws Exception {
        String data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><soap:Body><GetDepBoardWithDetailsResponse xmlns=\"http://thalesgroup.com/RTTI/2016-02-16/ldb/\"><GetStationBoardResult xmlns:lt3=\"http://thalesgroup.com/RTTI/2015-05-14/ldb/types\" xmlns:lt5=\"http://thalesgroup.com/RTTI/2016-02-16/ldb/types\" xmlns:lt4=\"http://thalesgroup.com/RTTI/2015-11-27/ldb/types\" xmlns:lt=\"http://thalesgroup.com/RTTI/2012-01-13/ldb/types\" xmlns:lt2=\"http://thalesgroup.com/RTTI/2014-02-20/ldb/types\"><lt4:generatedAt>2017-04-06T19:18:53.0148353+01:00</lt4:generatedAt><lt4:locationName>London Euston</lt4:locationName><lt4:crs>EUS</lt4:crs><lt4:nrccMessages><lt:message>&lt;P&gt;Until mid-2017,&amp;nbsp;there will be a reduced escalator service at Euston London Underground station. During this time crowd control measures will be in place, please follow staff direction, and allow extra time for your journey. Alternatively please use nearby Kings Cross St Pancras (for the Victoria line and Northern line via Bank) or Warren Street (for the Northern line via Charing Cross).&lt;/P&gt;\n" +
                "&lt;P&gt;During this time the passageway leading from platforms 8-11 at London Euston to Euston London Underground station, will close&amp;nbsp;during busy times. For interchange between these services, customers must go via the National Rail concourse.&lt;/P&gt;</lt:message><lt:message>Disruption between Coventry and Birmingham. More information can be found in &lt;A href=\"http://twt.lt/ruk4\"&gt;Latest Travel News.&lt;/A&gt;</lt:message></lt4:nrccMessages><lt4:platformAvailable>true</lt4:platformAvailable><lt5:trainServices><lt5:service><lt4:std>19:17</lt4:std><lt4:etd>On time</lt4:etd><lt4:platform>9</lt4:platform><lt4:operator>London Overground</lt4:operator><lt4:operatorCode>LO</lt4:operatorCode><lt4:serviceType>train</lt4:serviceType><lt4:serviceID>9sFE8R27BOFyU2wx/wQmeg==</lt4:serviceID><lt5:origin><lt4:location><lt4:locationName>London Euston</lt4:locationName><lt4:crs>EUS</lt4:crs></lt4:location></lt5:origin><lt5:destination><lt4:location><lt4:locationName>Watford Junction</lt4:locationName><lt4:crs>WFJ</lt4:crs></lt4:location></lt5:destination><lt5:subsequentCallingPoints><lt4:callingPointList><lt4:callingPoint><lt4:locationName>South Hampstead</lt4:locationName><lt4:crs>SOH</lt4:crs><lt4:st>19:23</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Kilburn High Road</lt4:locationName><lt4:crs>KBN</lt4:crs><lt4:st>19:24</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Queens Park (London)</lt4:locationName><lt4:crs>QPW</lt4:crs><lt4:st>19:26</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Kensal Green</lt4:locationName><lt4:crs>KNL</lt4:crs><lt4:st>19:28</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Willesden Junction</lt4:locationName><lt4:crs>WIJ</lt4:crs><lt4:st>19:31</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Harlesden</lt4:locationName><lt4:crs>HDN</lt4:crs><lt4:st>19:33</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Stonebridge Park</lt4:locationName><lt4:crs>SBP</lt4:crs><lt4:st>19:35</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Wembley Central</lt4:locationName><lt4:crs>WMB</lt4:crs><lt4:st>19:38</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>North Wembley</lt4:locationName><lt4:crs>NWB</lt4:crs><lt4:st>19:40</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>South Kenton</lt4:locationName><lt4:crs>SOK</lt4:crs><lt4:st>19:42</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Kenton</lt4:locationName><lt4:crs>KNT</lt4:crs><lt4:st>19:44</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Harrow &amp; Wealdstone</lt4:locationName><lt4:crs>HRW</lt4:crs><lt4:st>19:46</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Headstone Lane</lt4:locationName><lt4:crs>HDL</lt4:crs><lt4:st>19:49</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Hatch End</lt4:locationName><lt4:crs>HTE</lt4:crs><lt4:st>19:51</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Carpenders Park</lt4:locationName><lt4:crs>CPK</lt4:crs><lt4:st>19:54</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Bushey</lt4:locationName><lt4:crs>BSH</lt4:crs><lt4:st>19:57</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Watford High Street</lt4:locationName><lt4:crs>WFH</lt4:crs><lt4:st>20:00</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Watford Junction</lt4:locationName><lt4:crs>WFJ</lt4:crs><lt4:st>20:04</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint></lt4:callingPointList></lt5:subsequentCallingPoints></lt5:service><lt5:service><lt4:std>19:20</lt4:std><lt4:etd>On time</lt4:etd><lt4:operator>Virgin Trains</lt4:operator><lt4:operatorCode>VT</lt4:operatorCode><lt4:serviceType>train</lt4:serviceType><lt4:serviceID>IZDBacg6gEOtCgA2Bsw4lw==</lt4:serviceID><lt5:origin><lt4:location><lt4:locationName>London Euston</lt4:locationName><lt4:crs>EUS</lt4:crs></lt4:location></lt5:origin><lt5:destination><lt4:location><lt4:locationName>Manchester Piccadilly</lt4:locationName><lt4:crs>MAN</lt4:crs></lt4:location></lt5:destination><lt5:subsequentCallingPoints><lt4:callingPointList><lt4:callingPoint><lt4:locationName>Stoke-on-Trent</lt4:locationName><lt4:crs>SOT</lt4:crs><lt4:st>20:49</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Macclesfield</lt4:locationName><lt4:crs>MAC</lt4:crs><lt4:st>21:06</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Stockport</lt4:locationName><lt4:crs>SPT</lt4:crs><lt4:st>21:19</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Manchester Piccadilly</lt4:locationName><lt4:crs>MAN</lt4:crs><lt4:st>21:31</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint></lt4:callingPointList></lt5:subsequentCallingPoints></lt5:service><lt5:service><lt4:std>19:23</lt4:std><lt4:etd>On time</lt4:etd><lt4:platform>3</lt4:platform><lt4:operator>Virgin Trains</lt4:operator><lt4:operatorCode>VT</lt4:operatorCode><lt4:serviceType>train</lt4:serviceType><lt4:serviceID>efWq6NTvz2C8qk41vGlQIA==</lt4:serviceID><lt5:rsid>VT540000</lt5:rsid><lt5:origin><lt4:location><lt4:locationName>London Euston</lt4:locationName><lt4:crs>EUS</lt4:crs></lt4:location></lt5:origin><lt5:destination><lt4:location><lt4:locationName>Wolverhampton</lt4:locationName><lt4:crs>WVH</lt4:crs></lt4:location></lt5:destination><lt5:subsequentCallingPoints><lt4:callingPointList><lt4:callingPoint><lt4:locationName>Coventry</lt4:locationName><lt4:crs>COV</lt4:crs><lt4:st>20:22</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Birmingham International</lt4:locationName><lt4:crs>BHI</lt4:crs><lt4:st>20:33</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Birmingham New Street</lt4:locationName><lt4:crs>BHM</lt4:crs><lt4:st>20:45</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Sandwell &amp; Dudley</lt4:locationName><lt4:crs>SAD</lt4:crs><lt4:st>20:58</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Wolverhampton</lt4:locationName><lt4:crs>WVH</lt4:crs><lt4:st>21:12</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint></lt4:callingPointList></lt5:subsequentCallingPoints></lt5:service><lt5:service><lt4:std>19:24</lt4:std><lt4:etd>On time</lt4:etd><lt4:platform>8</lt4:platform><lt4:operator>London Midland</lt4:operator><lt4:operatorCode>LM</lt4:operatorCode><lt4:serviceType>train</lt4:serviceType><lt4:serviceID>8gUlHb6NavtQozpnROmH3A==</lt4:serviceID><lt5:origin><lt4:location><lt4:locationName>London Euston</lt4:locationName><lt4:crs>EUS</lt4:crs></lt4:location></lt5:origin><lt5:destination><lt4:location><lt4:locationName>Milton Keynes Central</lt4:locationName><lt4:crs>MKC</lt4:crs></lt4:location></lt5:destination><lt5:subsequentCallingPoints><lt4:callingPointList><lt4:callingPoint><lt4:locationName>Watford Junction</lt4:locationName><lt4:crs>WFJ</lt4:crs><lt4:st>19:40</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Hemel Hempstead</lt4:locationName><lt4:crs>HML</lt4:crs><lt4:st>19:50</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Berkhamsted</lt4:locationName><lt4:crs>BKM</lt4:crs><lt4:st>19:55</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Tring</lt4:locationName><lt4:crs>TRI</lt4:crs><lt4:st>20:01</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Cheddington</lt4:locationName><lt4:crs>CED</lt4:crs><lt4:st>20:07</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Leighton Buzzard</lt4:locationName><lt4:crs>LBZ</lt4:crs><lt4:st>20:12</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Bletchley</lt4:locationName><lt4:crs>BLY</lt4:crs><lt4:st>20:18</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Milton Keynes Central</lt4:locationName><lt4:crs>MKC</lt4:crs><lt4:st>20:24</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint></lt4:callingPointList></lt5:subsequentCallingPoints></lt5:service><lt5:service><lt4:std>19:30</lt4:std><lt4:etd>On time</lt4:etd><lt4:platform>5</lt4:platform><lt4:operator>Virgin Trains</lt4:operator><lt4:operatorCode>VT</lt4:operatorCode><lt4:serviceType>train</lt4:serviceType><lt4:serviceID>dGWZTwbc2KwcHuVVtsa6UQ==</lt4:serviceID><lt5:origin><lt4:location><lt4:locationName>London Euston</lt4:locationName><lt4:crs>EUS</lt4:crs></lt4:location></lt5:origin><lt5:destination><lt4:location><lt4:locationName>Glasgow Central</lt4:locationName><lt4:crs>GLC</lt4:crs></lt4:location></lt5:destination><lt5:subsequentCallingPoints><lt4:callingPointList><lt4:callingPoint><lt4:locationName>Warrington Bank Quay</lt4:locationName><lt4:crs>WBQ</lt4:crs><lt4:st>21:16</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Wigan North Western</lt4:locationName><lt4:crs>WGN</lt4:crs><lt4:st>21:27</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Preston</lt4:locationName><lt4:crs>PRE</lt4:crs><lt4:st>21:39</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Lancaster</lt4:locationName><lt4:crs>LAN</lt4:crs><lt4:st>21:55</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Oxenholme Lake District</lt4:locationName><lt4:crs>OXN</lt4:crs><lt4:st>22:09</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Penrith</lt4:locationName><lt4:crs>PNR</lt4:crs><lt4:st>22:34</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Carlisle</lt4:locationName><lt4:crs>CAR</lt4:crs><lt4:st>22:50</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Glasgow Central</lt4:locationName><lt4:crs>GLC</lt4:crs><lt4:st>00:05</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint></lt4:callingPointList></lt5:subsequentCallingPoints></lt5:service><lt5:service><lt4:std>19:34</lt4:std><lt4:etd>On time</lt4:etd><lt4:operator>London Midland</lt4:operator><lt4:operatorCode>LM</lt4:operatorCode><lt4:serviceType>train</lt4:serviceType><lt4:serviceID>lPSgNGW4ckTHUJtCPusqmQ==</lt4:serviceID><lt5:origin><lt4:location><lt4:locationName>London Euston</lt4:locationName><lt4:crs>EUS</lt4:crs></lt4:location></lt5:origin><lt5:destination><lt4:location><lt4:locationName>Tring</lt4:locationName><lt4:crs>TRI</lt4:crs></lt4:location></lt5:destination><lt5:subsequentCallingPoints><lt4:callingPointList><lt4:callingPoint><lt4:locationName>Harrow &amp; Wealdstone</lt4:locationName><lt4:crs>HRW</lt4:crs><lt4:st>19:46</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Bushey</lt4:locationName><lt4:crs>BSH</lt4:crs><lt4:st>19:51</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Watford Junction</lt4:locationName><lt4:crs>WFJ</lt4:crs><lt4:st>19:54</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Kings Langley</lt4:locationName><lt4:crs>KGL</lt4:crs><lt4:st>19:59</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Apsley</lt4:locationName><lt4:crs>APS</lt4:crs><lt4:st>20:03</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Hemel Hempstead</lt4:locationName><lt4:crs>HML</lt4:crs><lt4:st>20:06</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Berkhamsted</lt4:locationName><lt4:crs>BKM</lt4:crs><lt4:st>20:10</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Tring</lt4:locationName><lt4:crs>TRI</lt4:crs><lt4:st>20:17</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint></lt4:callingPointList></lt5:subsequentCallingPoints></lt5:service><lt5:service><lt4:std>19:37</lt4:std><lt4:etd>On time</lt4:etd><lt4:platform>9</lt4:platform><lt4:operator>London Overground</lt4:operator><lt4:operatorCode>LO</lt4:operatorCode><lt4:serviceType>train</lt4:serviceType><lt4:serviceID>byimx9q/0Uy7geXtDkcrZg==</lt4:serviceID><lt5:origin><lt4:location><lt4:locationName>London Euston</lt4:locationName><lt4:crs>EUS</lt4:crs></lt4:location></lt5:origin><lt5:destination><lt4:location><lt4:locationName>Watford Junction</lt4:locationName><lt4:crs>WFJ</lt4:crs></lt4:location></lt5:destination><lt5:subsequentCallingPoints><lt4:callingPointList><lt4:callingPoint><lt4:locationName>South Hampstead</lt4:locationName><lt4:crs>SOH</lt4:crs><lt4:st>19:43</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Kilburn High Road</lt4:locationName><lt4:crs>KBN</lt4:crs><lt4:st>19:44</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Queens Park (London)</lt4:locationName><lt4:crs>QPW</lt4:crs><lt4:st>19:46</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Kensal Green</lt4:locationName><lt4:crs>KNL</lt4:crs><lt4:st>19:48</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Willesden Junction</lt4:locationName><lt4:crs>WIJ</lt4:crs><lt4:st>19:51</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Harlesden</lt4:locationName><lt4:crs>HDN</lt4:crs><lt4:st>19:53</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Stonebridge Park</lt4:locationName><lt4:crs>SBP</lt4:crs><lt4:st>19:55</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Wembley Central</lt4:locationName><lt4:crs>WMB</lt4:crs><lt4:st>19:58</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>North Wembley</lt4:locationName><lt4:crs>NWB</lt4:crs><lt4:st>20:00</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>South Kenton</lt4:locationName><lt4:crs>SOK</lt4:crs><lt4:st>20:02</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Kenton</lt4:locationName><lt4:crs>KNT</lt4:crs><lt4:st>20:04</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Harrow &amp; Wealdstone</lt4:locationName><lt4:crs>HRW</lt4:crs><lt4:st>20:06</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Headstone Lane</lt4:locationName><lt4:crs>HDL</lt4:crs><lt4:st>20:09</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Hatch End</lt4:locationName><lt4:crs>HTE</lt4:crs><lt4:st>20:11</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Carpenders Park</lt4:locationName><lt4:crs>CPK</lt4:crs><lt4:st>20:14</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Bushey</lt4:locationName><lt4:crs>BSH</lt4:crs><lt4:st>20:17</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Watford High Street</lt4:locationName><lt4:crs>WFH</lt4:crs><lt4:st>20:20</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Watford Junction</lt4:locationName><lt4:crs>WFJ</lt4:crs><lt4:st>20:24</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint></lt4:callingPointList></lt5:subsequentCallingPoints></lt5:service><lt5:service><lt4:std>19:40</lt4:std><lt4:etd>On time</lt4:etd><lt4:operator>Virgin Trains</lt4:operator><lt4:operatorCode>VT</lt4:operatorCode><lt4:serviceType>train</lt4:serviceType><lt4:serviceID>EuIWXG879k2iOwvd5x/v7A==</lt4:serviceID><lt5:origin><lt4:location><lt4:locationName>London Euston</lt4:locationName><lt4:crs>EUS</lt4:crs></lt4:location></lt5:origin><lt5:destination><lt4:location><lt4:locationName>Manchester Piccadilly</lt4:locationName><lt4:crs>MAN</lt4:crs><lt4:via>via Wilmslow</lt4:via></lt4:location></lt5:destination><lt5:subsequentCallingPoints><lt4:callingPointList><lt4:callingPoint><lt4:locationName>Tamworth</lt4:locationName><lt4:crs>TAM</lt4:crs><lt4:st>20:42</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Lichfield Trent Valley</lt4:locationName><lt4:crs>LTV</lt4:crs><lt4:st>20:49</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Stafford</lt4:locationName><lt4:crs>STA</lt4:crs><lt4:st>21:02</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Crewe</lt4:locationName><lt4:crs>CRE</lt4:crs><lt4:st>21:21</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Wilmslow</lt4:locationName><lt4:crs>WML</lt4:crs><lt4:st>21:38</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Manchester Piccadilly</lt4:locationName><lt4:crs>MAN</lt4:crs><lt4:st>21:57</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint></lt4:callingPointList></lt5:subsequentCallingPoints></lt5:service><lt5:service><lt4:std>19:43</lt4:std><lt4:etd>On time</lt4:etd><lt4:operator>Virgin Trains</lt4:operator><lt4:operatorCode>VT</lt4:operatorCode><lt4:serviceType>train</lt4:serviceType><lt4:serviceID>3BfA7D+SJba89SjrbudcHQ==</lt4:serviceID><lt5:origin><lt4:location><lt4:locationName>London Euston</lt4:locationName><lt4:crs>EUS</lt4:crs></lt4:location></lt5:origin><lt5:destination><lt4:location><lt4:locationName>Preston</lt4:locationName><lt4:crs>PRE</lt4:crs></lt4:location></lt5:destination><lt5:subsequentCallingPoints><lt4:callingPointList><lt4:callingPoint><lt4:locationName>Milton Keynes Central</lt4:locationName><lt4:crs>MKC</lt4:crs><lt4:st>20:13</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Coventry</lt4:locationName><lt4:crs>COV</lt4:crs><lt4:st>20:42</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Birmingham International</lt4:locationName><lt4:crs>BHI</lt4:crs><lt4:st>20:53</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Birmingham New Street</lt4:locationName><lt4:crs>BHM</lt4:crs><lt4:st>21:08</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Sandwell &amp; Dudley</lt4:locationName><lt4:crs>SAD</lt4:crs><lt4:st>21:24</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Wolverhampton</lt4:locationName><lt4:crs>WVH</lt4:crs><lt4:st>21:37</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Stafford</lt4:locationName><lt4:crs>STA</lt4:crs><lt4:st>21:52</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Crewe</lt4:locationName><lt4:crs>CRE</lt4:crs><lt4:st>22:16</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Warrington Bank Quay</lt4:locationName><lt4:crs>WBQ</lt4:crs><lt4:st>22:35</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Wigan North Western</lt4:locationName><lt4:crs>WGN</lt4:crs><lt4:st>22:46</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Preston</lt4:locationName><lt4:crs>PRE</lt4:crs><lt4:st>23:00</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint></lt4:callingPointList></lt5:subsequentCallingPoints></lt5:service><lt5:service><lt4:std>19:46</lt4:std><lt4:etd>On time</lt4:etd><lt4:operator>London Midland</lt4:operator><lt4:operatorCode>LM</lt4:operatorCode><lt4:serviceType>train</lt4:serviceType><lt4:serviceID>Virld71LIvCQ2uKkUKwgmA==</lt4:serviceID><lt5:origin><lt4:location><lt4:locationName>London Euston</lt4:locationName><lt4:crs>EUS</lt4:crs></lt4:location></lt5:origin><lt5:destination><lt4:location><lt4:locationName>Crewe</lt4:locationName><lt4:crs>CRE</lt4:crs></lt4:location></lt5:destination><lt5:subsequentCallingPoints><lt4:callingPointList><lt4:callingPoint><lt4:locationName>Milton Keynes Central</lt4:locationName><lt4:crs>MKC</lt4:crs><lt4:st>20:18</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Rugby</lt4:locationName><lt4:crs>RUG</lt4:crs><lt4:st>20:41</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Nuneaton</lt4:locationName><lt4:crs>NUN</lt4:crs><lt4:st>20:53</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Atherstone</lt4:locationName><lt4:crs>ATH</lt4:crs><lt4:st>21:06</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Tamworth</lt4:locationName><lt4:crs>TAM</lt4:crs><lt4:st>21:14</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Lichfield Trent Valley</lt4:locationName><lt4:crs>LTV</lt4:crs><lt4:st>21:21</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Rugeley Trent Valley</lt4:locationName><lt4:crs>RGL</lt4:crs><lt4:st>21:29</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Stafford</lt4:locationName><lt4:crs>STA</lt4:crs><lt4:st>21:39</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Stone</lt4:locationName><lt4:crs>SNE</lt4:crs><lt4:st>21:50</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Stoke-on-Trent</lt4:locationName><lt4:crs>SOT</lt4:crs><lt4:st>22:01</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Kidsgrove</lt4:locationName><lt4:crs>KDG</lt4:crs><lt4:st>22:10</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Alsager</lt4:locationName><lt4:crs>ASG</lt4:crs><lt4:st>22:15</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint><lt4:callingPoint><lt4:locationName>Crewe</lt4:locationName><lt4:crs>CRE</lt4:crs><lt4:st>22:24</lt4:st><lt4:et>On time</lt4:et></lt4:callingPoint></lt4:callingPointList></lt5:subsequentCallingPoints></lt5:service></lt5:trainServices></GetStationBoardResult></GetDepBoardWithDetailsResponse></soap:Body></soap:Envelope>";

        String type = "GetDepBoardWithDetailsResponse";
        String expectedMessage = "<P>Until mid-2017,&nbsp;there will be a reduced escalator service at Euston London Underground station. During this time crowd control measures will be in place, please follow staff direction, and allow extra time for your journey. Alternatively please use nearby Kings Cross St Pancras (for the Victoria line and Northern line via Bank) or Warren Street (for the Northern line via Charing Cross).</P>\n" +
                "<P>During this time the passageway leading from platforms 8-11 at London Euston to Euston London Underground station, will close&nbsp;during busy times. For interchange between these services, customers must go via the National Rail concourse.</P> Disruption between Coventry and Birmingham. More information can be found in <A href=\"http://twt.lt/ruk4\">Latest Travel News.</A>";

        //Convert result into json
        final JSONObject rawJson = XML.toJSONObject(data);

        //Get message from rawJson
        final JSONObject envelope = rawJson.getJSONObject("soap:Envelope");
        final JSONObject body = envelope.getJSONObject("soap:Body");
        final JSONObject response = body.getJSONObject(type);
        final JSONObject results = response.getJSONObject("GetStationBoardResult");

        if (!results.isNull("lt4:nrccMessages")) {
            final JSONObject messages = results.getJSONObject("lt4:nrccMessages");
            final Object messageObject = new JSONTokener(messages.get("lt:message").toString()).nextValue();

            if (messageObject instanceof String) {
                Assert.assertEquals(expectedMessage, messages.getString("lt:message"));
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
                Assert.assertEquals(expectedMessage, message);
            }
        } else {
           Assert.assertThat(results.isNull("lt4:nrccMessages"), is(true));
        }
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

        String jsonString = "{\"fromStations\":[],\"lastName\":\"1\",\"image\":\"\",\"lastLogin\":null,\"rev\":null,\"type\":\"user\",\"firstName\":\"\",\"password\":\"18b1c941afc7d74478865b197b214f03\",\"dateCreated\":1491501749,\"journeyHistory\":[],\"toStations\":[],\"starredJourneys\":[],\"email\":\"giovanni09@live.co.uk\",\"username\":\"giovanni09@live.co.uk\"}";

        JSONObject json = new JSONObject(jsonString );

        User user = new User();

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


        user.set_rev("121212");
        user.setImage("");
        user.setToStations(null);
        user.setToStations(null);
        user.setJourneyHistory(null);
        user.setStarredJourneys(null);

        Assert.assertNotNull(user);
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


        Assert.assertNotNull(journey);
    }

    @Test
    public void toToken() throws Exception {
        JSONObject expected = new JSONObject("{\"user\":2,\"token\":\"2323232nisndis_Dsmds9d8whd9&_skd89sgdh&Dg\"}");
        JSONObject response = new JSONObject();
        response.put("token", "2323232nisndis_Dsmds9d8whd9&_skd89sgdh&Dg");
        response.put("user", 2);

        Assert.assertEquals(expected.toString(), response.toString());
    }

    @Test
    public void journeysResponse() throws Exception {
        JSONObject response = new JSONObject();
        response.put("journeys", "[]");

        Assert.assertEquals("{\"journeys\":\"[]\"}", response.toString());
    }

    @Test
    public void journeyResponse() throws Exception {
        JSONObject response = new JSONObject();
        response.put("journeys", "{}");

        Assert.assertEquals("{\"journeys\":\"{}\"}", response.toString());
    }

    @Test
    public void checkResponse() throws Exception {
        JSONObject response = new JSONObject();
        response.put("found", "{}");
        response.put("id", "12121");

        Assert.assertEquals("{\"found\":\"{}\",\"id\":\"12121\"}", response.toString());
    }

    @Test
    public void stationsResponse() throws Exception {
        JSONObject response = new JSONObject();
        response.put("stations", "[]");

        Assert.assertEquals("{\"stations\":\"[]\"}", response.toString());
    }

    @Test
    public void userResponse() throws Exception {
        JSONObject response = new JSONObject();
        response.put("user", "{}");

        Assert.assertEquals("{\"user\":\"{}\"}", response.toString());
    }

    @Test
    public void existResponse() throws Exception {
        JSONObject response = new JSONObject();
        response.put("exist", "false");

        Assert.assertEquals("{\"exist\":\"false\"}", response.toString());
    }

}