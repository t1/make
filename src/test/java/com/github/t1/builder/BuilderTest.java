package com.github.t1.builder;

import static com.github.t1.builder.Builder.*;
import static org.junit.Assert.*;

import java.io.StringWriter;

import javax.xml.bind.JAXB;

import org.junit.Test;

public class BuilderTest {
    private static final String BUILD1_XML = "src/test/resources/product1.xml";
    private static final String BUILD1_POM = "src/test/resources/product1.pom.xml";

    private final Builder builder = Builder.of(BUILD1_XML);

    private Product createProduct() {
        return Product.builder() //
                .id("foo:bar") //
                .version("1.0") //
                .name("Product Name") //
                .description("The Product") //
                .inceptionYear("2014") //
                .feature(Feature.builder().id("javaee-7").build()) //
                .feature(Feature.builder().id("org.projectlombok:lombok").version("1.12.6").build()) //
                .feature(Feature.builder().id("com.github.t1:webresource-generator").build()) //
                .feature(Feature.builder().id("org.slf4j:slf4j-api").version("1.7+").build()) //
                .build();
    }

    @Test
    public void shouldParse() {
        Product product = builder.getProduct();

        assertEquals(createProduct(), product);
    }

    @Test
    public void shouldMarshal() {
        Product product = createProduct();

        StringWriter out = new StringWriter();
        JAXB.marshal(product, out);

        assertEquals(readFile(BUILD1_XML), out.toString());
    }

    @Test
    public void shouldProducePom() {
        String pom = builder.toPom();

        assertEquals(readFile(BUILD1_POM), pom);
    }
}
