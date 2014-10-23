package com.github.t1.somemake.pom;

import static com.github.t1.somemake.model.Product.*;
import static com.github.t1.somemake.model.Type.*;

import java.util.*;

import com.github.t1.somemake.model.*;
import com.github.t1.xml.XmlElement;

class DependencyWriter extends PomSectionWriter {
    public static void addDependencies(Product from, XmlElement to) {
        List<Product> list = from.features(matching(type("dependency")));
        if (list.isEmpty())
            return;

        to.nl();
        XmlElement sectionElement = to.getOrCreateElement("dependencies");

        GroupingWriter grouping = new GroupingWriter();
        for (Scope scope : Scope.values()) {
            for (Product dependency : list) {
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
