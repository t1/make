package com.github.t1.sMake;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.github.t1.xml.*;
import com.google.common.collect.ImmutableList;

@Slf4j
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
            String elementName = element.getName();
            switch (elementName) {
                case "dependency":
                    Type type = Type.type(elementName);
                    Id id = type.id(element.getValue("groupId"), element.getValue("artifactId"));
                    Version version = id.version(element.getValue("version"));
                    result.add(new ProductEntity(version));
                    break;
                case "name":
                    // ignore these
                default:
                    log.debug("ignoring element: {}", elementName);
            }
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
        Optional<XmlElement> releaseTimestamp = xml.getOptionalElement("releaseTimestamp");
        if (!releaseTimestamp.isPresent())
            return null;
        return LocalDateTime.parse(releaseTimestamp.get().value());
    }

    private String element(String name) {
        return xml.getValue(name);
    }

    @Override
    public Stream<Product> features() {
        return features.stream();
    }
}
