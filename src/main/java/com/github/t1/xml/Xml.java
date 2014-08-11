package com.github.t1.xml;

import org.w3c.dom.*;

import com.google.common.collect.ImmutableList;


public class Xml extends XmlElement {
    private final Document dom;

    public Xml(Document document) {
        super(document.getDocumentElement());
        this.dom = element.getOwnerDocument();
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
