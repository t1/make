package com.github.t1.make.model;

import java.util.*;

import com.google.common.collect.ImmutableList;

public class InMemoryRepository extends Repository {
    private final Map<Version, Product> products = new HashMap<>();
    private final Map<Activation, Product> activations = new HashMap<>();

    @Override
    public void put(Product product) {
        products.put(product.version(), product);
    }

    @Override
    public Optional<Product> resolve(Version version) {
        Product product = products.get(version);
        return Optional.ofNullable(product);
    }


    @Override
    public ImmutableList<Activation> activations() {
        return ImmutableList.copyOf(activations.keySet());
    }

    @Override
    public Version version(Activation activation) {
        return activations.get(activation).version();
    }

    @Override
    public void addActivation(Version version) {
        Product product = resolve(version).get();
        activate(product);
    }

    private void activate(Product product) {
        Activation activation = Activation.of(product);
        activations.put(activation, product);
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
