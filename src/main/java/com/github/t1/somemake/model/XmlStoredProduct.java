package com.github.t1.somemake.model;

import java.util.Optional;

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
        if (id == null)
            id = Id.EMPTY;
        String version = xml.getAttribute("version");
        if (version == null || version.isEmpty())
            version = Version.ANY;
        return type.id(id).version(version);
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
}
