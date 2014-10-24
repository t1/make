package com.github.t1.somemake.model;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Optional;

import com.github.t1.xml.*;
import com.google.common.collect.ImmutableList;

public class XmlStoredProduct extends Product {
    private final XmlElement xml;

    public XmlStoredProduct(URI uri) {
        this.xml = Xml.load(uri);
    }

    private XmlStoredProduct(XmlElement xml) {
        this.xml = xml;
    }

    public XmlStoredProduct(Version version) {
        this.xml = Xml.createWithRootElement(version.type().typeName());

        this.xml.addAttribute("id", version.id().idString());
        this.xml.addAttribute("version", version.versionString());
    }

    @Override
    public Version version() {
        Type type = Type.type(xml.getName());
        String id = xml.getAttribute("id");
        if (id == null)
            id = Id.EMPTY;
        String version = xml.getAttribute("version");
        if (version == null || version.isEmpty())
            version = Version.ANY;
        return type.id(id).version(version);
    }

    @Override
    public Product set(Id id, String value) {
        XmlElement element = xml.addElement(id.type().typeName());
        if (!id.isEmpty())
            element.addAttribute("id", id.idString());
        element.value(value);
        return this;
    }

    @Override
    public ImmutableList<Product> unresolvedFeatures() {
        ImmutableList.Builder<Product> result = ImmutableList.builder();
        for (XmlElement element : xml.elements()) {
            result.add(new XmlStoredProduct(element));
        }
        return result.build();
    }

    @Override
    public Optional<String> value() {
        return xml.value();
    }

    @Override
    public String toString() {
        return super.toString() + "\n# " + xml.uri();
    }

    @Override
    public Optional<String> attribute(String name) {
        String value = xml.getAttribute(name);
        return (value.isEmpty()) ? Optional.empty() : Optional.of(value);
    }

    @Override
    public Product saveTo(URI uri) {
        Path dir = Paths.get(uri);
        mkdirs(dir);
        uri = dir.resolve("product.xml").toUri();
        ((Xml) xml).save(uri);
        return this;
    }

    private void mkdirs(Path dir) {
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
