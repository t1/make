package com.github.t1.sMake;

import static com.github.t1.sMake.Type.*;
import static org.junit.Assert.*;

import java.nio.file.Paths;

import org.junit.*;

public class XmlStoredProductTest extends AbstractTest {
    private static final Id LOMBOK_ID = dependency("org.projectlombok:lombok");
    private static final Version LOMBOK_VERSION = LOMBOK_ID.version("1.12.6");
    private static final String LOMBOK_NAME = "Project Lombok";
    private static final String LOMBOK_DESCRIPTION = "Simplify your code";

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

    private void assertJunitHamcrestMockito(Product jhm) {
        Product junit = jhm.get(dependency("junit:junit"));
        assertEquals("4.11", junit.version().versionString());

        Product hamcrest = jhm.get(dependency("org.hamcrest:hamcrest-core"));
        assertEquals("1.2.1", hamcrest.version().versionString());

        Product mockito = jhm.get(dependency("org.mockito:mockito-all"));
        assertEquals("1.9.5", mockito.version().versionString());
    }

    @Test
    public void shouldReadDependencyFromXmlFile() {
        Product lombok = Repositories.getInstance().get(LOMBOK_VERSION).get();

        assertEquals(LOMBOK_VERSION, lombok.version());
        assertEquals(LOMBOK_NAME, lombok.name());
        assertEquals(LOMBOK_DESCRIPTION, lombok.description());
        assertEquals(null, lombok.releaseTimestamp());
    }

    @Test
    public void shouldResolveDependencyFromFileSystem() {
        Product product = createProduct().add(newProduct(LOMBOK_VERSION));

        Product lombok = product.get(LOMBOK_ID);
        assertEquals(LOMBOK_VERSION, lombok.version());
        assertEquals(LOMBOK_NAME, lombok.name());
        assertEquals(LOMBOK_DESCRIPTION, lombok.description());
    }

    @Test
    public void shouldRetainNameWhenResolvingDependencyFromFileSystem() {
        Product product = createProduct().add(newProduct(LOMBOK_VERSION).name("foo"));

        Product lombok = product.get(LOMBOK_ID);
        assertEquals(LOMBOK_VERSION, lombok.version());
        assertEquals("foo", lombok.name());
        assertEquals(LOMBOK_DESCRIPTION, lombok.description());
    }

    @Test
    public void shouldReadDependenciesWhenResolvingFromFileSystem() {
        Product product = newProduct(product("test:prod"), "1.0") //
                .name("Test Product").description("A product used for tests") //
                .add(newProduct(feature("com.github.t1:junit-hamcrest-mockito").version("1.0"))) //
        ;

        Product jhm = product.get(feature("com.github.t1:junit-hamcrest-mockito"));

        assertJunitHamcrestMockito(jhm);
    }

    @Test
    public void shouldMergeDependenciesWhenResolvingFromFileSystem() {
        Product product = newProduct(product("test:prod"), "1.0") //
                .name("Test Product").description("A product used for tests") //
                .add(newProduct(feature("com.github.t1:junit-hamcrest-mockito").version("1.0")) //
                        .add(newProduct(LOMBOK_VERSION))) //
        ;

        Product jhm = product.get(feature("com.github.t1:junit-hamcrest-mockito"));

        assertJunitHamcrestMockito(jhm);

        Product lombok = jhm.get(LOMBOK_ID);
        assertEquals(LOMBOK_VERSION, lombok.version());
        assertEquals(LOMBOK_NAME, lombok.name());
        assertEquals(LOMBOK_DESCRIPTION, lombok.description());
    }
}
