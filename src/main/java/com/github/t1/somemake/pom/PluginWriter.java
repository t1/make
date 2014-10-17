package com.github.t1.somemake.pom;

import java.util.Optional;

import com.github.t1.somemake.model.Product;
import com.github.t1.xml.XmlElement;
import com.google.common.collect.ImmutableList;

@PomSection(from = "plugin", to = "build/plugins")
class PluginWriter extends PomSectionWriter {
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
            Optional<String> value = from.value();
            if (value.isPresent()) {
                subTo.addText(value.get());
            }
        }
    }
}
