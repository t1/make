package com.github.t1.sMake;

import static com.github.t1.sMake.Type.*;
import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;

public class ProductBuilderTest {
    @Test
    public void shouldBuildBasicProduct() {
        LocalDateTime now = LocalDateTime.now();

        Product product = product("foo").version("1.0").releaseTimestamp(now).build();

        assertEquals("product:foo", product.id().toString());
        assertEquals("1.0", product.version());
        assertEquals(now, product.releaseTimestamp());
    }

    @Test
    public void shouldBuildFeature() {
        Product product = product("foo").version("1.0").feature(feature("bar").version("1.1")).build();

        assertEquals("feature:bar", product.feature(feature("bar")).id().toString());
    }

    @Test
    public void shouldBuildDoubleNestedProduct() {
        ProductBuilder bar = feature("bar").version("1").feature(feature("baz").version("2"));
        Product product = product("foo").version("3").feature(bar).build();

        assertEquals("feature:baz", product.feature(feature("bar")).feature(feature("baz")).id().toString());
    }

    @Test
    public void shouldFetchFeatureTimestampAndFeatures() {
        LocalDateTime now = LocalDateTime.now();
        ProductBuilder foo1 = feature("foo").version("1.0");
        ProductBuilder bar2 = feature("bar").version("2.0");
        Repository.put(foo1.releaseTimestamp(now) //
                .feature(bar2.releaseTimestamp(now.plusDays(3))) //
                .build());

        Product product = product("baz").version("3.0") //
                .feature(foo1) //
                .build();

        assertEquals(now, product.feature(feature("foo")).releaseTimestamp());
        assertEquals(now.plusDays(3), product.feature(feature("foo")).feature(feature("bar")).releaseTimestamp());
    }

    @Test
    public void shouldIgnoreSameTypeAndIdButDifferentVersion() {
        LocalDateTime now = LocalDateTime.now();
        Repository.put(feature("foo").version("1.0").releaseTimestamp(now).build());

        Product product = product("baz").version("2").feature(feature("foo").version("2.0")).build();

        assertEquals(null, product.feature(feature("foo")).releaseTimestamp());
    }

    @Test
    public void shouldIgnoreDifferentTypeSameIdAndVersion() {
        LocalDateTime now = LocalDateTime.now();
        ProductBuilder foo1 = feature("foo").version("1.0");
        Repository.put(foo1.releaseTimestamp(now).build());

        Product product = product("baz").version("3.0") //
                .feature(foo1) //
                .feature(type("packaging").id("foo").version("1.0").releaseTimestamp(now.plusDays(5))) //
                .build();

        assertEquals(now, product.feature(feature("foo")).releaseTimestamp());
        assertEquals(now.plusDays(5), product.feature(type("packaging").id("foo")).releaseTimestamp());
    }
}
