package com.github.t1.somemake;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.t1.xml.XmlElement;

@Slf4j
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
    public String property(Path name) {
        return xml.getOptionalElement(name).map(e -> e.value()).orElse(null);
    }

    @Override
    public LocalDateTime releaseTimestamp() {
        return xml.getOptionalElement(Paths.get("releaseTimestamp")) //
                .map(releaseTimestamp -> LocalDateTime.parse(releaseTimestamp.value())) //
                .orElse(null);
    }

    @Override
    public Stream<Product> unresolvedFeatures() {
        return xml.elements().filter(element -> {
            switch (element.getName()) {
                case "dependency":
                case "feature":
                case "plugin":
                    return true;
                case "name":
                case "description":
                case "releaseTimestamp":
                    break; // ignore these
                default:
                    log.debug("ignoring element {} in {}", element.getName(), version());
            }
            return false;
        }).map(element -> new XmlStoredProduct(element));
    }

    @Override
    public Stream<Path> properties() {
        return xml.elementPaths().stream();
    }
}
