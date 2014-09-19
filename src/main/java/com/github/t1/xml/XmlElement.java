package com.github.t1.xml;

import java.nio.file.*;
import java.util.*;

import lombok.*;

import org.w3c.dom.*;

import com.google.common.collect.ImmutableList;

@EqualsAndHashCode
@AllArgsConstructor
public class XmlElement {
    protected Element element;

    public String getName() {
        return element.getNodeName();
    }

    public Path getPath() {
        return appendName(element, Paths.get("/"));
    }

    private Path appendName(Node e, Path out) {
        Node parentNode = e.getParentNode();
        if (parentNode != null && parentNode instanceof Element)
            out = appendName(parentNode, out);
        return out.resolve(e.getNodeName());
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
        return getOptionalElement(Paths.get(name)).map(e -> e.value()).get();
    }

    public List<XmlElement> elements() {
        return list(element.getChildNodes());
    }

    private List<XmlElement> list(NodeList childNodes) {
        ImmutableList.Builder<XmlElement> result = ImmutableList.builder();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child instanceof Element) {
                Element element = (Element) child;
                result.add(new XmlElement(element));
            }
        }
        return result.build();
    }

    public ImmutableList<Path> elementPaths() {
        ImmutableList.Builder<Path> result = ImmutableList.builder();
        for (XmlElement element : getChildNodes()) {
            result.add(element.getPath());
        }
        return result.build();
    }

    private List<XmlElement> getChildNodes() {
        ImmutableList.Builder<XmlElement> result = ImmutableList.builder();
        addChildNodes(element.getChildNodes(), result);
        return result.build();
    }

    private void addChildNodes(NodeList childNodes, ImmutableList.Builder<XmlElement> result) {
        list(childNodes).forEach(e -> {
            result.add(e);
            addChildNodes(e.element.getChildNodes(), result);
        });
    }

    public Optional<XmlElement> getOptionalElement(Path name) {
        Element node = element;
        for (Path pathElement : name) {
            NodeList elements = node.getElementsByTagName(pathElement.toString());
            if (elements.getLength() == 0)
                return Optional.empty();
            if (elements.getLength() > 1)
                throw new IllegalArgumentException("found " + elements.getLength() + " elements by name " + pathElement);
            node = (Element) elements.item(0);
        }
        return Optional.ofNullable(node).map(e -> new XmlElement(e));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() //
                + "[" + getName() + (hasAttribute("id") ? ("@" + getAttribute("id")) : "") + "]";
    }

    public boolean hasChildElements(Path path) {
        return getOptionalElement(path).filter(e -> hasChildElements(e.element)).isPresent();
    }

    private boolean hasChildElements(Element element) {
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node instanceof Element) {
                return true;
            }
        }
        return false;
    }
}
