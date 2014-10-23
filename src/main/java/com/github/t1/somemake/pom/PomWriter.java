package com.github.t1.somemake.pom;

import java.io.*;
import java.nio.file.*;
import java.util.List;

import com.github.t1.somemake.model.Product;
import com.github.t1.xml.*;

public class PomWriter extends PomSectionWriter {
    private static final String[] SCOPES = { "provided", "compile", "runtime", "test", "system" };
    private static final String NAMESPACE_NAME = "http://maven.apache.org/POM/4.0.0";

    public PomWriter(Product product) {
        super(product);
    }

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
        addTo(out);
        return out;
    }

    @Override
    protected void addTo(XmlElement out) {
        out.addAttribute("xmlns", NAMESPACE_NAME);
        out.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        out.addAttribute("xsi:schemaLocation", NAMESPACE_NAME + " http://maven.apache.org/xsd/maven-4.0.0.xsd");

        out.addComment("pom written by somemake; built from " + product.version());
        out.addElement("modelVersion").addText("4.0.0");
        out.nl();

        gav(out);
        out.nl();

        addProperty(out, "name", product.name());
        addProperty(out, "description", product.description());

        addSection(out, PackagingWriter.class);
        addSection(out, PluginWriter.class);
        addDependencies(out);
    }

    private void addDependencies(XmlElement out) {
        PomSection sectionAnnotation = pomSection(DependencyWriter.class);
        List<Product> list = featuresOfType(sectionAnnotation.from());
        if (list.isEmpty())
            return;

        Path path = Paths.get(sectionAnnotation.to());
        XmlElement sectionElement = out.getOrCreateElement(path);

        boolean firstScope = true;
        for (String scope : SCOPES) {
            boolean firstDependency = true;
            for (Product product : list) {
                DependencyWriter writer = createWriter(DependencyWriter.class, product);
                if (scope.equals(writer.scope())) {
                    if (firstDependency) {
                        if (firstScope) {
                            firstScope = false;
                        } else {
                            sectionElement.nl();
                        }
                        firstDependency = false;
                        sectionElement.addComment(scope + " scope");
                    }

                    writer.addTo(sectionElement);
                }
            }
        }
    }
}
