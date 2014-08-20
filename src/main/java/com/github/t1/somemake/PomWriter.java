package com.github.t1.somemake;

import static com.github.t1.somemake.Repositories.*;
import static com.github.t1.somemake.Type.*;

import java.io.Writer;
import java.nio.file.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import lombok.NonNull;

public class PomWriter extends XmlWriter {
    private static final Path CONFIGURATION = Paths.get("configuration");

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
        product.features(p -> p.type().is("plugin")).forEach(p -> {
            System.out.println("##> " + p);
        });
        Stream.of( //
                repositories().get(type("plugin").id("compiler.java").version("3.1")).get() //
        ).forEach(p -> {
            p.features().forEach(f -> {
                tag("plugin", () -> {
                    plugin(f);
                });
            });
        });
    }

    private void plugin(Product p) {
        gav(p.version());
        if (p.property(CONFIGURATION) != null) {
            tag("configuration",
                    () -> {
                        if ("maven-jar-plugin".equals(p.id().artifactId())) {
                            tag("archive",
                                    () -> {
                                        tag("addMavenDescriptor",
                                                p.property(CONFIGURATION.resolve("addMavenDescriptor")));
                                        tag("manifest",
                                                () -> {
                                                    tag("addDefaultImplementationEntries", p.property(CONFIGURATION
                                                            .resolve("manifest/addDefaultImplementationEntries")));
                                                });
                                    });
                        } else {
                            copyProperties(p, CONFIGURATION);
                        }
                    });
        }
    }

    private void copyProperties(Product product, Path subPath) {
        product.properties().forEach(property -> {
            Path path = Paths.get("/plugin/plugin/").resolve(subPath).relativize(property);
            String name = path.getFileName().toString();
            if (!name.isEmpty()) {
                tag(name, product.property(CONFIGURATION.resolve(path)));
            }
        });
    }

    private void dependencies() {
        tag("dependencies", () -> {
            product.features(ofType("dependency")).forEach(dependency -> {
                tag("dependency", () -> {
                    gav(dependency.version());
                    dependency.properties().forEach(property -> {
                        Path propertyName = property.getFileName();
                        tag(propertyName.toString(), dependency.property(propertyName));
                    });
                });
            });
        });
    }

    private Predicate<Product> ofType(String type) {
        return feature -> feature.type().is(type);
    }

    private void gav(Version version) {
        tag("groupId", version.id().groupId());
        tag("artifactId", version.id().artifactId());
        tag("version", version.versionString());
    }
}
