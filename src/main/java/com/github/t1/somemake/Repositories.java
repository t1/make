package com.github.t1.somemake;

import static com.github.t1.somemake.Slum.*;

import java.util.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Repositories {
    private static final Repositories INSTANCE = new Repositories();

    public static Repositories getInstance() {
        return INSTANCE;
    }


    private final List<Repository> repositories = new ArrayList<>();

    public Repositories register(Repository repository) {
        repositories.add(repository);
        return this;
    }

    public Repositories deregister(Repository repository) {
        repositories.remove(repository);
        return this;
    }

    public Optional<Product> get(Version version) {
        return repositories.stream().flatMap(r -> stream(r.get(version))).findAny();
    }

    public Product merge(Product product) {
        Optional<Product> referenced = get(product.version());
        if (referenced.isPresent()) {
            log.debug("merge {}", product.version());
            product = new MergedProduct(product, referenced.get());
        } else {
            log.debug("{} not found in repositories", product.version());
        }
        return product;
    }
}
