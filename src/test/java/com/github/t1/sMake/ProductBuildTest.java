package com.github.t1.sMake;

import static com.github.t1.sMake.Type.*;
import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;

public class ProductBuildTest extends AbstractTest {
    private final LocalDateTime now = LocalDateTime.now();

    @Test
    public void shouldBuildBasicProduct() {
        Product product = newProduct(product("foo"), "1.0").releaseTimestamp(now);

        assertEquals("product:foo", product.id().toString());
        assertEquals("1.0", product.version());
        assertEquals(now, product.releaseTimestamp());
    }

    @Test
    public void shouldBuildFeature() {
        Product product = newProduct(product("foo"), "1.0") //
                .feature(newProduct(feature("bar"), "1.1"));

        assertEquals("feature:bar", product.feature(feature("bar")).id().toString());
    }

    @Test
    public void shouldBuildDoubleNestedProduct() {
        Product bar = newProduct(feature("bar"), "1.0") //
                .feature(newProduct(feature("baz"), "2.0"));
        Product product = newProduct(product("foo"), "3.0").feature(bar);

        assertEquals("feature:baz", product.feature(feature("bar")).feature(feature("baz")).id().toString());
    }

    @Test
    public void shouldFetchFeatureTimestampAndFeatures() {
        Product foo1 = newProduct(feature("foo"), "1.0");
        Product bar2 = newProduct(feature("bar"), "2.0");
        repository.put(foo1.releaseTimestamp(now) //
                .feature(bar2.releaseTimestamp(now.plusDays(3))));

        Product product = newProduct(product("baz"), "3.0").feature(foo1);

        assertEquals(now, product.feature(feature("foo")).releaseTimestamp());
        assertEquals(now.plusDays(3), product.feature(feature("foo")).feature(feature("bar")).releaseTimestamp());
    }

    @Test
    public void shouldIgnoreSameTypeAndIdButDifferentVersion() {
        repository.put(newProduct(feature("foo"), "1.0").releaseTimestamp(now));

        Product product = newProduct(product("baz"), "2.0") //
                .feature(newProduct(feature("foo"), "2.0"));

        assertEquals(null, product.feature(feature("foo")).releaseTimestamp());
    }

    @Test
    public void shouldIgnoreDifferentTypeButSameIdAndVersion() {
        Product foo1 = newProduct(feature("foo"), "1.0");
        repository.put(foo1.releaseTimestamp(now));

        Product product = newProduct(product("baz"), "3.0") //
                .feature(foo1).feature(newProduct(type("packaging").id("foo"), "1.0") //
                        .releaseTimestamp(now.plusDays(5))) //
        ;

        assertEquals(now, product.feature(feature("foo")).releaseTimestamp());
        assertEquals(now.plusDays(5), product.feature(type("packaging").id("foo")).releaseTimestamp());
    }
}
