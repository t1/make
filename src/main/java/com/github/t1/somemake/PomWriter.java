package com.github.t1.somemake;

import static java.util.stream.Collectors.*;

import java.io.*;
import java.util.*;

import lombok.*;

import com.github.t1.xml.*;

@RequiredArgsConstructor
public class PomWriter {
    private static final String NAMESPACE_NAME = "http://maven.apache.org/POM/4.0.0";

    @NonNull
    private final Product product;

    public String writeToString() {
        StringWriter out = new StringWriter();
        writeTo(out);
        return out.toString();
    }

    public void writeTo(Writer writer) {
        build().writeTo(writer);
    }

    private Xml build() {
        Xml out = Xml.createWithRootElement("project");
        out.addAttribute("xmlns", NAMESPACE_NAME);
        out.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        out.addAttribute("xsi:schemaLocation", NAMESPACE_NAME + " http://maven.apache.org/xsd/maven-4.0.0.xsd");

        out.addComment("pom written by somemake; built from " + product.version());

        out.addElement("modelVersion").addText("4.0.0");
        out.nl();
        gav(product, out);
        out.nl();
        addProperty(out, "name", product.name());
        addProperty(out, "description", product.description());

        addBuildSection(out);
        addDependencies(out);
        return out;
    }

    private void addProperty(XmlElement element, String name, Optional<String> value) {
        if (value.isPresent()) {
            element.addElement(name).addText(value.get());
        }
    }

    private void addBuildSection(XmlElement out) {
        List<Product> pluginList = featuresOfType("plugin");
        if (pluginList.isEmpty())
            return;
        out.nl();
        XmlElement pluginsElement = out.addElement("build").addElement("plugins");
        for (Product plugin : pluginList) {
            copy(plugin, pluginsElement.addElement("plugin"));
        }
    }

    private List<Product> featuresOfType(String type) {
        return product.features().stream().filter(p -> p.type().is(type)).collect(toList());
    }

    private void copy(Product from, XmlElement to) {
        gav(from, to);
        copyProperties(from, to);
    }

    private void gav(Product from, XmlElement to) {
        Version version = from.version();
        to.addElement("groupId").addText(version.id().groupId());
        to.addElement("artifactId").addText(version.id().artifactId());
        to.addElement("version").addText(version.versionString());
    }

    private void copyProperties(Product from, XmlElement to) {
        for (Product property : from.features()) {
            Optional<String> value = property.value();
            if (value.isPresent()) {
                to.addElement(property.type().typeName()).addText(value.get());
            }
        }
    }

    private void addDependencies(XmlElement out) {
        List<Product> dependencyList = featuresOfType("dependency");
        if (dependencyList.isEmpty())
            return;
        out.nl();
        XmlElement dependenciesElement = out.addElement("dependencies");
        for (Product dependency : dependencyList) {
            XmlElement dependencyElement = dependenciesElement.addElement("dependency");
            gav(dependency, dependencyElement);
            copyProperties(dependency, dependencyElement);
        }
    }
}
