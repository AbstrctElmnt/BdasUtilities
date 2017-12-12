import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class XmlReader {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        String activeobjects = "d:\\export\\activeobjects.xml";

        String xmlStr = new String(Files.readAllBytes(Paths.get(activeobjects)), StandardCharsets.UTF_8);
        //System.out.println(xmlStr);

        // parsing XML file to get as String using DOM Parser
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(new InputSource(new StringReader(xmlStr)));
        doc.getDocumentElement().normalize();
        System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
        NodeList list = doc.getElementsByTagName("table");
        System.out.println(list.getLength());
        for (int i = 0; i < list.getLength(); i++)
        {
            NamedNodeMap map = list.item(i).getAttributes();
            String name = map.item(0).getNodeValue();
            //System.out.println(map.item(0).getTextContent());
            System.out.println(name);
        }
    }
}

