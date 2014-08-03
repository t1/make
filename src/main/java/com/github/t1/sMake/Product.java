package com.github.t1.sMake;

import static java.util.stream.Collectors.*;
import static lombok.AccessLevel.*;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;
import lombok.experimental.Accessors;

import com.google.common.collect.*;

@Value
@Accessors(fluent = true)
public class Product {
    private final Id id;
    private final String version;
    private final LocalDateTime releaseTimestamp;

    @Getter(NONE)
    private ImmutableSet<Product> features;

    public ImmutableCollection<Product> features() {
        return features;
    }

    public Product feature(Id id) {
        List<Product> matching = features.stream().filter(f -> id.equals(f.id())).collect(toList());
        return matching.get(0);
    }
}
