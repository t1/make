package com.github.t1.somemake;

import java.io.Writer;
import java.nio.file.*;
import java.util.function.Predicate;

import lombok.NonNull;

import com.google.common.collect.ImmutableList;

public class PomWriter extends XmlWriter {
    private static final String NAMESPACES =
            "xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" //
                    + "    xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\"";
    private static final Path PLUGIN_ROOT = Paths.get("/plugin/plugin/");

    private final Product product;

    public PomWriter(@NonNull Product product, @NonNull Writer target) {
        super(target);
        this.product = checked(product);
    }

    private Product checked(Product product) {
        if (!product.type().is("product"))
            throw new IllegalArgumentException("can't produce a pom.xml for type " + product.type());
        return product;
    }

    public void write() {
        tag("project", NAMESPACES, () -> {
            comment("pom written by somemake; built from " + product.version());
            tag("modelVersion", "4.0.0");
            nl();
            gav(product);
            nl();
            tag("name", product.name());
            tag("description", product.description());
            nl();
            tag("build", () -> tag("plugins", () -> plugins()));
            nl();
            dependencies();
        });
    }

    private void plugins() {
        for (Product plugin : product.features()) {
            if (plugin.type().is("plugin")) {
                tag("plugin", () -> plugin(plugin));
            }
        }
    }

    private void plugin(Product plugin) {
        gav(plugin);
        copyProperties(plugin);
    }

    private void copyProperties(Product plugin) {
        for (Product property : plugin.features()) {
            // tag(property.name().get(), () -> copyProperties(plugin));
        }
    }

    private void dependencies() {
        tag("dependencies", () -> {
            ImmutableList<Product> dependencies = product.features(ofType("dependency"));
            for (Product dependency : dependencies) {
                tag("dependency", () -> {
                    gav(dependency);
                    for (Product property : dependency.features()) {
                        tag(property.name().get(), property.value());
                    }
                });
            }
        });
    }

    private Predicate<Product> ofType(String type) {
        return feature -> feature.type().is(type);
    }

    private void gav(Product product) {
        Version version = product.version();
        tag("groupId", version.id().groupId());
        tag("artifactId", version.id().artifactId());
        tag("version", version.versionString());
    }
}
