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

    public String getAttribute(String name) {
        return element.getAttribute(name);
    }

    public String getElementValue(String name) {
        return getOptionalElementValue(name).get();
    }

    public Optional<String> getOptionalElementValue(String name) {
        NodeList elements = element.getElementsByTagName(name);
        if (elements.getLength() == 0)
            return Optional.empty();
        if (elements.getLength() > 1)
            throw new IllegalArgumentException("found " + elements.getLength() + " elements by name " + name);
        return Optional.of(elements.item(0).getTextContent());
    }
}
