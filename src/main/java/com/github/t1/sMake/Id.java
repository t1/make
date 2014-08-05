package com.github.t1.sMake;

import lombok.*;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
public class Id {
    @NonNull
    Type type;
    @NonNull
    String idString;

    public ProductBuilder version(String version) {
        return new ProductBuilder(this, version);
    }

    public String groupId() {
        return idSplit()[0];
    }

    public String artifactId() {
        return idSplit()[1];
    }

    private String[] idSplit() {
        return idString.split(":", 2);
    }

    @Override
    public String toString() {
        return type + ":" + idString;
    }
}
