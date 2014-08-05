package com.github.t1.sMake;

import static org.junit.Assert.*;

import java.io.*;
import java.nio.file.*;

import javax.xml.bind.JAXB;

import org.junit.*;

@Ignore
public class MarshalingTest extends AbstractTest {
    private static final Path PRODUCT1_XML = Paths.get("src/test/resources/product1.xml");

    @Test
    public void shouldMarshalProductAsXml() {
        StringWriter xml = new StringWriter();

        JAXB.marshal(createProduct(), xml);

        assertEquals(normalize(readFile(PRODUCT1_XML)), normalize(xml.toString()));
    }

    @Test
    public void shouldMarshalProductAsJson() throws IOException {
        StringWriter xml = new StringWriter();

        json.writeValue(xml, createProduct());

        assertEquals(readFile(PRODUCT1_XML), normalize(xml.toString()));
    }
}
