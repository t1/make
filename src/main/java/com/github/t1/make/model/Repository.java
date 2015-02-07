package com.github.t1.make.model;

import java.util.*;

import org.slf4j.*;

import com.google.common.collect.ImmutableList;

public abstract class Repository {
    public static final Logger LOG = LoggerFactory.getLogger(Repository.class);

    public abstract void put(Product product);

    public final Product get(Version version) {
        return withActivations(resolve(version).get());
    }

    public abstract Optional<Product> resolve(Version version);

    public abstract void addActivation(Version version);

    public abstract void removeActivation(Version version);

    public abstract void clearAllActivations();

    public abstract ImmutableList<Activation> activations();

    public final Product withActivations(Product product) {
        List<Product> productActivations = new ArrayList<>();
        for (Activation activation : activations()) {
            Version version = version(activation);
            if (version.equals(product.version()))
                continue;
            if (activation.active()) {
                LOG.debug("activate {}", version);
                Product activatedProduct = resolve(version).get();
                productActivations.add(activatedProduct);
            }
        }
        return MergedProduct.merged(product, productActivations);
    }

    public abstract Version version(Activation activation);
}
