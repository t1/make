package com.github.t1.make.model;

import static com.github.t1.make.model.Repositories.*;
import static com.github.t1.make.model.Type.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

import org.junit.*;

public abstract class AbstractTest {
    public static String readFile(Path path) {
        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected final Repository memRepository = new InMemoryRepository();

    @Before
    public void registerInMemoryRepository() {
        repositories().register(memRepository);
    }

    @After
    public void deregisterInMemoryRepository() {
        repositories().deregister(memRepository);
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

    protected final Product newProduct(Id id, String version) {
        return newProduct(id.version(version));
    }

    protected final Product newProduct(Version version) {
        return new ProductEntity(version);
    }
}
