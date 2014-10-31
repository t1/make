package com.github.t1.somemake.model;

import static com.github.t1.somemake.model.Version.*;

import java.util.*;

import lombok.*;

import com.google.common.collect.ImmutableList;

@RequiredArgsConstructor
public class ProductEntity extends Product {
    @Getter
    private final Version version;

    private final Map<String, String> attributes = new LinkedHashMap<>();

    @Setter
    private String value;

    @Override
    public Type type() {
        return version.type();
    }

    @Override
    public Id id() {
        return version.id();
    }

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
    public Product addFeature(Id id, String value) {
        ProductEntity feature = new ProductEntity(id.version(ANY));
        feature.value(value);
        add(feature);
        return this;
    }

    @Override
    public Product add(Product feature) {
        features.add(feature);
        return this;
    }

    @Override
    public Product remove(Product feature) {
        features.remove(feature);
        return this;
    }

    @Override
    public Optional<String> attribute(String name) {
        return Optional.ofNullable(attributes.get(name));
    }

    @Override
    public Product attribute(String key, String value) {
        if (value == null)
            attributes.remove(key);
        else
            attributes.put(key, value);
        return this;
    }
}
