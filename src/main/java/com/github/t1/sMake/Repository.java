package com.github.t1.sMake;

import java.util.Optional;

public interface Repository {
    public void put(Product product);

    public Optional<Product> get(Version version);
}
