package com.github.t1.sMAKe;

import static com.fasterxml.jackson.databind.DeserializationFeature.*;
import static com.fasterxml.jackson.databind.SerializationFeature.*;
import static com.github.t1.sMAKe.Builder.*;
import static org.junit.Assert.*;

import java.io.*;

import javax.xml.bind.JAXB;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializationTest extends AbstractTest {
    private final ObjectMapper json = new ObjectMapper().enable(INDENT_OUTPUT).disable(FAIL_ON_UNKNOWN_PROPERTIES);

    @Test
    public void shouldMarshalAsXml() {
        Product product = createProduct();

        StringWriter out = new StringWriter();
        JAXB.marshal(product, out);

        assertEquals(readFile(PRODUCT1_XML).replace("\n\n", "\n").replace(" />", "/>"), out.toString());
    }

    @Test
    public void shouldUnmarshalFromXml() throws IOException {
        try (FileReader reader = new FileReader(PRODUCT1_XML)) {
            Product product = JAXB.unmarshal(reader, Product.class);

            assertEquals(createProduct(), product);
        }
    }

    @Test
    public void shouldMarshalAsJsonWithoutExceptions() throws IOException {
        Product product = createProduct();

        // just check for exceptions... rely on Jackson to be able to parse it's own output
        json.writeValue(System.out, product);
    }
}
