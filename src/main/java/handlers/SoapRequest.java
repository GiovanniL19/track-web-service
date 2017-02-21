package handlers;

import javax.xml.soap.*;

/**
 * Created by giovannilenguito on 10/02/2017.
 */
public class SoapRequest {
    /*
    // REMEMBER
    // In the message body, the parent ldb comes from the OpenLDBWS site.
    // However, ass the word Request on the end for the message.
    // To get trains for a route, use the 'crs' as the origin and 'filterCrs' as the destination
    */

    private final String LDBWS_URL = "https://lite.realtime.nationalrail.co.uk/OpenLDBWS/ldb9.asmx";
    private final String LDBWS_TOKEN = "9e69bf8c-f691-4bb8-b28e-9ba048cf66d4";

    private final String TYP = "http://thalesgroup.com/RTTI/2013-11-28/Token/types";
    private final String LDB = "http://thalesgroup.com/RTTI/2016-02-16/ldb/";
    private final String PREFIX = "soap";
    private final String NAMESPACE = "env";

    private SOAPMessage soapMessage;
    private SOAPPart soapPart;
    private SOAPEnvelope envelope;

    public SoapRequest() throws Exception {
        //Create message
        soapMessage = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
        soapMessage.getSOAPPart().getEnvelope().setPrefix(PREFIX);
        soapMessage.getSOAPPart().getEnvelope().removeNamespaceDeclaration(NAMESPACE);
        soapMessage.getSOAPHeader().setPrefix(PREFIX);
        soapMessage.getSOAPBody().setPrefix(PREFIX);

        //SOAP part
        soapPart = soapMessage.getSOAPPart();

        //Set up SOAP envelope
        envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("typ", TYP);
        envelope.addNamespaceDeclaration("ldb", LDB);
    }

    //Call this method to get a response from the web service
    public SOAPMessage execute(SOAPMessage message) throws Exception{
        //SOAP Connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        //Send SOAP message to server and get response
        SOAPMessage soapResponse = soapConnection.call(message, LDBWS_URL);

        //Close connection
        soapConnection.close();

        return soapResponse;
    }

    //To create a soap message to execute, use the appropriate create method below
    public SOAPMessage createBoardWithDetailsMessage(String request, String numRows, String crs, String filterCrs, String filterType, String timeOffset, String timeWindow) throws Exception{
        // SOAP Body
        SOAPBody soapBody = envelope.getBody();

        //New Parent Element
        SOAPElement soapBodyElem = soapBody.addChildElement(request, "ldb");

        //First new child element
        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("numRows", "ldb");
        //Value of child element
        soapBodyElem1.addTextNode(numRows);
        //New child element
        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("crs", "ldb");
        //Value of child element
        soapBodyElem2.addTextNode(crs);
        //New child element
        SOAPElement soapBodyElemFC = soapBodyElem.addChildElement("filterCrs", "ldb");
        //Value of child element
        soapBodyElemFC.addTextNode(filterCrs);
        //New child element
        SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("filterType", "ldb");
        //Value of child element
        soapBodyElem3.addTextNode(filterType);
        //New child element
        SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("timeOffset", "ldb");
        //Value of child element
        soapBodyElem4.addTextNode(timeOffset);
        //New child element
        SOAPElement soapBodyElem5 = soapBodyElem.addChildElement("timeWindow", "ldb");
        //Value of child element
        soapBodyElem5.addTextNode(timeWindow);


        SOAPHeader soapHeader = envelope.getHeader();
        //New header Element
        SOAPElement accessTokenElm = soapHeader.addChildElement("AccessToken", "typ");
        //New header Element
        SOAPElement tokenElm = accessTokenElm.addChildElement("TokenValue", "typ");
        tokenElm.addTextNode(LDBWS_TOKEN);

        //Save message
        soapMessage.saveChanges();
        //Return
        return soapMessage;
    }

    public SOAPMessage createServiceDetailsMessage(String serviceID) throws Exception{
        // SOAP Body
        SOAPBody soapBody = envelope.getBody();

        //New Parent Element
        SOAPElement soapBodyElem = soapBody.addChildElement("GetServiceDetailsRequest", "ldb");

        //First new child element
        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("serviceID", "ldb");
        //Value of child element
        soapBodyElem1.addTextNode(serviceID);


        SOAPHeader soapHeader = envelope.getHeader();
        //New header Element
        SOAPElement accessTokenElm = soapHeader.addChildElement("AccessToken", "typ");
        //New header Element
        SOAPElement tokenElm = accessTokenElm.addChildElement("TokenValue", "typ");
        tokenElm.addTextNode(LDBWS_TOKEN);

        //Save message
        soapMessage.saveChanges();
        //Return
        return soapMessage;
    }
}
