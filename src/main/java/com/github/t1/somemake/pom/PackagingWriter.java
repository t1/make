package com.github.t1.somemake.pom;

import static com.github.t1.somemake.model.Type.*;

import java.nio.file.*;
import java.util.Optional;

import com.github.t1.somemake.model.Product;
import com.github.t1.xml.XmlElement;

@PomSection(from = "packaging", to = "build")
class PackagingWriter extends PomSectionWriter {

    private static final Path MANIFEST = Paths.get("configuration/archive/manifest");

    public PackagingWriter(Product product) {
        super(product);
    }

    @Override
    protected void addTo(XmlElement out) {
        XmlElement plugins = out.getOrCreateElement("plugins");
        XmlElement plugin = plugins.addElement("plugin");

        copyPlugin(plugin);

        Optional<Product> mainClass = product.optionalFeature(p -> type("Main-Class").equals(p.type()));
        if (mainClass.isPresent()) {
            XmlElement manifest = plugin.getOrCreateElement(MANIFEST);
            manifest.addElement("Main-Class").addText(mainClass.get().value().get());
        }
    }

    private void copyPlugin(XmlElement to) {
        Product from = product.feature(f -> "plugin".equals(f.version().type().typeName()));
        gav(from.version(), to);
        copyProperties(from, to);
    }

    private void copyProperties(Product from, XmlElement element) {
        XmlElement configurationElement = null;
        for (Product property : from.features()) {
            if (configurationElement == null)
                configurationElement = element.addElement("configuration");
            copy(property, configurationElement);
        }
    }
}
