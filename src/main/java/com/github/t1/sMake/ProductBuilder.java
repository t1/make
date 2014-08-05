package com.github.t1.sMake;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import lombok.*;
import lombok.experimental.Accessors;

import com.google.common.collect.ImmutableSet;

@Setter
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class ProductBuilder {
    private final Id id;
    private final String version;

    private String name;
    private String description;
    private LocalDateTime releaseTimestamp;

    @Setter(NONE)
    @Getter(NONE)
    private final ImmutableSet.Builder<Product> features = ImmutableSet.builder();

    public ProductBuilder feature(ProductBuilder feature) {
        features.add(feature.build());
        return this;
    }

    public Product build() {
        Repository.INSTANCE.get().get(id, version).ifPresent(p -> merge(p));
        return new Product(id, version, name, description, releaseTimestamp, features.build());
    }

    private void merge(Product feature) {
        if (this.releaseTimestamp == null)
            releaseTimestamp(feature.releaseTimestamp());
        feature.features().forEach(f -> features.add(f));
    }
}
