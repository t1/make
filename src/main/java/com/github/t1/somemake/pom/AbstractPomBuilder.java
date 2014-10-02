package com.github.t1.somemake.pom;

import static java.util.stream.Collectors.*;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;

import com.github.t1.somemake.model.*;
import com.github.t1.xml.XmlElement;

@RequiredArgsConstructor
abstract class AbstractPomBuilder {
    protected final Product product;

    protected abstract void addTo(XmlElement out);

    protected List<Product> featuresOfType(String type) {
        return product.features().stream().filter(p -> p.type().is(type)).collect(toList());
    }

    protected void gav(XmlElement to) {
        Version version = product.version();
        to.addElement("groupId").addText(version.id().groupId());
        to.addElement("artifactId").addText(version.id().artifactId());
        to.addElement("version").addText(version.versionString());
    }

    protected void addProperty(XmlElement to, String name, Optional<String> value) {
        if (value.isPresent()) {
            to.addElement(name).addText(value.get());
        }
    }

    protected void addProperty(XmlElement to, Id id) {
        if (product.hasFeature(id)) {
            to.addElement(id.type().typeName()).addText(product.feature(id).value().get());
        }
    }

    protected void addSection(XmlElement out, String type, Path path, Function<Product, AbstractPomBuilder> f) {
        List<Product> list = featuresOfType(type);
        if (list.isEmpty())
            return;
        out.nl();
        XmlElement pluginsElement = out.addElement(path);
        for (Product plugin : list) {
            f.apply(plugin).addTo(pluginsElement);
        }
    }
}
