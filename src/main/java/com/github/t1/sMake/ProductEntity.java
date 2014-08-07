package com.github.t1.sMake;

import static java.util.stream.Collectors.*;
import static lombok.AccessLevel.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import lombok.*;

@Data
@ToString(doNotUseGetters = true)
public class ProductEntity implements Product {
    private Id id;
    private String version;

    private String name;
    private String description;
    private LocalDateTime releaseTimestamp;

    @Getter(NONE)
    private final List<Product> features = new ArrayList<>();

    @Override
    public Type type() {
        return id.type();
    }

    @Override
    public Stream<Product> features() {
        return features.stream();
    }

    @Override
    public Product feature(Product product) {
        features.add(product);
        return this;
    }

    @Override
    public Stream<Product> features(Predicate<? super Product> predicate) {
        return features.stream().filter(predicate);
    }

    @Override
    public Product feature(Id id) {
        List<Product> matching = features.stream().filter(f -> id.equals(f.id())).collect(toList());
        return matching.get(0);
    }
}
