package com.github.t1.somemake.model;

import static com.github.t1.somemake.model.Repositories.*;
import static com.github.t1.somemake.model.Type.*;
import static org.junit.Assert.*;

import java.nio.file.*;
import java.time.LocalDateTime;

import org.junit.*;

public class XmlStoredProductTest extends AbstractTest {
    private static final Path REPOSITORY_ROOT = Paths.get("src", "test", "resources", "repository");

    private static final Id LOMBOK_ID = dependency("org.projectlombok:lombok");
    private static final Version LOMBOK_VERSION = LOMBOK_ID.version("1.12.6");
    private static final String LOMBOK_NAME = "Project Lombok";
    private static final String LOMBOK_DESCRIPTION = "Simplify your code";

    private final FileSystemRepository repository = new FileSystemRepository(REPOSITORY_ROOT);

    @Before
    public void registerFileSystemRepository() {
        repositories().register(repository);
        activatedProducts.add(javac());
    }

    @After
    public void deregisterFileSystemRepository() {
        repositories().deregister(repository);
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
    public void shouldReadDependencyFromXmlFile() {
        Product lombok = repositories().get(LOMBOK_VERSION).get();

        assertEquals(LOMBOK_VERSION, lombok.version());
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
}
