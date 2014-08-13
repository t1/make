package com.github.t1.sMake;

import lombok.*;

@Value(staticConstructor = "type")
public class Type {
    public static Id product(String id) {
        return type("product").id(id);
    }

    public static Id feature(String id) {
        return type("feature").id(id);
    }

    public static Id dependency(String id) {
        return type("dependency").id(id);
    }

    @NonNull
    private final String typeName;

    public boolean is(String string) {
        return typeName.equals(string);
    }

    public Id id(String id) {
        return new Id(this, id);
    }

    public Id id(String groupId, String artifactId) {
        return new Id(this, groupId, artifactId);
    }

    @Override
    public String toString() {
        return typeName;
    }
}
