package com.github.t1.somemake.model;

import static com.github.t1.somemake.model.Repositories.*;
import static com.github.t1.somemake.model.Type.*;
import static com.github.t1.somemake.model.Version.*;
import static java.util.stream.Collectors.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.Test;

public class JsonStoredProductTest extends AbstractFileRepositoryTest {
    private String json(String versionString, String... elements) {
        return ("{" //
                + "'-type':'product'," //
                + "'-id':'product:test-product'," //
                + "'-version':'" + versionString + "'" //
                + ((elements.length == 0) ? "" : ",") //
                + Arrays.asList(elements).stream().collect(joining(",")) //
        + "}").replace('\'', '\"');
    }

    private void write(Path dir, String json) throws IOException {
        Files.createDirectories(dir);
        Files.write(dir.resolve("product.json"), json.getBytes());
    }

    private void assertJunitHamcrestMockito(Product jhm) {
        Product junit = jhm.feature(dependency("junit:junit"));
        assertEquals("4.11", junit.version().versionString());

        Product hamcrest = jhm.feature(dependency("org.hamcrest:hamcrest-core"));
        assertEquals("1.2.1", hamcrest.version().versionString());

        Product mockito = jhm.feature(dependency("org.mockito:mockito-all"));
        assertEquals("1.9.5", mockito.version().versionString());
    }

    @Test
    public void shouldReadDependencyFromJsonFile() {
        Product lombok = repositories().get(LOMBOK_VERSION).get();

        assertEquals(LOMBOK_ID.version(ANY), lombok.version());
        assertEquals(LOMBOK_NAME, lombok.name().get());
        assertEquals(LOMBOK_DESCRIPTION, lombok.description().get());
        assertFalse(lombok.releaseTimestamp().isPresent());
    }

    @Test
    public void shouldResolveDependencyFromFileSystem() {
        Product product = createProduct().add(newProduct(LOMBOK_VERSION));

        Product lombok = product.feature(LOMBOK_ID);
        assertEquals(LOMBOK_VERSION, lombok.version());
        assertEquals(LOMBOK_NAME, lombok.name().get());
        assertEquals(LOMBOK_DESCRIPTION, lombok.description().get());
        assertFalse(lombok.releaseTimestamp().isPresent());
    }

    @Test
    public void shouldRetainNameWhenResolvingDependencyFromFileSystem() {
        Product product = createProduct().add(newProduct(LOMBOK_VERSION).name("foo"));

        Product lombok = product.feature(LOMBOK_ID);
        assertEquals(LOMBOK_VERSION, lombok.version());
        assertEquals("foo", lombok.name().get());
        assertEquals(LOMBOK_DESCRIPTION, lombok.description().get());
    }

    @Test
    public void shouldReadDependenciesWhenResolvingFromFileSystem() {
        Product product = newProduct(product("test:prod"), "1.0") //
                .add(newProduct(feature("com.github.t1:junit-hamcrest-mockito").version("1.0"))) //
        ;

        Product jhm = product.feature(feature("com.github.t1:junit-hamcrest-mockito"));

        assertJunitHamcrestMockito(jhm);
    }

    @Test
    public void shouldMergeDependenciesWhenResolvingFromFileSystem() {
        Product container = new ProductEntity(product("test:prod").version("1.0")) //
                .add(new ProductEntity(feature("com.github.t1:junit-hamcrest-mockito").version("1.0")) //
                        .add(new ProductEntity(LOMBOK_VERSION))) //
        ;

        Product jhm = container.feature(feature("com.github.t1:junit-hamcrest-mockito"));

        assertJunitHamcrestMockito(jhm);

        Product lombok = jhm.feature(LOMBOK_ID);
        assertEquals(LOMBOK_VERSION, lombok.version());
        assertEquals(LOMBOK_NAME, lombok.name().get());
        assertEquals(LOMBOK_DESCRIPTION, lombok.description().get());
    }

    @Test
    public void shouldOverwriteScopeWhenMerging() {
        ProductEntity jhm2 = new ProductEntity(feature("com.github.t1:junit-hamcrest-mockito").version("1.0"));
        // .set(LOMBOK_ID, LOMBOK_DESCRIPTION);
        Product container = new ProductEntity(product("test:prod").version("1.0")).add(jhm2);

        Product jhm = container.feature(feature("com.github.t1:junit-hamcrest-mockito"));

        assertJunitHamcrestMockito(jhm);
    }

    @Test
    public void shouldReadProductFromJsonFile() {
        Version version = product("product:test-product").version("101.0");
        Product product = repositories().get(version).get();

        assertEquals(version, product.version());
        assertEquals("Test Product", product.name().get());
        assertEquals("A product used for tests", product.description().get());
        assertEquals(LocalDateTime.of(2014, 8, 4, 15, 16, 59), product.releaseTimestamp().get());
    }

    @Test
    public void shouldWriteProductToJsonFile() throws IOException {
        Path pathOut = REPOSITORY_ROOT.resolve("product/product/test-product/999/product.json");
        Version version = product("product:test-product").version("999");

        try {
            Product product = new JsonStoredProduct(version).description("description");
            product.add(newProduct(LOMBOK_VERSION).name("foo"));
            product.add(newProduct(dependency("foo:bar").version("1")).name("baz"));

            fileRepository.store(product);

            assertEquals(json("999", //
                    "'description':'description'", //
                    "'dependencies':[" //
                            + "{" //
                            + "'-type':'dependency'," //
                            + "'-id':'org.projectlombok:lombok'," //
                            + "'-version':'1.12.6'," //
                            + "'name':'foo'" //
                            + "}," //
                            + "{" //
                            + "'-type':'dependency'," //
                            + "'-id':'foo:bar'," //
                            + "'-version':'1'," //
                            + "'name':'baz'" //
                            + "}" //
                            + "]" //
            ), readFile(pathOut));
        } finally {
            Files.deleteIfExists(pathOut);
        }
    }

    @Test
    public void shouldUpdateProductVersionFromJsonFileAndStore() throws IOException {
        Path dir = REPOSITORY_ROOT.resolve("product/product/test-product");
        Path pathOut = dir.resolve("999/product.json");
        try {
            write(dir.resolve("998"), json("998"));
            Version versionIn = product("product:test-product").version("998");
            Product product = repositories().get(versionIn).get();

            product.versionString("999");

            fileRepository.store(product);

            assertEquals(json("999"), readFile(pathOut));
        } finally {
            Files.deleteIfExists(pathOut);
        }
    }
}
