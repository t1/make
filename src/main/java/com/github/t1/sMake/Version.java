package com.github.t1.sMake;

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
        return versionString;
    }
}
