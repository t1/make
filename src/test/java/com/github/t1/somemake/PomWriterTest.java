package com.github.t1.somemake;

import static com.github.t1.somemake.Type.*;
import static org.junit.Assert.*;

import java.io.StringWriter;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.junit.*;

public class PomWriterTest extends AbstractTest {
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
    public void shouldBuildBasicProduct() {
        Product product = newProduct(product("product:test-product"), "1.0") //
                .name("Test Product").description("A product used for tests") //
                .releaseTimestamp(LocalDateTime.of(2014, 8, 4, 15, 16, 59)) //
        ;
        PomWriter writer = new PomWriter(product, target);

        writer.write();

        assertEquals(normalize(readFile(Paths.get("src/test/resources/basic-product.pom"))), target.toString());
    }

    @Test
    public void shouldBuildProductWithDependencies() {
        Product product = Repositories.getInstance().get(product("product:test-product").version("1.0")).get();
        PomWriter writer = new PomWriter(product, target);

        writer.write();

        assertEquals(normalize(readFile(Paths.get("src/test/resources/test-product.pom"))), target.toString());
    }
}
