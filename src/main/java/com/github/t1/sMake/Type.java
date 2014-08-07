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

    public static Id testDependency(String id) {
        return type("testDependency").id(id);
    }

    @NonNull
    private final String typeName;

    public Id id(String id) {
        return new Id(this, id);
    }

    @Override
    public String toString() {
        return typeName;
    }

    public boolean is(String string) {
        return typeName.equals(string);
    }
}
