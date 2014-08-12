package com.github.t1.sMake;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Optional;

import javax.xml.parsers.*;

import lombok.*;

import org.jboss.weld.exceptions.UnsupportedOperationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.github.t1.xml.Xml;

@AllArgsConstructor
public class FileSystemRepository implements Repository {
    public static Document loadXml(URI uri) {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(uri.toASCIIString());
            // JsonObject obj = Json.createReader(uri.toURL().openStream()).readObject();
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private final Path path;


    @Override
    public void put(Product product) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Product> get(@NonNull Version version) {
        Path filePath = path.resolve(version.path()).resolve("product.xml");
        if (!Files.exists(filePath))
            return Optional.empty();

        Xml xml = new Xml(loadXml(filePath.toUri()));
        XmlStoredProduct product = new XmlStoredProduct(xml);
        if (!version.equals(product.version()))
            throw new IllegalStateException("unexpected version in " + filePath + "\n" //
                    + "  expected: " + version + "\n" //
                    + "     found: " + product.version());
        return Optional.of(product);
    }
}
