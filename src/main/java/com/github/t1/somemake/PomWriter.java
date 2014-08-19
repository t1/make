package com.github.t1.somemake;

import java.io.Writer;
import java.util.function.Consumer;

import lombok.NonNull;

public class PomWriter extends XmlWriter {
    private static final String NAMESPACES =
            "xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" //
                    + "    xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\"";

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
            tag("groupId", product.id().groupId());
            tag("artifactId", product.id().artifactId());
            tag("version", product.version().versionString());
            nl();
            tag("name", product.name());
            tag("description", product.description());
            nl();
            tag("build", () -> {
                tag("plugins", () -> {
                    plugins();
                });
            });
            nl();
            dependencies();
        });
    }

    private void plugins() {
        tag("plugin", () -> {
            tag("artifactId", "maven-compiler-plugin");
            tag("version", "3.1");
            tag("configuration", () -> {
                String version = System.getProperty("java.specification.version");
                tag("source", version);
                tag("target", version);
                tag("showWarnings", "true");
                tag("showDeprecation", "true");
                tag("encoding", "utf8");
                tag("fork", "true");
                if (Version.VERSION.compare(version, "1.8") >= 0) {
                    tag("compilerArgument", "-parameters");
                }
            });
        });
        tag("plugin", () -> {
            tag("artifactId", "maven-jar-plugin");
            tag("version", "2.4");
            tag("configuration", () -> {
                tag("archive", () -> {
                    tag("addMavenDescriptor", "false");
                    tag("manifest", () -> {
                        tag("addDefaultImplementationEntries", "true");
                    });
                });
            });
        });
    }

    private void dependencies() {
        tag("dependencies", () -> {
            product.features(p -> p.type().is("dependency")).forEach(dependency());
        });
    }

    private Consumer<? super Product> dependency() {
        return p -> {
            tag("dependency", () -> {
                tag("groupId", p.id().groupId());
                tag("artifactId", p.id().artifactId());
                tag("version", p.version().versionString());
                tag("scope", p.property("scope"));
            });
        };
    }
}
