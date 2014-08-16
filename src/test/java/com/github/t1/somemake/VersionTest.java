package com.github.t1.somemake;

import static com.github.t1.somemake.Type.*;
import static org.junit.Assert.*;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;

public class VersionTest {
    private Optional<String> resolve(String pattern, String... matches) {
        Version version = feature("dummy").version(pattern);
        return version.resolve(Stream.of(matches));
    }

    @Test
    public void shouldResolveSimple() {
        assertEquals("1.2.3", resolve("1.2.3", "1.2.3").get());
    }

    @Test
    public void shouldNotResolveSimpleWrongVersion() {
        assertFalse(resolve("1.2.3", "1.2.4").isPresent());
    }

    @Test
    public void shouldResolveBefore() {
        assertEquals("1.2.3", resolve("1.2.3", "1.2.3", "1.2.4").get());
    }

    @Test
    public void shouldResolveAfter() {
        assertEquals("1.2.3", resolve("1.2.3", "1.2.2", "1.2.3").get());
    }

    @Test
    public void shouldResolveSingleStar() {
        assertEquals("1.2.3", resolve("*", "1.2.3").get());
    }

    @Test
    public void shouldResolve1Star() {
        assertEquals("1.2.3", resolve("1*", "1.2.3").get());
    }

    @Test
    public void shouldResolve1DotStar() {
        assertEquals("1.2.3", resolve("1.*", "1.2.3").get());
    }

    @Test
    public void shouldNotResolve2Star() {
        assertFalse(resolve("2*", "1.2.3").isPresent());
    }

    @Test
    public void shouldNotResolve2DotStar() {
        assertFalse(resolve("2.*", "1.2.3").isPresent());
    }

    @Test
    public void shouldNotResolveStar3() {
        assertFalse(resolve("*3", "1.2.3").isPresent());
    }

    @Test
    public void shouldResolveHighest1DotStar() {
        assertEquals("1.10.3", resolve("1.*", "1.3.1", "1.2.4", "1.3", "1.2.3", "1.10.3", "10.2.3", "2.6.7").get());
    }

    @Test
    public void shouldResolveHighest1DotStarWithSnapshot() {
        assertEquals("1.2.3", resolve("1.*", "1.2.3", "1.2-SNAPSHOT").get());
    }
}
