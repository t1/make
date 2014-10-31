package com.github.t1.make.pom;

import static com.github.t1.make.model.Product.*;
import static com.github.t1.make.model.Type.*;

import java.util.*;

import com.github.t1.make.model.Product;
import com.github.t1.xml.XmlElement;

@PomSection(from = "plugin", to = "build/plugins")
class PluginWriter extends PomSectionWriter {
    public PluginWriter(Product product) {
        super(product);
    }

    @Override
    public XmlElement addTo(XmlElement out) {
        List<Product> plugins = product.features(matching(type("plugin")));
        XmlElement element;
        if (plugins.isEmpty()) {
            element = addPlugin(product, out);
        } else {
            element = null;
            for (Product plugin : plugins) {
                element = addPlugin(plugin, out);
            }
        }

        return element;
    }

    private static XmlElement addPlugin(Product product, XmlElement out) {
        XmlElement element = out.addElement("plugin");
        gav(product.version(), element);
        copyProperties(product, element);
        return element;
    }

    private static void copyProperties(Product product, XmlElement element) {
        XmlElement configuration = null;
        XmlElement executions = null;

        for (Product property : product.features()) {
            if (property.type().is("inherited")) {
                element.addElement("inherited").addText(property.value().orElse("true"));
            } else if (property.type().is("execution")) {
                if (executions == null)
                    executions = element.addElement("executions");
                XmlElement execution = executions.addElement("execution");
                addPhase(property, execution);
                addGoals(property, execution.addElement("goals"));
            } else {
                if (configuration == null)
                    configuration = element.addElement("configuration");
                copy(property, configuration);
            }
        }
    }

    private static void addPhase(Product property, XmlElement execution) {
        Optional<String> phase = property.attribute("phase");
        if (!phase.isPresent())
            throw new RuntimeException("execution requires a phase attribute");
        execution.addElement("phase").addText(phase.get());
    }

    private static void addGoals(Product property, XmlElement goalsElement) {
        Optional<String> goalsString = property.attribute("goals");
        if (!goalsString.isPresent())
            throw new RuntimeException("execution requires a goals attribute");
        for (String goal : goalsString.get().split(",")) {
            goalsElement.addElement("goal").addText(goal);
        }
    }
}
