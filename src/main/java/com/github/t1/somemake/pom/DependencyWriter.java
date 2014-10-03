package com.github.t1.somemake.pom;

import static com.github.t1.somemake.model.Type.*;

import java.nio.file.*;

import com.github.t1.somemake.model.Product;
import com.github.t1.xml.XmlElement;

class DependencyWriter extends AbstractPomBuilder {
    public static final Path PATH = Paths.get("dependencies");

    public DependencyWriter(Product product) {
        super(product);
    }

    @Override
    public void addTo(XmlElement out) {
        XmlElement element = out.addElement("dependency");

        gav(element);

        addProperty(element, property("scope"));
        addProperty(element, property("classifier"));
        addProperty(element, property("optional"));
        addProperty(element, property("systemPath"));
        addProperty(element, property("type"));

        copyExlusions(element);
    }

    private void copyExlusions(XmlElement element) {
        XmlElement exclusionsElement = null;
        for (Product exclusion : product.features()) {
            if (exclusion.id().type().is("exclusion")) {
                if (exclusionsElement == null)
                    exclusionsElement = element.addElement("exclusions");
                XmlElement exclusionElement = exclusionsElement.addElement("exclusion");
                exclusionElement.addElement("groupId").addText(exclusion.id().groupId());
                exclusionElement.addElement("artifactId").addText(exclusion.id().artifactId());
            }
        }
    }
}
