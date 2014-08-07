package com.github.t1.sMake;

import static com.github.t1.sMake.Type.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

import org.junit.Before;

public class AbstractTest {
    protected final Repository repository = new ImMemoryRepository();

    @Before
    public void before() {
        Repository.INSTANCE.set(repository);
    }

    protected Product createProduct() {
        return new ProductEntity() //
                .id(product("test:prod")) //
                .version("1.0") //
                .name("Test Product").description("A product used for tests") //
                .releaseTimestamp(LocalDateTime.of(2014, 8, 4, 15, 16, 59)) //
                .feature(new ProductEntity().id(feature("javaee-7")).version("1.1")) //
                // .feature(dependency("org.projectlombok:lombok").version("1.12.6")) //
                .feature(new ProductEntity().id(testDependency("ch.qos.logback:logback-classic")).version("1.1.2")) //
                .feature(new ProductEntity().id(testDependency("junit:junit")).version("4.11")) //
                .feature(new ProductEntity().id(testDependency("org.hamcrest:hamcrest-core")).version("1.2.1")) //
                .feature(new ProductEntity().id(testDependency("org.mockito:mockito-all")).version("1.9.5")) //
        ;
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
