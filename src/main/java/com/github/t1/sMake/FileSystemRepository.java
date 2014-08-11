package com.github.t1.sMake;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;

import javax.xml.parsers.*;

import lombok.AllArgsConstructor;

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
    public Optional<Product> get(Version version) {
        URI uri = path.resolve(version.path()).resolve("product.xml").toUri();
        Xml xml = new Xml(loadXml(uri));
        return Optional.of(new XmlStoredProduct(xml));
    }
}
