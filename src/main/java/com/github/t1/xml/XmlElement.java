package com.github.t1.xml;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

import lombok.*;

import org.w3c.dom.*;

import com.google.common.collect.*;

@Data
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
        return getOptionalElement(name).map(e -> e.value()).get();
    }

    public Stream<XmlElement> elements() {
        return stream(element.getChildNodes());
    }

    private Stream<XmlElement> stream(NodeList childNodes) {
        Stream.Builder<XmlElement> result = Stream.builder();
        // NodeIterator iterator = traversal.createNodeIterator(dom, NodeFilter.SHOW_ALL, null, false);
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child instanceof Element) {
                Element element = (Element) child;
                result.add(new XmlElement(element));
            }
        }
        return result.build();
    }

    public Set<Path> elementPaths() {
        ImmutableList.Builder<XmlElement> list = ImmutableList.builder();
        addChildNodes(element.getChildNodes(), list);
        ImmutableSet.Builder<Path> map = ImmutableSet.builder();
        for (XmlElement element : list.build()) {
            map.add(element.getPath());
        }
        return map.build();
    }

    private void addChildNodes(NodeList childNodes, ImmutableList.Builder<XmlElement> result) {
        stream(childNodes).forEach(e -> {
            result.add(e);
            addChildNodes(e.element.getChildNodes(), result);
        });
    }

    public Optional<XmlElement> getOptionalElement(String name) {
        Element node = element;
        for (String pathElement : name.split("/")) {
            NodeList elements = node.getElementsByTagName(pathElement);
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
}
