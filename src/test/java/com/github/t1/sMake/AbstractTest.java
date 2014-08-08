package com.github.t1.sMake;

import static com.github.t1.sMake.Type.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

import org.junit.Before;

public class AbstractTest {
    protected final Repository repository = new InMemoryRepository();

    @Before
    public void before() {
        Repository.INSTANCE.set(repository);
    }

    protected Product createProduct() {
        return newProduct(product("test:prod"), "1.0") //
                .name("Test Product").description("A product used for tests") //
                .releaseTimestamp(LocalDateTime.of(2014, 8, 4, 15, 16, 59)) //
                .feature(newProduct(feature("javaee-7").version("1.1"))) //
                // .feature(newProduct(dependency("org.projectlombok:lombok").version("1.12.6"))) //
                .feature(newProduct(testDependency("ch.qos.logback:logback-classic").version("1.1.2"))) //
                .feature(newProduct(testDependency("junit:junit").version("4.11"))) //
                .feature(newProduct(testDependency("org.hamcrest:hamcrest-core").version("1.2.1"))) //
                .feature(newProduct(testDependency("org.mockito:mockito-all").version("1.9.5"))) //
        ;
    }

    protected Product newProduct(Id id, String version) {
        return newProduct(id.version(version));
    }

    private ProductEntity newProduct(Version version) {
        return new ProductEntity(version);
    }

    protected String readFile(Path path) {
        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String normalize(String xml) {
        return xml.replace(" />", "/>").replaceAll("(?m)<!--.*-->", "");
    }
}
