package com.github.t1.sMake;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public interface Repository {
    public static AtomicReference<Repository> INSTANCE = new AtomicReference<>();

    public void put(Product product);

    public Optional<Product> get(Id id, String version);
}
