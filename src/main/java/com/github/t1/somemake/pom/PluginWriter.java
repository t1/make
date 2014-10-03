package com.github.t1.somemake.pom;

import java.nio.file.*;

import com.github.t1.somemake.model.Product;
import com.github.t1.xml.XmlElement;
import com.google.common.collect.ImmutableList;

class PluginWriter extends AbstractPomBuilder {
    public static final Path PATH = Paths.get("build/plugins");

    public PluginWriter(Product product) {
        super(product);
    }

    @Override
    public void addTo(XmlElement out) {
        XmlElement element = out.addElement("plugin");

        gav(element);

        copyConfiguration(element);
    }

    private void copyConfiguration(XmlElement element) {
        XmlElement configurationElement = null;
        for (Product configuration : product.features()) {
            if (configurationElement == null)
                configurationElement = element.addElement("configuration");
            copy(configuration, configurationElement);
        }
    }

    private void copy(Product from, XmlElement to) {
        XmlElement subTo = to.addElement(from.type().toString());
        if (from.hasFeatures()) {
            ImmutableList<Product> features = from.features();
            for (Product subFrom : features) {
                copy(subFrom, subTo);
            }
        } else {
            subTo.addText(from.value().get());
        }
    }
}
