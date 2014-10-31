package com.github.t1.make.model;

import java.util.function.Predicate;

import lombok.Value;

@Value
public class PredicateWithToString<T> implements Predicate<T> {
    private final Predicate<T> predicate;
    private final String toString;

    @Override
    public boolean test(T t) {
        return predicate.test(t);
    }

    @Override
    public String toString() {
        return toString;
    }
}
