package com.github.t1.make.model;

import static java.util.Arrays.*;

import java.time.LocalDateTime;
import java.util.*;

import lombok.EqualsAndHashCode;

import com.google.common.collect.ImmutableList;

@EqualsAndHashCode(callSuper = false)
public class MergedProduct extends Product {
    public static Product merged(Product product, List<Product> other) {
        if (other.isEmpty())
            return product;
        List<Product> products = new ArrayList<>();
        products.add(product);
        products.addAll(other);
        return new MergedProduct(products);
    }

    public static Product merged(Product first, Product second, Product... other) {
        List<Product> products = new ArrayList<>();
        products.add(first);
        products.add(second);
        products.addAll(asList(other));
        return new MergedProduct(products);
    }

    private final List<Product> products;

    public MergedProduct(List<Product> products) {
        assert !products.isEmpty();
        this.products = products;
    }

    private Product firstProduct() {
        return this.products.get(0);
    }

    @Override
    public Type type() {
        return firstProduct().type();
    }

    @Override
    public Id id() {
        return firstProduct().id();
    }

    @Override
    public Version version() {
        for (Product product : products)
            if (!product.version().isWildcard())
                return product.version();
        return firstProduct().version();
    }

    @Override
    public Optional<String> name() {
        for (Product product : products)
            if (product.name().isPresent())
                return product.name();
        return Optional.empty();
    }

    @Override
    public Optional<String> description() {
        for (Product product : products)
            if (product.description().isPresent())
                return product.description();
        return Optional.empty();
    }

    @Override
    public Optional<String> value() {
        for (Product product : products)
            if (product.value().isPresent())
                return product.value();
        return Optional.empty();
    }

    @Override
    protected ImmutableList<Product> unresolvedFeatures() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<LocalDateTime> releaseTimestamp() {
        for (Product product : products)
            if (product.releaseTimestamp().isPresent())
                return product.releaseTimestamp();
        return Optional.empty();
    }

    @Override
    public Optional<String> attribute(String name) {
        for (Product product : products)
            if (product.hasAttribute(name))
                return product.attribute(name);
        return Optional.empty();
    }

    @Override
    public Product attribute(String key, String value) {
        for (Product product : products) {
            if (product.attribute(key).isPresent()) {
                product.attribute(key, value);
                return this;
            }
        }
        firstProduct().attribute(key, value);
        return this;
    }

    @Override
    public ImmutableList<Product> features() {
        List<Id> ids = new ArrayList<>();
        ImmutableList.Builder<Product> result = ImmutableList.builder();
        for (Product product : products) {
            for (Product feature : product.features()) {
                if (!ids.contains(feature.id())) {
                    ids.add(feature.id());
                    result.add(feature);
                }
            }
        }
        return result.build();
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        int i = 0;
        for (Product product : products) {
            out.append("merged ").append(i++).append(": ");
            out.append(product.getClass().getSimpleName()).append(": ");
            out.append(product).append("\n");
        }
        return out.toString();
    }
}
