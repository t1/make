package com.github.t1.sMake;

import static com.github.t1.sMake.Type.*;
import static org.junit.Assert.*;

import java.io.StringWriter;
import java.nio.file.*;

import org.junit.*;

public class PomWriterTest extends AbstractTest {
    private static final Path POM1_XML = Paths.get("src/test/resources/product1.pom.xml");

    private final FileSystemRepository repository = new FileSystemRepository(Paths.get("src", "test", "resources",
            "repository"));

    @Before
    public void registerFileSystemRepository() {
        Repositories.getInstance().register(repository);
    }

    @After
    public void deregisterFileSystemRepository() {
        Repositories.getInstance().deregister(repository);
    }

    private final StringWriter target = new StringWriter();

    @SuppressWarnings("unused")
    @Test(expected = NullPointerException.class)
    public void shouldFailToBuildNullProduct() {
        new PomWriter(null, target);
    }

    @SuppressWarnings("unused")
    @Test(expected = NullPointerException.class)
    public void shouldFailToBuildNullTarget() {
        new PomWriter(createProduct(), null);
    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToBuildFeature() {
        new PomWriter(newProduct(feature("test-feature"), "1.0"), target);
    }

    @Test
    @Ignore
    public void shouldBuildBasicProduct() {
        Product product = Repositories.getInstance().get(product("product:test-product").version("1.0")).get();
        PomWriter writer = new PomWriter(product, target);

        writer.write();

        assertEquals(normalize(readFile(POM1_XML)), target.toString());
    }
}
