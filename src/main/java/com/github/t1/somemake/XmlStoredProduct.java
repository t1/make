package com.github.t1.somemake;

import java.nio.file.*;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;

import com.github.t1.xml.XmlElement;
import com.google.common.collect.ImmutableList;

@AllArgsConstructor
public class XmlStoredProduct extends Product {
    private final XmlElement xml;

    @Override
    public Version version() {
        Type type = Type.type(xml.getName());
        String id = xml.getAttribute("id");
        String version = xml.getAttribute("version");
        if (version.isEmpty())
            version = "*";
        return type.id(id).version(version);
    }

    @Override
    public String name() {
        return property(Paths.get("name"));
    }

    @Override
    public String description() {
        return property(Paths.get("description"));
    }

    @Override
    public LocalDateTime releaseTimestamp() {
        return xml.getOptionalElement(Paths.get("releaseTimestamp")) //
                .map(releaseTimestamp -> LocalDateTime.parse(releaseTimestamp.value())) //
                .orElse(null);
    }

    @Override
    public String property(Path path) {
        return xml.getOptionalElement(path).map(e -> e.value()).orElse(null);
    }

    @Override
    public ImmutableList<Path> properties() {
        return xml.elementPaths();
    }

    @Override
    public boolean hasChildProperties(Path property) {
        return xml.hasChildElements(property);
    }

    @Override
    public ImmutableList<Product> unresolvedFeatures() {
        ImmutableList.Builder<Product> result = ImmutableList.builder();
        for (XmlElement element : xml.elements()) {
            result.add(new XmlStoredProduct(element));
        }
        return result.build();
    }
}
