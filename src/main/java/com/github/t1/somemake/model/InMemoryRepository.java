package com.github.t1.somemake.model;

import static java.util.stream.Collectors.*;

import java.util.*;

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
    public void addActivation(Version version) {
        Product product = get(version).get();
        activate(product);
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
    public void removeActivation(Version version) {
        Iterator<Map.Entry<Activation, Product>> iter = activations.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Activation, Product> entry = iter.next();
            if (entry.getValue().version().equals(version)) {
                iter.remove();
            }
        }
    }

    @Override
    public void clearAllActivations() {
        activations.clear();
    }
}
