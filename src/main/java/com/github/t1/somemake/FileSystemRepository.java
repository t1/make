package com.github.t1.somemake;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Optional;
import java.util.stream.Stream;

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
        return scan(version).map(path -> {
            Xml xml = new Xml(loadXml(path.toUri()));
            XmlStoredProduct product = new XmlStoredProduct(xml);
            if (!version.equals(product.version()))
                throw new IllegalStateException("unexpected version in " + path + "\n" //
                        + "  expected: " + version + "\n" //
                        + "     found: " + product.version());
            return product;
        });
    }

    private Optional<Path> scan(Version version) {
        Path basePath = path.resolve(version.id().path());
        if (!Files.exists(basePath))
            return Optional.empty();
        String resolvedVersion = version.resolve(versions(basePath));
        Path filePath = basePath.resolve(resolvedVersion).resolve("product.xml");
        if (!Files.exists(filePath))
            return Optional.empty();
        return Optional.of(filePath);
    }

    private Stream<String> versions(Path basePath) {
        try {
            return Files.list(basePath).map(p -> p.getFileName().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
