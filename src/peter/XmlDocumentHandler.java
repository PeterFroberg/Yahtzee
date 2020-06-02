package peter;

import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.StringReader;

public class XmlDocumentHandler {
    XmlDocumentHandler(){
    }

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

        //Set endnode values
        typeElement.addContent("YCP");
        versionElement.addContent("1.0");
        codeElement.addContent(code);
        nameElement.addContent(player.getName());
        emailElement.addContent(player.getEmail());
        gamenumElement.addContent(String.valueOf(game.getID()));
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

        DocType docType = new DocType("message", null, "src/peter/Yatzy.dtd");

        return convertToString(new Document(messageElement, docType));
    }

    public String convertToString(Document doc){
        Format format = Format.getCompactFormat();
        format.setLineSeparator("");
        XMLOutputter xmlOutputter = new XMLOutputter(format);

        return xmlOutputter.outputString(doc);
    }

    public String parseXml(String xmlString, String attribute){
        SAXBuilder saxBuilder = new SAXBuilder();
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
