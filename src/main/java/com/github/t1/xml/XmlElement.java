package com.github.t1.xml;

import java.util.Optional;

import lombok.*;

import org.jboss.weld.exceptions.IllegalArgumentException;
import org.w3c.dom.*;

@Data
@AllArgsConstructor
public class XmlElement {
    protected Element element;

    public String getName() {
        return element.getNodeName();
    }

    public boolean hasAttribute(String name) {
        return !element.getAttribute(name).isEmpty();
    }

    public String getAttribute(String name) {
        return element.getAttribute(name);
    }

    public String value() {
        return element.getTextContent();
    }

    public String getValue(String name) {
        return getOptionalElement(name).map(e -> e.value()).get();
    }

    public Optional<XmlElement> getOptionalElement(String name) {
        NodeList elements = element.getElementsByTagName(name);
        if (elements.getLength() == 0)
            return Optional.empty();
        if (elements.getLength() > 1)
            throw new IllegalArgumentException("found " + elements.getLength() + " elements by name " + name);
        Node sub = elements.item(0);
        return (sub == null) ? Optional.empty() : Optional.of(new XmlElement((Element) sub));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() //
                + "[" + getName() + (hasAttribute("id") ? ("@" + getAttribute("id")) : "") + "]";
    }
}
