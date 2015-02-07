package com.github.t1.make.model;

import static java.util.Arrays.*;

import java.time.LocalDateTime;
import java.util.*;

import lombok.EqualsAndHashCode;

import com.google.common.collect.ImmutableList;

@EqualsAndHashCode(callSuper = false)
public class MergedProduct extends Product {
    public static Product merged(Product first, Product second, Product... other) {
        return new MergedProduct(first, second, other);
    }

    private final List<Product> products = new ArrayList<>();

    public MergedProduct(Product first, Product second, Product... other) {
        this.products.add(first);
        this.products.add(second);
        this.products.addAll(asList(other));

        checkVersions();
    }

    private void checkVersions() {
        Version referenceVersion = firstProduct().version();
        for (Product referencedProduct : this.products.subList(1, this.products.size())) {
            check(referenceVersion, referencedProduct.version());
        }
    }

    private Product firstProduct() {
        return this.products.get(0);
    }

    private void check(Version master, Version servant) {
        if (!master.type().equals(servant.type()))
            throw new IllegalArgumentException("types of products to be merged don't match\n" //
                    + "master: " + master.type() + "\n" //
                    + "servant: " + servant.type());
        if (!master.id().equals(servant.id()))
            throw new IllegalArgumentException("ids of products to be merged don't match\n" //
                    + "master: " + master.id() + "\n" //
                    + "servant: " + servant.id());
        if (!master.matches(servant))
            throw new IllegalArgumentException("versions of products to be merged don't match\n" //
                    + "master: " + master.versionString() + "\n" //
                    + "servant: " + servant.versionString());
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
