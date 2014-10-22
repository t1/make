package com.github.t1.somemake.pom;

import java.util.Optional;

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

    private void addPhase(Product property, XmlElement execution) {
        Optional<String> phase = property.attribute("phase");
        if (!phase.isPresent())
            throw new RuntimeException("execution requires a phase attribute");
        execution.addElement("phase").addText(phase.get());
    }

    private void addGoals(Product property, XmlElement goalsElement) {
        Optional<String> goalsString = property.attribute("goals");
        if (!goalsString.isPresent())
            throw new RuntimeException("execution requires a goals attribute");
        for (String goal : goalsString.get().split(",")) {
            goalsElement.addElement("goal").addText(goal);
        }
    }
}
