package com.github.t1.somemake;

import static lombok.AccessLevel.*;

import java.util.Optional;
import java.util.stream.Stream;

import lombok.NoArgsConstructor;

/** Utility methods that need a better place to live. */
@NoArgsConstructor(access = PRIVATE)
public class Slum {
    /**
     * Turns an Optional<T> into a Stream<T> of length zero or one depending upon whether a value is present.
     * 
     * @see <a
     *      href="http://stackoverflow.com/questions/22725537/using-java-8s-optional-with-streamflatmap">stackoverflow</a>
     */
    public static <T> Stream<T> stream(Optional<T> optional) {
        return optional.map(Stream::of).orElseGet(Stream::empty);
    }
}
