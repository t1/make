package com.github.t1.somemake;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.github.t1.xml.XmlElement;
import com.google.common.collect.ImmutableList;

@Slf4j
public class XmlStoredProduct extends Product {
    private final XmlElement xml;

    @Getter
    private final Version version;
    private final List<Product> features;

    public XmlStoredProduct(XmlElement xml) {
        this.xml = xml;
        this.version = parseVersion();
        this.features = parseFeatures();
    }

    private Version parseVersion() {
        Type type = Type.type(xml.getName());
        String id = xml.getAttribute("id");
        String version = xml.getAttribute("version");
        if (version.isEmpty())
            version = "*";
        return type.id(id).version(version);
    }

    private ImmutableList<Product> parseFeatures() {
        ImmutableList.Builder<Product> result = ImmutableList.builder();
        for (XmlElement element : xml.elements()) {
            switch (element.getName()) {
                case "dependency":
                case "feature":
                    result.add(new XmlStoredProduct(element));
                    break;
                case "name":
                case "description":
                case "releaseTimestamp":
                    break; // ignore these
                default:
                    log.debug("ignoring element {} in {}", element.getName(), version());
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
        return property(name);
    }

    @Override
    public Stream<Product> unresolvedFeatures() {
        return features.stream();
    }

    @Override
    public String property(String name) {
        return xml.getOptionalElement(name).map(e -> e.value()).orElse(null);
    }
}
