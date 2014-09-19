package com.github.t1.somemake;

import static com.github.t1.somemake.Repositories.*;
import static com.github.t1.somemake.Type.*;
import static org.junit.Assert.*;

import java.io.StringWriter;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.junit.*;

public class PomWriterTest extends AbstractTest {
    private final FileSystemRepository repository = new FileSystemRepository(Paths.get("target", "test-classes",
            "repository"));

    @Before
    public void registerFileSystemRepository() {
        repositories().register(repository);
    }

    @After
    public void deregisterFileSystemRepository() {
        repositories().deregister(repository);
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

        assertEquals(readFile(Paths.get("src/test/resources/basic-product.pom")), target.toString());
    }

    @Test
    public void shouldBuildProductWithDependencies() {
        Product product = repositories().get(product("product:test-product").version("1.0")).get();
        PomWriter writer = new PomWriter(product, target);

        writer.write();

        assertEquals(readFile(Paths.get("src/test/resources/test-product-1.0.pom")), target.toString());
    }

    @Test
    // FIXME
    @Ignore
    public void shouldBuildProductWithIndirectDependencies() {
        Product product = repositories().get(product("product:test-product").version("1.1")).get();
        PomWriter writer = new PomWriter(product, target);

        writer.write();

        assertEquals(readFile(Paths.get("src/test/resources/test-product-1.1.pom")), target.toString());
    }

    @Test
    @Ignore
    public void shouldOverwriteNestedPluginProperty() {
        Product product = repositories().get(product("product:test-product").version("1.2")).get();
        PomWriter writer = new PomWriter(product, target);

        writer.write();

        assertEquals(readFile(Paths.get("src/test/resources/test-product-1.2.pom")), target.toString());
    }

    @Test
    @Ignore
    public void shouldWriteParametersCompilerArgumentOnJdk7() {
        assertFalse(buildParametersCompilerArgumentOn("1.7"));
    }

    @Test
    @Ignore
    public void shouldWriteParametersCompilerArgumentOnJdk71() {
        assertFalse(buildParametersCompilerArgumentOn("1.7.1"));
    }

    @Test
    public void shouldWriteParametersCompilerArgumentOnJdk8() {
        assertTrue(buildParametersCompilerArgumentOn("1.8"));
    }

    @Test
    public void shouldWriteParametersCompilerArgumentOnJdk9() {
        assertTrue(buildParametersCompilerArgumentOn("1.9"));
    }

    @Test
    public void shouldWriteParametersCompilerArgumentOnJdk10() {
        assertTrue(buildParametersCompilerArgumentOn("1.10"));
    }

    private boolean buildParametersCompilerArgumentOn(String jdk) {
        String oldVersion = System.getProperty("java.specification.version");
        try {
            System.setProperty("java.specification.version", jdk);

            Product product = repositories().get(product("product:test-product").version("1.1")).get();
            PomWriter writer = new PomWriter(product, target);

            writer.write();

            return target.toString().contains("<compilerArgument>-parameters</compilerArgument>");
        } finally {
            System.setProperty("java.specification.version", oldVersion);
        }
    }
}
