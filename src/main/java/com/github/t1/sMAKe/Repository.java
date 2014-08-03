package com.github.t1.sMAKe;

import java.nio.file.*;
import java.util.*;

public class Repository {
    private static Map<Path, Product> map = new HashMap<>();

    public static void put(Product product) {
        map.put(path(product), product);
    }

    private static Path path(Product product) {
        return Paths.get(product.id().type().toString(), product.id().toString(), product.version());
    }

    public static Optional<Product> get(Id id, String version) {
        Path path = Paths.get(id.type().toString(), id.toString());
        if (version != null)
            path = path.resolve(version);
        return Optional.ofNullable(map.get(path));
    }
}
