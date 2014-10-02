package com.github.t1.somemake.pom;

import java.nio.file.*;

import com.github.t1.somemake.model.Product;
import com.github.t1.xml.XmlElement;

class PluginWriter extends AbstractPomBuilder {
    public static final Path PATH = Paths.get("build/plugins");

    public PluginWriter(Product product) {
        super(product);
    }

    @Override
    public void addTo(XmlElement out) {
        gav(out.addElement("plugin"));
    }
}
