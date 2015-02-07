package com.github.t1.make.pom;

import static java.util.stream.Collectors.*;

import java.nio.file.*;
import java.util.*;

import lombok.RequiredArgsConstructor;

import com.github.t1.make.model.*;
import com.github.t1.xml.XmlElement;
import com.google.common.collect.ImmutableList;

@RequiredArgsConstructor
abstract class PomSectionWriter {
    protected final Product product;

    protected abstract XmlElement addTo(XmlElement out);

    protected void gav(XmlElement to) {
        gav(product.version(), to);
    }

    protected static void gav(Version from, XmlElement to) {
        to.addElement("groupId").addText(from.id().groupId());
        to.addElement("artifactId").addText(from.id().artifactId());
        to.addElement(Version.ATTRIBUTE).addText(from.versionString());
    }

    protected void addFeature(XmlElement to, String name, Optional<String> value) {
        if (value.isPresent()) {
            to.addElement(name).addText(value.get());
        }
    }

    protected void addFeature(XmlElement to, Id id) {
        if (product.hasFeature(id)) {
            to.addElement(id.type().typeName()).addText(product.feature(id).value().get());
        }
    }

    protected void addSection(XmlElement out, Class<? extends PomSectionWriter> type) {
        PomSection sectionAnnotation = pomSection(type);
        List<Product> list = featuresOfType(sectionAnnotation.from());
        if (list.isEmpty())
            return;

        XmlElement sectionElement = getOrCreateSectionElement(out, sectionAnnotation);

        for (Product feature : list) {
            createWriter(type, feature).addTo(sectionElement);
        }
    }

    private XmlElement getOrCreateSectionElement(XmlElement out, PomSection sectionAnnotation) {
        Path path = Paths.get(sectionAnnotation.to());
        if (path.toString().isEmpty())
            throw new IllegalArgumentException("PomSection annotation must not be empty");
        if (!out.hasChildElement(path))
            out.nl();
        XmlElement sectionElement = out.getOrCreateElement(path);
        return sectionElement;
    }

    protected PomSection pomSection(Class<? extends PomSectionWriter> type) {
        PomSection sectionAnnotation = type.getAnnotation(PomSection.class);
        if (sectionAnnotation == null)
            throw new IllegalArgumentException("the type " + type.getName() + " is not annoated as "
                    + PomSection.class.getSimpleName());
        return sectionAnnotation;
    }

    protected List<Product> featuresOfType(String type) {
        return product.features().stream().filter(p -> {
            return p.type().is(type);
        }).collect(toList());
    }

    protected <T extends PomSectionWriter> T createWriter(Class<T> type, Product product) {
        try {
            return type.getConstructor(Product.class).newInstance(product);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void copy(Product from, XmlElement to) {
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
