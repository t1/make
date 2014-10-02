package com.github.t1.somemake.model;

import static com.github.t1.somemake.model.MergedProduct.*;

import java.util.*;

public class Repositories {
    private static final Repositories INSTANCE = new Repositories();

    public static Product merge(Product product) {
        return repositories().doMerge(product);
    }

    public static Repositories repositories() {
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
        for (Repository repository : repositories) {
            Optional<Product> product = repository.get(version);
            if (product.isPresent()) {
                return product;
            }
        }
        return Optional.empty();
    }

    private Product doMerge(Product product) {
        return get(product.version()) //
                .map(referenced -> merged(product, referenced)) //
                .orElse(product);
    }
}
