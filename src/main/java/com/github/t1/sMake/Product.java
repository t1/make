package com.github.t1.sMake;

import static java.util.stream.Collectors.*;
import static lombok.AccessLevel.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import lombok.*;

import com.google.common.collect.ImmutableSet;

@Value
@AllArgsConstructor
@ToString(doNotUseGetters = true)
public class Product {
    private final Id id;
    private final String version;

    private String name;
    private String description;
    private LocalDateTime releaseTimestamp;

    @Getter(NONE)
    private ImmutableSet<Product> features;

    /** required for JAXB */
    @SuppressWarnings("unused")
    private Product() {
        this.id = null;
        this.version = null;
        this.name = null;
        this.description = null;
        this.releaseTimestamp = null;
        this.features = null;
    }

    public Type type() {
        return id.type();
    }

    public Stream<Product> features() {
        return features.stream();
    }

    public Stream<Product> features(Predicate<? super Product> predicate) {
        return features.stream().filter(predicate);
    }

    public Product feature(Id id) {
        List<Product> matching = features.stream().filter(f -> id.equals(f.id())).collect(toList());
        return matching.get(0);
    }
}
