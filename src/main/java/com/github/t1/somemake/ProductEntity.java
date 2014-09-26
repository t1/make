package com.github.t1.somemake;

import static com.github.t1.somemake.Version.*;

import java.util.*;

import lombok.*;

import com.google.common.collect.ImmutableList;

@RequiredArgsConstructor
public class ProductEntity extends Product {
    @Getter
    private final Version version;

    @Setter
    private String value;

    @Override
    public Optional<String> value() {
        return Optional.ofNullable(value);
    }

    private final List<Product> features = new ArrayList<>();

    @Override
    public ImmutableList<Product> unresolvedFeatures() {
        return ImmutableList.copyOf(features);
    }

    @Override
    public Product add(Product feature) {
        features.add(feature);
        return this;
    }

    @Override
    public Product set(Id id, String value) {
        ProductEntity feature = new ProductEntity(id.version(ANY));
        feature.value(value);
        return add(feature);
    }
}
