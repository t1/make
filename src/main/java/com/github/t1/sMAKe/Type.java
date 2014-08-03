package com.github.t1.sMAKe;

import lombok.Value;
import lombok.experimental.Accessors;

@Value(staticConstructor = "type")
@Accessors(fluent = true)
public class Type {
    public static Id product(String id) {
        return type("product").id(id);
    }

    public static Id feature(String id) {
        return type("feature").id(id);
    }

    public static Id type(String type, String id) {
        return type(type).id(id);
    }

    private final String typeName;

    public Id id(String id) {
        return new Id(this, id);
    }

    @Override
    public String toString() {
        return typeName;
    }
}
