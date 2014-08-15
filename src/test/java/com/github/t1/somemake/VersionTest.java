package com.github.t1.somemake;

import static com.github.t1.somemake.Type.*;
import static java.util.Arrays.*;
import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;

public class VersionTest {
    private Optional<String> resolve(String pattern, String... matches) {
        Version version = feature("dummy").version(pattern);
        return version.resolve(asList(matches).stream());
    }

    @Test
    public void shouldResolveSimple() {
        assertTrue(resolve("1.2.3", "1.2.3").isPresent());
    }

    @Test
    public void shouldNotResolveSimpleWrongVersion() {
        assertTrue(!resolve("1.2.3", "1.2.4").isPresent());
    }

    @Test
    public void shouldResolveBefore() {
        assertTrue(resolve("1.2.3", "1.2.3", "1.2.4").isPresent());
    }

    @Test
    public void shouldResolveAfter() {
        assertTrue(resolve("1.2.3", "1.2.2", "1.2.3").isPresent());
    }

    @Test
    public void shouldResolveSingleStar() {
        assertTrue(resolve("*", "1.2.3").isPresent());
    }

    @Test
    public void shouldResolve1Star() {
        assertTrue(resolve("1*", "1.2.3").isPresent());
    }

    @Test
    public void shouldResolve1DotStar() {
        assertTrue(resolve("1.*", "1.2.3").isPresent());
    }

    @Test
    public void shouldNotResolve2Star() {
        assertTrue(!resolve("2*", "1.2.3").isPresent());
    }

    @Test
    public void shouldNotResolve2DotStar() {
        assertTrue(!resolve("2.*", "1.2.3").isPresent());
    }

    @Test
    public void shouldNotResolveStar3() {
        assertTrue(!resolve("*3", "1.2.3").isPresent());
    }
}
