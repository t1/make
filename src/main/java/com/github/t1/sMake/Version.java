package com.github.t1.sMake;

import java.nio.file.Path;

import lombok.Value;

@Value
public class Version {
    Id id;
    String versionString;

    public Path path() {
        return id.path().resolve(versionString);
    }

    public Type type() {
        return id.type();
    }

    @Override
    public String toString() {
        return id + ":" + versionString;
    }
}
