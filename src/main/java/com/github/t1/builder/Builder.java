package com.github.t1.builder;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import javax.xml.bind.JAXB;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.w3c.dom.*;

@Slf4j
public class Builder {
    private static final Path REPOSITORY = Paths.get("src/test/resources/repository");

    public static void mainx(String[] args) {
        System.out.println(of(args[0]).toPom());
    }

    public static Builder of(String fileName) {
        return new Builder(fileName);
    }

    public static Builder of(Path path) {
        return new Builder(path);
    }

    public static String readFile(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    private final Product product;
    private final List<Product> features;

    public Builder(String fileName) {
        this(Paths.get(fileName));
    }

    public Builder(Path path) {
        this.product = parse(path);
        this.features = features();
    }

    private Product parse(Path path) {
        try {
            return JAXB.unmarshal(new FileReader(path.toFile()), Product.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Product> features() {
        List<Product> features = new ArrayList<>();
        if (product.getFeatures() != null) {
            for (Feature feature : product.getFeatures()) {
                Path path = featurePath(feature);
                if (path != null) {
                    log.debug("found feature in repository: {}:{}", feature.getId(), feature.getVersion());
                    features.add(Builder.of(path).getProduct());
                }
            }
        }
        return features;
    }

    private Path featurePath(Feature feature) {
        String id = feature.getId();
        String version = feature.getVersion();
        if (id == null || version == null)
            return null;

        String idPath = id.replace('.', '/').replace(':', '/');
        Path path = REPOSITORY.resolve("feature").resolve(idPath).resolve(version).resolve("product.xml");

        if (!Files.exists(path))
            return null;

        return path;
    }

    public String toPom() {
        return "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" //
                + "    xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" //
                + "    <modelVersion>4.0.0</modelVersion>\n" //
                + "\n" //
                + "    <groupId>"
                + product.getGroupId()
                + "</groupId>\n" //
                + "    <artifactId>"
                + product.getArtifactId()
                + "</artifactId>\n" //
                + "    <version>"
                + product.getVersion()
                + "</version>\n" //
                + "\n" //
                + "    <build>\n" //
                + "        <plugins>\n" //
                + "            <plugin>\n" //
                + "                <artifactId>maven-compiler-plugin</artifactId>\n" //
                + "                <version>3.1</version>\n" //
                + "                <configuration>\n" //
                + "                    <source>1.8</source>\n" //
                + "                    <target>1.8</target>\n" //
                + "                    <showWarnings>true</showWarnings>\n" //
                + "                    <showDeprecation>true</showDeprecation>\n" //
                + "                    <encoding>utf8</encoding>\n" //
                + "                    <fork>true</fork>\n" //
                + "                    <compilerArgument>-parameters</compilerArgument>\n" //
                + "                </configuration>\n" //
                + "            </plugin>\n" //
                + "            <plugin>\n" //
                + "                <artifactId>maven-jar-plugin</artifactId>\n" //
                + "                <version>2.4</version>\n" //
                + "                <configuration>\n" //
                + "                    <archive>\n" //
                + "                        <addMavenDescriptor>false</addMavenDescriptor>\n" //
                + "                        <manifest>\n" //
                + "                            <addDefaultImplementationEntries>true</addDefaultImplementationEntries>\n" //
                + "                        </manifest>\n" //
                + "                    </archive>\n" //
                + "                </configuration>\n" //
                + "            </plugin>\n" //
                + "        </plugins>\n" //
                + "    </build>\n" //
                + "\n" //
                + "    <dependencies>\n" //
                + dependendies() //
                + "        \n" //
                + "        <dependency>\n" //
                + "            <groupId>ch.qos.logback</groupId>\n" //
                + "            <artifactId>logback-classic</artifactId>\n" //
                + "            <version>1.1.2</version>\n" //
                + "        </dependency>\n" //
                + "\n" //
                + "        <dependency>\n" //
                + "            <groupId>junit</groupId>\n" //
                + "            <artifactId>junit</artifactId>\n" //
                + "            <version>4.11</version>\n" //
                + "            <scope>test</scope>\n" //
                + "        </dependency>\n" //
                + "        <dependency>\n" //
                + "            <groupId>org.hamcrest</groupId>\n" //
                + "            <artifactId>hamcrest-core</artifactId>\n" //
                + "            <version>1.2.1</version>\n" //
                + "            <scope>test</scope>\n" //
                + "        </dependency>\n" //
                + "        <dependency>\n" //
                + "            <groupId>org.mockito</groupId>\n" //
                + "            <artifactId>mockito-all</artifactId>\n" //
                + "            <version>1.9.5</version>\n" //
                + "            <scope>test</scope>\n" //
                + "        </dependency>\n" //
                + "    </dependencies>\n" //
                + "</project>\n" //
        ;
    }

    private String dependendies() {
        StringBuilder out = new StringBuilder();
        for (Product feature : features) {
            copyDependencies(out, feature);
        }
        return out.toString();
    }

    private void copyDependencies(StringBuilder out, Product feature) {
        for (Element element : feature.getOther()) {
            if ("dependency".equals(element.getTagName())) {
                out.append("        <dependency>\n");
                NodeList childNodes = element.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node childNode = childNodes.item(i);
                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        tag(out, childNode.getNodeName(), childNode.getTextContent());
                    }
                }
                out.append("        </dependency>\n");
            }
        }
    }

    private void tag(StringBuilder out, String name, String body) {
        out.append("            <" + name + ">" + body + "</" + name + ">\n");
    }
}
