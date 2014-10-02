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
    public void addTo(XmlElement dependenciesElement) {
        XmlElement dependencyElement = dependenciesElement.addElement("dependency");

        gav(dependencyElement);

        addProperty(dependencyElement, property("scope"));
        addProperty(dependencyElement, property("classifier"));
        addProperty(dependencyElement, property("optional"));
        addProperty(dependencyElement, property("systemPath"));
        addProperty(dependencyElement, property("type"));

        copyExlusions(dependencyElement);
    }

    private void copyExlusions(XmlElement dependencyElement) {
        XmlElement exclusionsElement = null;
        for (Product exclusion : product.features()) {
            if (exclusion.id().type().is("exclusion")) {
                if (exclusionsElement == null)
                    exclusionsElement = dependencyElement.addElement("exclusions");
                XmlElement exclusionElement = exclusionsElement.addElement("exclusion");
                exclusionElement.addElement("groupId").addText(exclusion.id().groupId());
                exclusionElement.addElement("artifactId").addText(exclusion.id().artifactId());
            }
        }
    }
}
