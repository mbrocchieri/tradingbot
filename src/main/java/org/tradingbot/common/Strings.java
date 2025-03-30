package org.tradingbot.common;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class Strings {
    private Strings() {}
    public static String prettyFormatXml(String xml) {
        xml = StringUtils.replace(xml, "\n", "");
        xml = StringUtils.replace(xml, "\t", "");
        try(var is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))){
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            // initialize StreamResult with File object to save to file
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);
            return result.getWriter().toString();

        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
