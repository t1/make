package com.github.t1.xml;

import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class Xml extends XmlElement {
    public static Xml createWithRootElement(String rootElementName) {
        Document document = newDocument();
        Element rootElement = document.createElement(rootElementName);
        document.appendChild(rootElement);
        return new Xml(document);
    }

    private static Document newDocument() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Xml load(URI uri) {
        return new Xml(loadDocument(uri));
    }

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

    public Xml(Document document) {
        super(document.getDocumentElement(), 1);
    }

    public URI uri() {
        String uri = document().getDocumentURI();
        return (uri == null) ? null : URI.create(uri);
    }

    public void uri(URI uri) {
        document().setDocumentURI((uri == null) ? null : uri.toString());
    }

    public void save(URI uri) {
        uri(uri);
        save();
    }

    public void save() {
        Document document = document();
        serializer().writeToURI(document, document.getDocumentURI());
    }
}
