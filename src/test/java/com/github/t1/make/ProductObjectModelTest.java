package com.github.t1.make;

import static com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature.*;
import static com.github.t1.make.model2.Package.*;
import static com.github.t1.make.model2.Product.*;
import static java.nio.charset.StandardCharsets.*;
import static java.nio.file.Files.*;
import static org.assertj.core.api.Assertions.*;

import java.io.*;
import java.nio.file.*;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.t1.make.model2.*;

public class ProductObjectModelTest {
    public static final ProductObjectModel POM = ProductObjectModel.pom()
            .product(product()
                    .id(new ArtifactId("com.github.t1", "make"))
                    .version("0.0.1-SNAPSHOT")
                    .name("make")
                    .description("simplifies the description of project build processes"))
            .pack(pack()
                    .as("executable jar")
                    .containing("org.projectlombok", "lombok", "1.16.8")
                    .containing("ch.qos.logback", "logback-classic", "1.1*")
                    .containing("com.google.guava", "guava", "*")
                    .containing("org.glassfish", "javax.json", "1.0.4")
                    .containing("org.joda", "joda-convert", "1.8.1"))
            .build();

    private static final Path PATH = Paths.get("pom.yaml");
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory()
            .enable(MINIMIZE_QUOTES).disable(WRITE_DOC_START_MARKER));

    @Test
    public void shouldParseSamplePom() throws Exception {
        ProductObjectModel pom = MAPPER.readValue(newBufferedReader(PATH, UTF_8), ProductObjectModel.class);

        assertThat(pom).isEqualTo(POM);
    }

    @Test
    public void shouldSerializeSamplePom() throws Exception {
        Writer writer = new StringWriter();

        MAPPER.writeValue(writer, POM);

        System.out.println(writer);
        //assertThat(writer.toString()).isEqualTo(contentOf(PATH.toFile(), UTF_8));
    }

    // TODO type safe Version
}
