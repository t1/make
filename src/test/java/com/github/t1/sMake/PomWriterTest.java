package com.github.t1.sMake;

import static com.github.t1.sMake.Type.*;
import static org.junit.Assert.*;

import java.io.StringWriter;
import java.nio.file.*;

import org.junit.Test;

public class PomWriterTest extends AbstractTest {
    private static final Path POM1_XML = Paths.get("src/test/resources/product1.pom.xml");

    private final StringWriter target = new StringWriter();

    @SuppressWarnings("unused")
    @Test(expected = NullPointerException.class)
    public void shouldFailToBuildNullProduct() {
        new PomWriter(null, target);
    }

    @SuppressWarnings("unused")
    @Test(expected = NullPointerException.class)
    public void shouldFailToBuildNullTarget() {
        new PomWriter(createProduct(), null);
    }

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToBuildFeature() {
        new PomWriter(feature("test-feature").version("1.0").build(), target);
    }

    @Test
    public void shouldBuildBasicProduct() {
        PomWriter writer = new PomWriter(createProduct(), target);

        writer.write();

        assertEquals(normalize(readFile(POM1_XML)), target.toString());
    }
}
