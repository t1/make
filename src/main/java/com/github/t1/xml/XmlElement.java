package com.github.t1.xml;

import static lombok.AccessLevel.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import lombok.*;

import org.w3c.dom.*;
import org.w3c.dom.ls.*;

import com.google.common.collect.ImmutableList;

@EqualsAndHashCode
@RequiredArgsConstructor(access = PROTECTED)
public class XmlElement {
    protected final Element element;
    private final int indent;

    private String indentString;
    private Text finalText;

    protected Document document() {
        return element.getOwnerDocument();
    }

    public String getName() {
        return element.getNodeName();
    }

    public Path getPath() {
        return buildPath(element, Paths.get("/"));
    }

    private Path buildPath(Node e, Path out) {
        Node parentNode = e.getParentNode();
        if (parentNode != null && parentNode instanceof Element)
            out = buildPath(parentNode, out);
        return out.resolve(e.getNodeName());
    }

    public boolean hasAttribute(String name) {
        return !element.getAttribute(name).isEmpty();
    }

    public String getAttribute(String name) {
        return element.getAttribute(name);
    }

    public Optional<String> value() {
        if (elements().isEmpty())
            return Optional.ofNullable(element.getTextContent());
        return Optional.empty();
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
                result.add(new XmlElement(element, indent + 1));
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

    public Optional<XmlElement> getOptionalElement(Path path) {
        Element node = element;
        for (Path pathElement : path) {
            NodeList elements = node.getElementsByTagName(pathElement.toString());
            if (elements.getLength() == 0)
                return Optional.empty();
            if (elements.getLength() > 1)
                throw new IllegalArgumentException("found " + elements.getLength() + " elements '" + pathElement
                        + "' in '" + getPath() + "'");
            node = (Element) elements.item(0);
        }
        return Optional.ofNullable(node).map(e -> new XmlElement(e, indent));
    }

    public boolean hasChildElement(Path path) {
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

    public XmlElement addElement(Path path) {
        XmlElement out = this;
        for (Path item : path) {
            out = out.addElement(item.toString());
        }
        return out;
    }

    public XmlElement addElement(String name) {
        Element node = document().createElement(name);
        addIndent();
        append(node);
        return new XmlElement(node, indent + 1);
    }

    private void append(Node node) {
        element.insertBefore(node, finalText());
    }

    private Text finalText() {
        if (finalText == null) {
            finalText = createText(indentString(indent - 1));
            element.insertBefore(finalText, null);
        }
        return finalText;
    }

    private void addIndent() {
        element.insertBefore(createText(indentString()), finalText());
    }

    private String indentString() {
        if (indentString == null)
            this.indentString = indentString(indent);
        return indentString;
    }

    private String indentString(int n) {
        StringBuilder builder = new StringBuilder("\n");
        for (int i = 0; i < n; i++) {
            builder.append("    ");
        }
        return builder.toString();
    }

    private Text createText(String text) {
        return document().createTextNode(text);
    }

    public XmlElement addAttribute(String name, String value) {
        element.setAttribute(name, value);
        return this;
    }

    public XmlElement addComment(String text) {
        addIndent();
        append(document().createComment(" " + text + " "));
        return this;
    }

    public XmlElement nl() {
        return addText("\n");
    }

    public XmlElement addText(String string) {
        element.insertBefore(createText(string), finalText);
        return this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() //
                + "[" + getName() + (hasAttribute("id") ? ("@" + getAttribute("id")) : "") + "]\n" //
                + toXmlString();
    }

    public String toXmlString() {
        StringWriter out = new StringWriter();
        writeTo(out);
        return out.toString();
    }

    public void writeTo(Writer writer) {
        serializer().write(element, createOutput(writer));
        nl(writer);
    }

    protected LSSerializer serializer() {
        return domLs().createLSSerializer();
    }

    protected DOMImplementationLS domLs() {
        DOMImplementationLS domLs = (DOMImplementationLS) (document().getImplementation()).getFeature("LS", "3.0");
        if (domLs == null)
            throw new UnsupportedOperationException("dom load and save not supported");
        return domLs;
    }

    private LSOutput createOutput(Writer writer) {
        LSOutput output = domLs().createLSOutput();
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
