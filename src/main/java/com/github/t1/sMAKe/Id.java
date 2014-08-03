package com.github.t1.sMAKe;

import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
public class Id {
    Type type;
    String idString;

    public ProductBuilder version(String version) {
        return new ProductBuilder(this, version);
    }

    @Override
    public String toString() {
        return type + ":" + idString;
    }
}
