package com.github.t1.xml;

import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.ls.*;
import org.xml.sax.SAXException;

public class Xml extends XmlElement {
    private static Document loadDocument(URI uri) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(uri.toASCIIString());
            document.setDocumentURI(uri.toString());
            return document;
            // JsonObject obj = Json.createReader(uri.toURL().openStream()).readObject();
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Xml load(URI uri) {
        return new Xml(loadDocument(uri));
    }

    public Xml(Document document) {
        super(document.getDocumentElement());
    }

    public void save() {
        Document document = document();
        serializer().writeToURI(document, document.getDocumentURI());
    }

    @Override
    public String toString() {
        return super.toString() + ":\n" + serializer().writeToString(document());
    }

    private LSSerializer serializer() {
        DOMImplementationLS ls = (DOMImplementationLS) (document().getImplementation()).getFeature("LS", "3.0");
        if (ls == null)
            throw new UnsupportedOperationException("dom load and save not supported");
        return ls.createLSSerializer();
    }
}
