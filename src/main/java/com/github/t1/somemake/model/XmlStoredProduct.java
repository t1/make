package com.github.t1.somemake.model;

import java.net.URI;
import java.nio.file.Path;
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

        this.xml.setAttribute(Id.ATTRIBUTE, version.id().idString());
        this.xml.setAttribute(Version.ATTRIBUTE, version.versionString());
    }

    @Override
    public Type type() {
        return Type.type(xml.getName());
    }

    @Override
    public Product version(Version version) {
        xml.setAttribute(Id.ATTRIBUTE, version.id().idString());
        xml.setAttribute(Version.ATTRIBUTE, version.versionString());
        return this;
    }

    @Override
    public Product addFeature(Id id, String value) {
        XmlElement element = xml.addElement(id.type().typeName());
        if (!id.isEmpty())
            element.setAttribute(Id.ATTRIBUTE, id.idString());
        element.value(value);
        return this;
    }

    @Override
    public Product add(Product feature) {
        Version version = feature.version();
        XmlElement element = xml.addElement(version.id().type().typeName());
        if (!version.id().isEmpty()) {
            element.setAttribute(Id.ATTRIBUTE, version.id().idString());
            element.setAttribute(Version.ATTRIBUTE, version.versionString());
        }

        feature.value().ifPresent(v -> xml.value(v));

        for (Product subFrom : feature.features()) {
            XmlElement subElement = element.addElement(subFrom.type().typeName());
            XmlStoredProduct subTo = new XmlStoredProduct(subElement);
            subTo.add(subFrom);
        }

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
    public Product attribute(String key, String value) {
        xml.setAttribute(key, value);
        return this;
    }

    @Override
    public Product saveTo(Path directory) {
        Path filePath = directory.resolve("product.xml");
        URI uri = filePath.toUri();
        ((Xml) xml).save(uri);
        return this;
    }
}
