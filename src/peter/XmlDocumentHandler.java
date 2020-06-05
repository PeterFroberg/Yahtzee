/**
 *
 * @author  Peter Fröberg, pefr7147@student.su.se
 * @version 1.0
 * @since   2020-06-04
 */

package peter;

import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.StringReader;

public class XmlDocumentHandler {

    public XmlDocumentHandler(){
    }

    /**
     * Creates a XMLString by first create a XML document and then convert it to a string
     * @param code - communication code/command
     * @param player - player object
     * @param body  - message to be sent
     * @param game  - game object
     * @return - a XML document converted to a string representation
     */
    public String createXmlString(String code, Player player, String body, Game game){
        Element messageElement = new Element("message");
        Element headerElement = new Element("header");
        Element protocolElement = new Element("protocol");
        Element typeElement = new Element("type");
        Element versionElement = new Element("version");
        Element codeElement = new Element("code");
        Element idElement = new Element("id");
        Element nameElement = new Element("name");
        Element emailElement = new Element("email");
        Element gamenumElement = new Element("gamenum");
        Element playerpositionElement = new Element("playerposition");
        Element bodyElement = new Element("body");

        //Set end-node values
        typeElement.addContent("YCP");
        versionElement.addContent("1.0");
        codeElement.addContent(code);
        if (player != null) {
            nameElement.addContent(player.getName());
            emailElement.addContent(player.getEmail());
            gamenumElement.addContent(String.valueOf(game.getID()));
        }else{
            nameElement.addContent("");
            emailElement.addContent("");
            gamenumElement.addContent("");
        }

        playerpositionElement.addContent(String.valueOf(game.getPositionInGame()));
        bodyElement.addContent(body);

        //Combine elements
        messageElement.addContent(headerElement);
        messageElement.addContent(codeElement);
        messageElement.addContent(bodyElement);
        headerElement.addContent(protocolElement);
        headerElement.addContent(idElement);
        protocolElement.addContent(typeElement);
        protocolElement.addContent(versionElement);
        idElement.addContent(nameElement);
        idElement.addContent(emailElement);
        idElement.addContent(gamenumElement);
        idElement.addContent(playerpositionElement);

        //Define document Type
        DocType docType = new DocType("message", null, "http://lejonqvist.se/yatzy.dtd");

        //Return and convert the document to a String representation
        return convertToString(new Document(messageElement, docType));
    }

    /**
     * convert a XML document to a string representation in compact format and "" as line separator
     * @param doc
     * @return
     */
    public String convertToString(Document doc){
        Format format = Format.getCompactFormat();
        format.setLineSeparator("");
        XMLOutputter xmlOutputter = new XMLOutputter(format);

        return xmlOutputter.outputString(doc);
    }

    /**
     * parse a XML string vy converting the XML string to a document using SAXBuilder and verify that the
     * XML follows the rules set in the yatzy.dtd file, if XML don't follow the "yatzy.dtd" requirements
     * a blank string is returned
     * @param xmlString
     * @param attribute
     * @return
     */
    public String parseXml(String xmlString, String attribute){
        SAXBuilder saxBuilder = new SAXBuilder(XMLReaders.DTDVALIDATING);
        try {
            Document doc = saxBuilder.build(new StringReader(xmlString));
            Element root = doc.getRootElement();
            return root.getChildText(attribute);
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
