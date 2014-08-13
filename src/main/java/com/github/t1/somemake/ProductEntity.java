package com.github.t1.somemake;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class ProductEntity extends Product {
    private final Version version;

    private String name;
    private String description;
    private LocalDateTime releaseTimestamp;

    @Getter(NONE)
    private final List<Product> features = new ArrayList<>();

    @Override
    public Stream<Product> features() {
        return features.stream().map(merged());
    }

    @Override
    public Product add(Product feature) {
        features.add(feature);
        return this;
    }
}
