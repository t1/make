package com.github.t1.sMake;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Product {
    public Id id();

    public Product id(Id id);

    default public Type type() {
        return id().type();
    }


    public String version();

    public Product version(String version);


    public String name();

    public Product name(String name);


    public String description();

    public Product description(String description);


    public LocalDateTime releaseTimestamp();

    public Product releaseTimestamp(LocalDateTime releaseTimestamp);


    public Stream<Product> features();

    public Product feature(Product product);


    default public Stream<Product> features(Predicate<? super Product> predicate) {
        return features().filter(predicate);
    }

    default public Product feature(Id id) {
        List<Product> matching = features(f -> id.equals(f.id())).collect(toList());
        if (matching.size() == 0)
            throw new IllegalArgumentException("no feature with id " + id);
        if (matching.size() > 1)
            throw new IllegalArgumentException("multiple features with id " + id + ":\n" + matching);
        return matching.get(0);
    }
}
