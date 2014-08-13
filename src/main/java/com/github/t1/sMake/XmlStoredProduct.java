package com.github.t1.sMake;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.github.t1.xml.*;
import com.google.common.collect.ImmutableList;

@Slf4j
public class XmlStoredProduct extends Product {
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
            switch (element.getName()) {
                case "dependency":
                case "feature":
                    Version version = parseVersion(element);
                    result.add(new ProductEntity(version));
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

    private Version parseVersion(XmlElement element) {
        try {
            return parseId(element).version(parseVersionString(element));
        } catch (RuntimeException e) {
            throw new RuntimeException("can't parse version for " + element, e);
        }
    }

    private Id parseId(XmlElement element) {
        Type type = Type.type(element.getName());
        if (element.hasAttribute("id"))
            return type.id(element.getAttribute("id"));
        return type.id(element.getValue("groupId"), element.getValue("artifactId"));
    }

    private String parseVersionString(XmlElement element) {
        if (element.hasAttribute("version"))
            return element.getAttribute("version");
        Optional<XmlElement> versionElement = element.getOptionalElement("version");
        if (versionElement.isPresent())
            return versionElement.get().value();
        return "*";
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
        return features.stream().map(merged());
    }
}
