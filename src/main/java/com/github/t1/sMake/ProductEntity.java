package com.github.t1.sMake;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import lombok.*;

@Data
@RequiredArgsConstructor
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
public class ProductEntity implements Product {
    private final Version version;

    private String name;
    private String description;
    private LocalDateTime releaseTimestamp;

    @Getter(NONE)
    private final List<Product> features = new ArrayList<>();

    @Override
    public Stream<Product> features() {
        return features.stream();
    }

    @Override
    public Product feature(Product feature) {
        features.add(feature);
        return this;
    }
}
