package com.github.t1.somemake.pom;

import static com.github.t1.somemake.model.Type.*;

import java.util.Optional;

import com.github.t1.somemake.model.*;
import com.github.t1.xml.XmlElement;

@PomSection(from = "dependency", to = "dependencies")
class DependencyWriter extends PomSectionWriter {
    public static class GroupingWriter {
        private Scope lastScope = null;

        public void write(Scope scope, XmlElement element) {
            if (scope != lastScope) {
                if (lastScope != null)
                    element.nl();
                element.addComment(scope + " scope");
            }
            lastScope = scope;
        }
    }

    private static final Id SCOPE = property("scope");

    public DependencyWriter(Product product) {
        super(product);
    }

    @Override
    public void addTo(XmlElement out) {
        XmlElement element = out.addElement("dependency");

        gav(element);

        addProperty(element, SCOPE);
        addProperty(element, property("classifier"));
        addProperty(element, property("optional"));
        addProperty(element, property("systemPath"));
        addProperty(element, property("type"));

        copyExlusions(element);
    }

    public Scope scope() {
        Optional<Product> optional = product.optionalFeature(SCOPE);
        return optional.isPresent() ? Scope.valueOf(optional.get().value().get()) : Scope.DEFAULT;
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
