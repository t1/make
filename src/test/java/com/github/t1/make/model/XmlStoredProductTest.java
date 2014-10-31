package com.github.t1.make.model;

import static com.github.t1.make.model.Repositories.*;
import static com.github.t1.make.model.Type.*;
import static com.github.t1.make.model.Version.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

import org.junit.Test;

public class XmlStoredProductTest extends AbstractFileRepositoryTest {
    private void assertJunitHamcrestMockito(Product jhm) {
        Product junit = jhm.feature(dependency("junit:junit"));
        assertEquals("4.11", junit.version().versionString());

        Product hamcrest = jhm.feature(dependency("org.hamcrest:hamcrest-core"));
        assertEquals("1.2.1", hamcrest.version().versionString());

        Product mockito = jhm.feature(dependency("org.mockito:mockito-all"));
        assertEquals("1.9.5", mockito.version().versionString());
    }

    @Test
    public void shouldReadDependencyFromXmlFile() {
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
    public void shouldReadProductFromXmlFile() {
        Version version = product("product:test-product").version("1.0");
        Product product = repositories().get(version).get();

        assertEquals(version, product.version());
        assertEquals("Test Product", product.name().get());
        assertEquals("A product used for tests", product.description().get());
        assertEquals(LocalDateTime.of(2014, 8, 4, 15, 16, 59), product.releaseTimestamp().get());
    }

    @Test
    public void shouldWriteProductToXmlFile() throws IOException {
        Path pathOut = REPOSITORY_ROOT.resolve("product/product/test-product/999/product.xml");
        Version version = product("product:test-product").version("999");

        try {
            Product product = new XmlStoredProduct(version);
            product.add(newProduct(LOMBOK_VERSION).name("foo"));

            fileRepository.store(product);

            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                    + "<product id=\"product:test-product\" version=\"999\">\n" //
                    + "    <dependency id=\"org.projectlombok:lombok\" version=\"1.12.6\">\n" //
                    + "        <name>foo</name>\n" //
                    + "    </dependency>\n" //
                    + "</product>\n" //
            , readFile(pathOut) + "\n");
        } finally {
            Files.deleteIfExists(pathOut);
        }
    }

    @Test
    public void shouldUpdateProductVersionFromXmlFileAndStore() throws IOException {
        Path pathOut = REPOSITORY_ROOT.resolve("product/product/test-product/999/product.xml");

        try {
            Version versionIn = product("product:test-product").version("1.0");
            Product product = repositories().get(versionIn).get();

            product.versionString("999");

            fileRepository.store(product);

            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                    + "<product id=\"product:test-product\" version=\"999\">\n" //
                    + "    <name>Test Product</name>\n" //
                    + "    <description>A product used for tests</description>\n" //
                    + "    <releaseTimestamp>2014-08-04T15:16:59</releaseTimestamp>\n" //
                    + "\n" //
                    + "    <dependency id=\"ch.qos.logback:logback-classic\" version=\"1.1*\">\n" //
                    + "        <scope>test</scope>\n" //
                    + "        <classifier>cls</classifier>\n" //
                    + "        <optional>true</optional>\n" //
                    + "        <systemPath>sys</systemPath>\n" //
                    + "        <type>type</type>\n" //
                    + "    </dependency>\n" //
                    + "</product>" //
            , readFile(pathOut));
        } finally {
            Files.deleteIfExists(pathOut);
        }
    }
}
