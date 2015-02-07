package com.github.t1.make.pom;

import com.github.t1.make.model.Product;
import com.github.t1.xml.XmlElement;

@PomSection(from = "property", to = "properties")
public class PropertiesWriter extends PomSectionWriter {
    public PropertiesWriter(Product product) {
        super(product);
    }

    @Override
    protected XmlElement addTo(XmlElement properties) {
        XmlElement property = properties.addElement(product.id().idString());
        product.value().ifPresent(v -> property.value(v));
        return properties;
    }
}
