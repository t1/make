package com.github.t1.sMAKe;

import java.util.*;

public class Repository {
    private static Map<Key, Product> map = new HashMap<>();

    public static void put(Product product) {
        map.put(product.key(), product);
    }

    public static Optional<Product> get(Key key) {
        return Optional.ofNullable(map.get(key));
    }
}
