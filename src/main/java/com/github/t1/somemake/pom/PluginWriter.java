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

        copyProperties(element);
    }

    private void copyProperties(XmlElement element) {
        XmlElement configurationElement = null;
        for (Product property : product.features()) {
            if (property.type().is("inherited")) {
                element.addElement("inherited").addText(property.value().orElse("true"));
            } else {
                if (configurationElement == null)
                    configurationElement = element.addElement("configuration");
                copy(property, configurationElement);
            }
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