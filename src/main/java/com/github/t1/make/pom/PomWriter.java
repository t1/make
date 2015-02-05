package com.github.t1.make.pom;

import java.io.*;

import com.github.t1.make.model.Product;
import com.github.t1.xml.*;

public class PomWriter extends PomSectionWriter {
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
    protected XmlElement addTo(XmlElement out) {
        out.setAttribute("xmlns", NAMESPACE_NAME);
        out.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        out.setAttribute("xsi:schemaLocation", NAMESPACE_NAME + " http://maven.apache.org/xsd/maven-4.0.0.xsd");

        out.addComment("pom written by make; built from " + product.version());
        out.addElement("modelVersion").addText("4.0.0");
        out.nl();

        gav(out);
        out.nl();

        addFeature(out, "name", product.name());
        addFeature(out, "description", product.description());

        addSection(out, PackagingWriter.class);
        addSection(out, PluginWriter.class);

        DependencyWriter.addDependencies(product, out);

        return out;
    }
}
