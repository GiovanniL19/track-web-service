package Handlers;

import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;

import org.json.JSONObject;
import org.json.XML;

/**
 * Created by giovannilenguito on 10/02/2017.
 */
public class Parse {

    public JSONObject xmlToJson(SOAPMessage xml) throws Exception{
        //Sets up transformer
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        //Get xml content and puts in source
        Source sourceContent = xml.getSOAPPart().getContent();

        final ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
        final StreamResult result = new StreamResult(streamOut);

        transformer.transform(sourceContent, result);

        //Convert result into json and return
        return XML.toJSONObject(streamOut.toString());
    }
}
