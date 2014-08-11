package com.github.t1.sMake;

import static com.github.t1.sMake.Type.*;
import static org.junit.Assert.*;

import java.nio.file.Paths;

import org.junit.*;

public class XmlStoredProductTest {
    @Before
    public void before() {
        Repository.INSTANCE.set(new FileSystemRepository(Paths.get("src", "test", "resources", "repository")));
    }

    @Test
    public void shouldReadFromXmlFile() {
        Version version = dependency("org.projectlombok:lombok").version("1.12.6");
        Product product = Repository.INSTANCE.get().get(version).get();

        assertEquals("product", product.type().typeName());
        assertEquals("org.projectlombok:lombok", product.id().idString());
        assertEquals("1.12.6", product.version().versionString());

        assertEquals(new ProductEntity(version), product.feature(version.id()));
    }
}
