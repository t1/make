package com.github.t1.somemake;

import static com.github.t1.somemake.Repositories.*;
import static com.github.t1.somemake.Type.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

import org.junit.*;

public class AbstractTest {
    protected final Repository repository = new InMemoryRepository();

    @Before
    public void registerInMemoryRepository() {
        repositories().register(repository);
    }

    @After
    public void deregisterInMemoryRepository() {
        repositories().deregister(repository);
    }

    protected Product createProduct() {
        return newProduct(product("test:prod"), "1.0") //
                .name("Test Product").description("A product used for tests") //
                .releaseTimestamp(LocalDateTime.of(2014, 8, 4, 15, 16, 59)) //
                .add(newProduct(feature("javaee-7").version("1.1"))) //
                .add(newProduct(dependency("ch.qos.logback:logback-classic").version("1.1.2"))) //
                .add(newProduct(dependency("junit:junit").version("4.11"))) //
                .add(newProduct(dependency("org.hamcrest:hamcrest-core").version("1.2.1"))) //
                .add(newProduct(dependency("org.mockito:mockito-all").version("1.9.5"))) //
        ;
    }

    protected Product newProduct(Id id, String version) {
        return newProduct(id.version(version));
    }

    protected Product newProduct(Version version) {
        return new ProductEntity(version);
    }

    protected String readFile(Path path) {
        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
