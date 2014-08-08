package com.github.t1.xml;

import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableList;


public class Xml extends XmlElement {
    private final Document dom;

    public Xml(URI uri) {
        super(loadDocument(uri).getDocumentElement());
        this.dom = element.getOwnerDocument();
    }

    private static Document loadDocument(URI uri) {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(uri.toASCIIString());
            // JsonObject obj = Json.createReader(uri.toURL().openStream()).readObject();
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public ImmutableList<XmlElement> elements() {
        ImmutableList.Builder<XmlElement> result = ImmutableList.builder();
        // NodeIterator iterator = traversal.createNodeIterator(dom, NodeFilter.SHOW_ALL, null, false);
        NodeList childNodes = dom.getDocumentElement().getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child instanceof Element) {
                Element element = (Element) child;
                result.add(new XmlElement(element));
            }
        }
        return result.build();
    }
}
