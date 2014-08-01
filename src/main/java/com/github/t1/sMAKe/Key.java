package com.github.t1.sMAKe;

import lombok.Value;
import lombok.experimental.*;

import com.github.t1.sMAKe.Product.ProductBuilder;

@Value
@Accessors(fluent = true)
@Builder(builderMethodName = "key", chain = true)
@Wither
public class Key {
    String type, id, version;

    public static KeyBuilder type(String type) {
        return key().type(type);
    }

    public ProductBuilder builder() {
        return new ProductBuilder().key(this);
    }
}
