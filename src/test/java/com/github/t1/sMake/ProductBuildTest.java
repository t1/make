package com.github.t1.sMake;

import static com.github.t1.sMake.Type.*;
import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;

public class ProductBuildTest extends AbstractTest {
    @Test
    public void shouldBuildBasicProduct() {
        LocalDateTime now = LocalDateTime.now();

        Product product = new ProductEntity().id(product("foo")).version("1.0").releaseTimestamp(now);

        assertEquals("product:foo", product.id().toString());
        assertEquals("1.0", product.version());
        assertEquals(now, product.releaseTimestamp());
    }

    @Test
    public void shouldBuildFeature() {
        Product product = new ProductEntity().id(product("foo")).version("1.0") //
                .feature(new ProductEntity().id(feature("bar")).version("1.1"));

        assertEquals("feature:bar", product.feature(feature("bar")).id().toString());
    }

    @Test
    public void shouldBuildDoubleNestedProduct() {
        Product bar = new ProductEntity().id(feature("bar")).version("1") //
                .feature(new ProductEntity().id(feature("baz")).version("2"));
        Product product = new ProductEntity().id(product("foo")).version("3").feature(bar);

        assertEquals("feature:baz", product.feature(feature("bar")).feature(feature("baz")).id().toString());
    }

    @Test
    public void shouldFetchFeatureTimestampAndFeatures() {
        LocalDateTime now = LocalDateTime.now();
        Product foo1 = new ProductEntity().id(feature("foo")).version("1.0");
        Product bar2 = new ProductEntity().id(feature("bar")).version("2.0");
        repository.put(foo1.releaseTimestamp(now) //
                .feature(bar2.releaseTimestamp(now.plusDays(3))));

        Product product = new ProductEntity().id(product("baz")).version("3.0").feature(foo1);

        assertEquals(now, product.feature(feature("foo")).releaseTimestamp());
        assertEquals(now.plusDays(3), product.feature(feature("foo")).feature(feature("bar")).releaseTimestamp());
    }

    @Test
    public void shouldIgnoreSameTypeAndIdButDifferentVersion() {
        LocalDateTime now = LocalDateTime.now();
        repository.put(new ProductEntity().id(feature("foo")).version("1.0").releaseTimestamp(now));

        Product product = new ProductEntity().id(product("baz")).version("2") //
                .feature(new ProductEntity().id(feature("foo")).version("2.0"));

        assertEquals(null, product.feature(feature("foo")).releaseTimestamp());
    }

    @Test
    public void shouldIgnoreDifferentTypeButSameIdAndVersion() {
        LocalDateTime now = LocalDateTime.now();
        Product foo1 = new ProductEntity().id(feature("foo")).version("1.0");
        repository.put(foo1.releaseTimestamp(now));

        Product product = new ProductEntity().id(product("baz")).version("3.0") //
                .feature(foo1).feature(new ProductEntity().id(type("packaging").id("foo")).version("1.0") //
                        .releaseTimestamp(now.plusDays(5))) //
        ;

        assertEquals(now, product.feature(feature("foo")).releaseTimestamp());
        assertEquals(now.plusDays(5), product.feature(type("packaging").id("foo")).releaseTimestamp());
    }
}
