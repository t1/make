package com.github.t1.sMAKe;

import static com.github.t1.sMAKe.Product.*;
import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;

public class ProductBuilderTest {
    @Test
    public void shouldBuildBasicProduct() {
        LocalDateTime now = LocalDateTime.now();

        Product product = product().id("foo").version("1.0").releaseTimestamp(now).build();

        assertEquals("product", product.type());
        assertEquals("foo", product.id());
        assertEquals("1.0", product.version());
        assertEquals(now, product.releaseTimestamp());
    }

    @Test
    public void shouldBuildFeature() {
        Product product = product().feature(feature().id("foo")).build();

        assertEquals("foo", product.feature("foo").id());
    }

    @Test
    public void shouldBuildDoubleNestedProduct() {
        Product product = product().feature(feature().id("foo").feature(feature().id("bar"))).build();

        assertEquals("bar", product.feature("foo").feature("bar").id());
    }

    @Test
    public void shouldFetchFeature() {
        LocalDateTime now = LocalDateTime.now();
        Repository.put(feature().id("foo").version("1.0").releaseTimestamp(now).build());

        Product product = product().feature(feature().id("foo").version("1.0")).build();

        assertEquals(now, product.feature("foo").releaseTimestamp());
    }
}
