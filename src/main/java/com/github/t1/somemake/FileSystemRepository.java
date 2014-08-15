package com.github.t1.somemake;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.parsers.*;

import lombok.*;

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

    private final Path repositoryRoot;

    @Override
    public void put(Product product) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Product> get(@NonNull Version version) {
        return resolvePath(version).map((Path path) -> {
            XmlStoredProduct product = load(path);
            checkVersion(version, product.version(), path);
            return product;
        });
    }

    private Optional<Path> resolvePath(Version version) {
        Path idPath = repositoryRoot.resolve(version.id().path());
        if (Files.exists(idPath)) {
            Optional<String> resolvedVersion = version.resolve(versions(idPath));
            if (resolvedVersion.isPresent()) {
                Path versionPath = idPath.resolve(resolvedVersion.get());
                if (Files.exists(versionPath)) {
                    return Optional.of(versionPath.resolve("product.xml"));
                }
            }
        }
        return Optional.empty();
    }

    private Stream<String> versions(Path basePath) {
        try {
            return Files.list(basePath).map(p -> p.getFileName().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private XmlStoredProduct load(Path path) {
        Xml xml = new Xml(loadXml(path.toUri()));
        return new XmlStoredProduct(xml);
    }

    private void checkVersion(Version version, Version productVersion, Path path) {
        if (!version.matches(productVersion))
            throw new IllegalStateException("unexpected version in " + path + "\n" //
                    + "  expected: " + version + "\n" //
                    + "     found: " + productVersion);
    }
}
