package com.github.t1.sMAKe;

import java.time.LocalDateTime;

import lombok.*;
import lombok.experimental.Accessors;

import com.google.common.collect.ImmutableSet;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class ProductBuilder {
    private final Id id;
    private final String version;

    @Setter
    private LocalDateTime releaseTimestamp;

    private final ImmutableSet.Builder<Product> features = ImmutableSet.builder();

    public ProductBuilder feature(ProductBuilder feature) {
        features.add(feature.build());
        return this;
    }

    public Product build() {
        Repository.get(id, version).ifPresent(p -> merge(p));
        return new Product(id, version, releaseTimestamp, features.build());
    }

    private void merge(Product feature) {
        if (this.releaseTimestamp == null)
            releaseTimestamp(feature.releaseTimestamp());
        for (Product sub : feature.features()) {
            features.add(sub);
        }
    }
}