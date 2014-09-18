package com.github.t1.somemake;

import static com.github.t1.somemake.MergedProduct.*;
import static com.github.t1.somemake.Slum.*;

import java.util.*;

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

    public Repositories deregister(Repository repository) {
        repositories.remove(repository);
        return this;
    }

    public Optional<Product> get(Version version) {
        return repositories.stream().flatMap(r -> stream(r.get(version))).findAny();
    }

    public Product merge(Product product) {
        if (product.id().idString().isEmpty())
            return product;
        return get(product.version()) //
                .map(referenced -> merged(product, referenced)) //
                .orElse(product);
    }
}
