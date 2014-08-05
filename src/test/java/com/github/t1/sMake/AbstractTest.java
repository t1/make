package com.github.t1.sMake;

import static com.fasterxml.jackson.databind.DeserializationFeature.*;
import static com.fasterxml.jackson.databind.SerializationFeature.*;
import static com.github.t1.sMake.Type.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

import org.junit.Before;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AbstractTest {
    protected final Repository repository = new ImMemoryRepository();

    protected final ObjectMapper json = new ObjectMapper().enable(INDENT_OUTPUT).disable(FAIL_ON_UNKNOWN_PROPERTIES);

    @Before
    public void before() {
        Repository.INSTANCE.set(repository);
    }

    protected Product createProduct() {
        return product("test:prod").version("1.0") //
                .name("Test Product").description("A product used for tests") //
                .releaseTimestamp(LocalDateTime.of(2014, 8, 4, 15, 16, 59)) //
                .feature(feature("javaee-7").version("1.1")) //
                // .feature(dependency("org.projectlombok:lombok").version("1.12.6")) //
                .feature(testDependency("ch.qos.logback:logback-classic").version("1.1.2")) //
                .feature(testDependency("junit:junit").version("4.11")) //
                .feature(testDependency("org.hamcrest:hamcrest-core").version("1.2.1")) //
                .feature(testDependency("org.mockito:mockito-all").version("1.9.5")) //
                .build();
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
