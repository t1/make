package com.github.t1.somemake;

import java.util.stream.Stream;

import lombok.Value;

@Value
public class Version {
    Id id;
    String versionString;

    public Type type() {
        return id.type();
    }

    @Override
    public String toString() {
        return id + ":" + versionString;
    }

    public String resolve(Stream<String> versions) {
        if (!hasWildcards())
            return versionString;
        return versionString;
    }

    private boolean hasWildcards() {
        return versionString.contains("+") || versionString.contains("*");
    }
}
