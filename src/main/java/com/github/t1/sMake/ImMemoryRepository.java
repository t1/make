package com.github.t1.sMake;

import java.nio.file.*;
import java.util.*;

public class ImMemoryRepository implements Repository {
    private final Map<Path, Product> map = new HashMap<>();

    @Override
    public void put(Product product) {
        map.put(path(product.id(), product.version()), product);
    }

    @Override
    public Optional<Product> get(Id id, String version) {
        Path path = path(id, version);
        return Optional.ofNullable(map.get(path));
    }

    private Path path(Id id, String version) {
        Path path = Paths.get(id.type().toString(), id.toString());
        if (version != null)
            path = path.resolve(version);
        return path;
    }
}
