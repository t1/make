package com.github.t1.sMake;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import lombok.Getter;

import com.github.t1.xml.*;
import com.google.common.collect.ImmutableList;

public class XmlStoredProduct implements Product {
    private final Xml xml;

    @Getter
    private final Version version;
    private final List<Product> features;

    public XmlStoredProduct(Xml xml) {
        this.xml = xml;
        this.version = parseVersion();
        this.features = parseFeatures();
    }

    private Version parseVersion() {
        Type type = Type.type(xml.getName());
        String id = xml.getAttribute("id");
        String version = xml.getAttribute("version");
        return type.id(id).version(version);
    }

    private ImmutableList<Product> parseFeatures() {
        ImmutableList.Builder<Product> result = ImmutableList.builder();
        for (XmlElement element : xml.elements()) {
            Type type = Type.type(element.getName());
            Id id = type.id(element.getElementValue("groupId"), element.getElementValue("artifactId"));
            Version version = id.version(element.getElementValue("version"));
            result.add(new ProductEntity(version));
        }
        return result.build();
    }

    @Override
    public String name() {
        return element("name");
    }

    @Override
    public String description() {
        return element("description");
    }

    @Override
    public LocalDateTime releaseTimestamp() {
        return LocalDateTime.parse(element("releaseTimestamp"));
    }

    private String element(String name) {
        return xml.getElementValue(name);
    }

    @Override
    public Stream<Product> features() {
        return features.stream();
    }
}
