package com.github.t1.make.pom;

import static com.github.t1.make.model.Product.*;
import static com.github.t1.make.model.Type.*;

import java.nio.file.*;
import java.util.Optional;

import com.github.t1.make.model.*;
import com.github.t1.xml.XmlElement;

@PomSection(from = "packaging", to = "build")
class PackagingWriter extends PomSectionWriter {
    private static final Type PLUGIN = type("plugin");
    private static final Path MANIFEST = Paths.get("configuration/archive/manifest");

    public PackagingWriter(Product product) {
        super(product);
    }

    @Override
    protected XmlElement addTo(XmlElement build) {
        copyFinalName(build);

        XmlElement plugins = build.getOrCreateElement("plugins");
        XmlElement plugin = plugins.addElement("plugin");

        copyPlugin(plugin);
        copyMainClass(plugin);

        return plugins;
    }

    private void copyFinalName(XmlElement build) {
        Optional<Product> finalName = product.optionalFeature(p -> type("finalName").equals(p.type()));
        if (finalName.isPresent()) {
            build.addElement("finalName").addText(finalName.get().value().get());
        }
    }

    private void copyPlugin(XmlElement to) {
        Product from = product.feature(matching(PLUGIN));
        gav(from.version(), to);
        copyFeatures(from, to);
    }

    private void copyFeatures(Product from, XmlElement element) {
        XmlElement configurationElement = null;
        for (Product product : from.features()) {
            if (configurationElement == null)
                configurationElement = element.addElement("configuration");
            copy(product, configurationElement);
        }
    }

    private void copyMainClass(XmlElement plugin) {
        Optional<Product> mainClass = product.optionalFeature(p -> type("Main-Class").equals(p.type()));
        if (mainClass.isPresent()) {
            XmlElement manifest = plugin.getOrCreateElement(MANIFEST);
            manifest.addElement("Main-Class").addText(mainClass.get().value().get());
        }
    }
}
