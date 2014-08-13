package com.github.t1.somemake;

import java.nio.file.*;
import java.util.*;

public class InMemoryRepository implements Repository {
    private final Map<Path, Product> map = new HashMap<>();

    @Override
    public void put(Product product) {
        map.put(path(product.version()), product);
    }

    @Override
    public Optional<Product> get(Version version) {
        Path path = path(version);
        return Optional.ofNullable(map.get(path));
    }

    private Path path(Version version) {
        return Paths.get(version.type().typeName(), version.id().idString(), version.versionString());
    }
}
