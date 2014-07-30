package com.github.t1.sMAKe;

import static com.github.t1.sMAKe.Builder.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class BuilderTest extends AbstractTest {
    @Test
    public void shouldParse() {
        Builder builder = Builder.of(PRODUCT1_XML);

        Product product = builder.getProduct();

        assertEquals(createProduct(), product);
    }

    @Test
    public void shouldProducePom() {
        Builder builder = Builder.of(PRODUCT1_XML);

        String pom = builder.toPom();

        assertEquals(readFile(PRODUCT1_POM), pom);
    }
}
