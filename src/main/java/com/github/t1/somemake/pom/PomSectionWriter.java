package com.github.t1.somemake.pom;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;

import java.nio.file.*;
import java.util.*;

import lombok.RequiredArgsConstructor;

import com.github.t1.somemake.model.*;
import com.github.t1.xml.XmlElement;
import com.google.common.collect.ImmutableList;

@RequiredArgsConstructor
abstract class PomSectionWriter {
    protected final Product product;

    protected abstract void addTo(XmlElement out);

    protected void gav(XmlElement to) {
        gav(product.version(), to);
    }

    protected void gav(Version from, XmlElement to) {
        to.addElement("groupId").addText(from.id().groupId());
        to.addElement("artifactId").addText(from.id().artifactId());
        to.addElement("version").addText(from.versionString());
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

    protected void addSection(XmlElement out, Class<? extends PomSectionWriter> type) {
        PomSection sectionAnnotation = type.getAnnotation(PomSection.class);
        if (sectionAnnotation == null)
            throw new IllegalArgumentException("the type " + type.getName() + " is not annoated as "
                    + PomSection.class.getSimpleName());
        List<Product> list = featuresOfType(sectionAnnotation.from());
        if (list.isEmpty())
            return;
        out.nl();

        Path path = Paths.get(sectionAnnotation.to());
        XmlElement sectionElement = out.getOrCreateElement(path);

        for (Product feature : list) {
            createWriter(type, feature).addTo(sectionElement);
        }
    }

    protected List<Product> featuresOfType(String... types) {
        return product.features().stream().filter(p -> {
            return stream(types).anyMatch(type -> p.type().is(type));
        }).collect(toList());
    }

    private PomSectionWriter createWriter(Class<? extends PomSectionWriter> type, Product product) {
        try {
            return type.getConstructor(Product.class).newInstance(product);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    protected void copy(Product from, XmlElement to) {
        XmlElement subTo = to.addElement(from.type().toString());
        if (from.hasFeatures()) {
            ImmutableList<Product> features = from.features();
            for (Product subFrom : features) {
                copy(subFrom, subTo);
            }
        } else {
            Optional<String> value = from.value();
            if (value.isPresent()) {
                subTo.addText(value.get());
            }
        }
    }
}
