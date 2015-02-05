package com.github.t1.make.model;

import lombok.*;

@Value(staticConstructor = "type")
public class Type {
    public static final String ATTRIBUTE = "type";

    public static Id product(String id) {
        return type("product").id(id);
    }

    public static Id feature(String id) {
        return type("feature").id(id);
    }

    public static Id dependency(String id) {
        return type("dependency").id(id);
    }

    public static Id plugin(String id) {
        return type("plugin").id(id);
    }

    public static Id emptyId(String type) {
        return type(type).id(Id.EMPTY);
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

    public String pluralString() {
        if (typeName.endsWith("y"))
            return typeName.substring(0, typeName.length() - 1) + "ies";
        return typeName + "s";
    }
}
