package com.github.t1.sMake;

import static com.github.t1.sMake.Type.*;
import static org.junit.Assert.*;

import java.net.URI;
import java.nio.file.*;

import org.junit.Test;

public class XmlStoredProductTest {
    private static final Path REPOSITORY = Paths.get("src", "test", "resources", "repository");

    @Test
    public void shouldReadFromXmlFile() {
        Version version = dependency("org.projectlombok:lombok").version("1.12.6");
        URI uri = REPOSITORY.resolve(version.path()).resolve("product.xml").toUri();

        Product product = new XmlStoredProduct(uri);

        assertEquals("product", product.type().typeName());
        assertEquals("org.projectlombok:lombok", product.id().idString());
        assertEquals("1.12.6", product.version().versionString());

        assertEquals(new ProductEntity(version), product.feature(version.id()));
    }
}
