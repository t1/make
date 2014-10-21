package com.github.t1.somemake.pom;

import com.github.t1.somemake.model.Product;
import com.github.t1.xml.XmlElement;

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
}
