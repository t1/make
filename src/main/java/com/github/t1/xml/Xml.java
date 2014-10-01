package com.github.t1.xml;

import java.io.*;
import java.net.URI;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.w3c.dom.ls.*;
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

    private final DOMImplementationLS domLs;

    public Xml(Document document) {
        super(document.getDocumentElement(), 1);
        this.domLs = (DOMImplementationLS) (document.getImplementation()).getFeature("LS", "3.0");
    }

    public void save() {
        Document document = document();
        serializer().writeToURI(document, document.getDocumentURI());
    }

    @Override
    public String toString() {
        return super.toString() + ":\n" + toXmlString();
    }

    public String toXmlString() {
        StringWriter out = new StringWriter();
        writeTo(out);
        return out.toString();
    }

    public void writeTo(Writer writer) {
        if (domLs == null)
            throw new UnsupportedOperationException("dom load and save not supported");
        serializer().write(document(), createOutput(writer));
        nl(writer);
    }

    private LSSerializer serializer() {
        return domLs.createLSSerializer();
    }

    private LSOutput createOutput(Writer writer) {
        LSOutput output = domLs.createLSOutput();
        output.setCharacterStream(writer);
        return output;
    }

    private void nl(Writer writer) {
        try {
            writer.append('\n');
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
