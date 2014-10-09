package com.github.t1.somemake.model;

import static java.util.stream.Collectors.*;

import java.util.*;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

public class InMemoryRepository implements Repository {
    private final Map<Version, Product> products = new HashMap<>();
    private final Map<Activation, Product> activations = new HashMap<>();

    @Override
    public void put(Product product) {
        products.put(product.version(), product);
    }

    @Override
    public Optional<Product> get(Version version) {
        Product product = products.get(version);
        if (product == null)
            return Optional.empty();
        updateActivationsIn(product);
        return Optional.of(product);
    }

    private void updateActivationsIn(Product product) {
        for (Map.Entry<Activation, Product> entry : activations.entrySet()) {
            if (entry.getKey().active()) {
                product.add(entry.getValue());
            } else {
                product.remove(entry.getValue());
            }
        }
    }

    @Override
    public void activate(Id id) {
        Version version = resolve(id, id.version(Version.ANY));
        Product product = get(version).get();
        activate(product);
    }

    private Version resolve(Id id, Version version) {
        Stream<String> versionStrings = products.keySet().stream().map(v -> v.versionString());
        Optional<String> matchingVersion = version.resolve(versionStrings);
        if (!matchingVersion.isPresent())
            throw new IllegalStateException("no version matches: " + version);
        return id.version(matchingVersion.get());
    }

    private void activate(Product product) {
        Activation activation = Activation.of(product);
        activations.put(activation, product);
    }

    @Override
    public ImmutableList<Version> activations() {
        return ImmutableList.copyOf(activations.values().stream().map(p -> p.version()).collect(toList()));
    }

    @Override
    public void clearActivations() {
        activations.clear();
    }
}
