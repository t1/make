package com.github.t1.xml;

import org.w3c.dom.Document;

public class Xml extends XmlElement {
    public Xml(Document document) {
        super(document.getDocumentElement());
    }
}
