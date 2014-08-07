package com.github.t1.sMake;

import static com.github.t1.sMake.Type.*;
import static org.junit.Assert.*;

import java.io.StringReader;
import java.time.LocalDateTime;

import org.junit.Test;

public class ProductJsonReaderTest extends AbstractTest {
    @Test
    public void shouldReadSimpleJson() {
        String json = "{'type':'product-type', 'id':'foo:bar', 'version':'1.0', " //
                + "'name':'product-name', 'description':'product-description', " //
                + "'releaseTimestamp':'2014-08-15T16:50:15' " //
                + "}";
        ProductJsonReader reader = new ProductJsonReader(new StringReader(json.replace('\'', '\"')));

        Product product = reader.read();

        assertEquals(type("product-type").id("foo:bar").version("1.0") //
                .name("product-name").description("product-description") //
                .releaseTimestamp(LocalDateTime.of(2014, 8, 15, 16, 50, 15)) //
                .build().toString(), product.toString());
    }
}
