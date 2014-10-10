package com.github.t1.somemake.model;

import java.util.Optional;

import com.google.common.collect.ImmutableList;

public interface Repository {
    public void put(Product product);

    public Optional<Product> get(Version version);

    public void addActivation(Version version);

    public void removeActivation(Version version);

    public void clearAllActivations();

    public ImmutableList<Version> activations();
}
