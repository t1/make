package com.github.t1.somemake.model;

import static com.github.t1.somemake.model.Repositories.*;
import static com.github.t1.somemake.model.Type.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

import org.junit.*;

public abstract class AbstractTest {
    protected List<Product> activatedProducts = new ArrayList<>();

    public static String readFile(Path path) {
        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
        ProductEntity result = new ProductEntity(version);
        for (Product product : activatedProducts) {
            result.add(product);
        }
        return result;
    }

    // TODO activate javac automatically
    protected static Product javac() {
        return repositories().get(Type.type("plugin").id("compiler.java").version("3.1")).get();
    }
}
