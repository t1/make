package com.github.t1.make.pom;

import static com.github.t1.make.model.Product.*;
import static com.github.t1.make.model.Type.*;

import java.util.*;

import com.github.t1.make.model.*;
import com.github.t1.xml.XmlElement;

class DependencyWriter extends PomSectionWriter {
    public static void addDependencies(Product from, XmlElement to) {
        List<Product> topScopes = from.features(matching(type("scope")));
        List<Product> dependencies = from.features(matching(type("dependency")));
        if (dependencies.isEmpty() && topScopes.isEmpty())
            return;

        to.nl();
        XmlElement sectionElement = to.getOrCreateElement("dependencies");

        GroupingWriter grouping = new GroupingWriter();
        for (Scope scope : Scope.values()) {
            for (Product topScope : topScopes) {
                for (Product dependency : topScope.features(matching(type("dependency")))) {
                    if (scope.toString().equals(topScope.id().idString())) {
                        DependencyWriter writer = new DependencyWriter(dependency);
                        grouping.write(scope, sectionElement);
                        writer.addTo(sectionElement).addElement("scope").value(scope.toString());
                    }
                }
            }
            for (Product dependency : dependencies) {
                DependencyWriter writer = new DependencyWriter(dependency);
                if (scope == writer.scope()) {
                    grouping.write(scope, sectionElement);
                    writer.addTo(sectionElement);
                }
            }
        }
    }

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

    private static final Id SCOPE = emptyId("scope");

    public DependencyWriter(Product product) {
        super(product);
    }

    @Override
    public XmlElement addTo(XmlElement out) {
        XmlElement element = out.addElement("dependency");

        gav(element);

        addFeature(element, SCOPE);
        addFeature(element, emptyId("classifier"));
        addFeature(element, emptyId("optional"));
        addFeature(element, emptyId("systemPath"));
        addFeature(element, emptyId("type"));

        copyExlusions(element);

        return element;
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
