package com.ovoc01.app.core.connection.reader;

import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.NodeList;

import com.ovoc01.app.core.tools.Utils;

/**
 * A utility class for reading and parsing XML files.
 */
public class XmlReader {
    
    /**
     * Reads an XML file and returns the root element of the document.
     *
     * @param filePath The path to the XML file to be read.
     * @return The root element of the XML document.
     * @throws Exception If the file does not exist or if there is an issue parsing the XML.
     */
    public static Element readXml(String filePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        File xmlFile = new File(filePath);
        if (!xmlFile.exists()) {
            throw new Exception("Create the database.xml file in your project root directory in " + Utils.currentLocation());
        }
        Document document = builder.parse(xmlFile);
        Element rootElement = document.getDocumentElement();
        return rootElement;
    }

    /**
     * Retrieves the text content of the first element with the given tag name within the provided XML element.
     *
     * @param element The XML element to search within.
     * @param tagName The tag name of the element to retrieve.
     * @return The text content of the first matching element or null if no match is found.
     */
    public static String getElementValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }

    /**
     * Retrieves the first child element with the specified tag name and 'dbname' attribute matching the given value.
     *
     * @param element The XML element to search within.
     * @param tagName The tag name of the child elements to search for.
     * @param dbname  The value to match in the 'dbname' attribute.
     * @return The first matching child element or null if no match is found.
     */
    public static Element getElement(Element element, String tagName, String dbname) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element el = (Element) nodeList.item(i);
                if (XmlReader.getElementValue(el, "dbname").equals(dbname)) {
                    return el;
                }
            }
        }
        return null;
    }
}
