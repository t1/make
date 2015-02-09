package com.github.t1.make.model;

import static com.github.t1.make.model.MergedProduct.*;

import java.util.*;
import java.util.stream.Stream;

public class Repositories {
    private static final Repositories INSTANCE = new Repositories();

    public static Repositories repositories() {
        return INSTANCE;
    }

    private final List<Repository> repositories = new ArrayList<>();

    public Repositories register(Repository repository) {
        repositories.add(repository);
        return this;
    }

    public Stream<Repository> stream() {
        return repositories.stream();
    }

    public Repositories deregister(Repository repository) {
        repositories.remove(repository);
        return this;
    }

    public Optional<Product> get(Version version) {
        for (Repository repository : repositories) {
            Optional<Product> product = repository.resolve(version);
            if (product.isPresent()) {
                return product;
            }
        }
        return Optional.empty();
    }

    public Product merge(Product product) {
        return get(product.version()) //
                .map(referenced -> merged(product, referenced)) //
                .orElse(product);
    }
}
