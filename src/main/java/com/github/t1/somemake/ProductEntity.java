package com.github.t1.somemake;

import static lombok.AccessLevel.*;

import java.nio.file.Path;
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

    private Map<Path, String> properties = new HashMap<>();

    @Override
    public Stream<Product> unresolvedFeatures() {
        return features.stream();
    }

    @Override
    public Product add(Product feature) {
        features.add(feature);
        return this;
    }

    @Override
    public Product property(Path path, String value) {
        properties.put(path, value);
        return this;
    }

    @Override
    public String property(Path path) {
        return properties.get(path);
    }

    @Override
    public Stream<Path> properties() {
        return properties.keySet().stream();
    }

    @Override
    public boolean hasChildProperties(Path property) {
        return properties.keySet().stream().filter(p -> p.getParent().equals(property)).findAny().isPresent();
    }
}
