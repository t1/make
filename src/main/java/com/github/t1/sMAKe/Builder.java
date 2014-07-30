package com.github.t1.sMAKe;

import static com.github.t1.sMAKe.ChildNodesIterable.*;
import static org.w3c.dom.Node.*;

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
                String type = "feature";
                String id = feature.getId();
                String version = feature.getVersion();
                Path path = featurePath(type, id, version);
                if (path != null) {
                    log.debug("found in repository: {} {} {}", type, id, version);
                    features.add(Builder.of(path).getProduct());
                }
            }
            // if (product.getOther() != null) {
            // for (Element feature : product.getOther()) {
            String type = "packaging";
            String id = "jar";
            String version = "1.0";
            Path path = featurePath(type, id, version);
            if (path != null) {
                log.debug("found in repository: {} {} {}", type, id, version);
                features.add(Builder.of(path).getProduct());
            }
            // }
            // }
        }
        return features;
    }

    private Path featurePath(String type, String id, String version) {
        if (id == null || version == null)
            return null;

        String idPath = id.replace('.', '/').replace(':', '/');
        Path path = REPOSITORY.resolve(type).resolve(idPath).resolve(version).resolve("product.xml");

        if (!Files.exists(path))
            return null;

        return path;
    }

    public String toPom() {
        return "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" //
                + "    xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" //
                + "    <modelVersion>4.0.0</modelVersion>\n" //
                + "\n" //
                + tag(1, "groupId", product.getGroupId()) //
                + tag(1, "artifactId", product.getArtifactId()) //
                + tag(1, "version", product.getVersion()) //
                + "\n" //
                + tag(1, "name", product.getName()) //
                + tag(1, "description", product.getDescription()) //
                + tag(1, "inceptionYear", product.getInceptionYear()) //
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
                + buildPlugins() //
                + "        </plugins>\n" //
                + "    </build>\n" //
                + "\n" //
                + "    <dependencies>\n" //
                + dependencies() //
                + "    </dependencies>\n" //
                + "</project>\n" //
        ;
    }

    private String tag(int indent, String tagName, String body) {
        StringBuilder out = new StringBuilder();
        indent(indent, out);
        out.append("<").append(tagName).append(">");
        out.append(body);
        out.append("</").append(tagName).append(">\n");
        return out.toString();
    }

    private void indent(int indent, StringBuilder out) {
        for (int i = 0; i < indent; i++) {
            out.append("    ");
        }
    }

    private String buildPlugins() {
        StringBuilder out = new StringBuilder();
        for (Product feature : features) {
            copy(out, "plugin", feature, 3);
        }
        return out.toString();
    }

    private String dependencies() {
        StringBuilder out = new StringBuilder();
        for (Product feature : features) {
            copy(out, "dependency", feature, 2);
        }
        return out.toString();
    }

    private void copy(StringBuilder out, String type, Product feature, int indent) {
        for (Element element : feature.getOther()) {
            if (type.equals(element.getNodeName())) {
                copyNode(out, indent, element);
            }
        }
    }

    private void copyNode(StringBuilder out, int indent, Node node) {
        String tagName = node.getNodeName();
        indent(indent, out);
        out.append("<").append(tagName).append(">");
        boolean hasChildNodes = copyChildNodes(out, node, indent);
        if (hasChildNodes) {
            indent(indent, out);
        }
        out.append("</").append(tagName).append(">\n");
    }

    private boolean copyChildNodes(StringBuilder out, Node node, int indent) {
        boolean hasChildNodes = false;
        for (Node childNode : childNodes(node)) {
            switch (childNode.getNodeType()) {
                case ELEMENT_NODE:
                    if (!hasChildNodes) {
                        hasChildNodes = true;
                        out.append("\n");
                    }
                    copyNode(out, indent + 1, childNode);
                    break;
                case TEXT_NODE:
                    out.append(childNode.getTextContent().trim());
                    break;
                default:
                    break;
            }
        }
        return hasChildNodes;
    }
}
