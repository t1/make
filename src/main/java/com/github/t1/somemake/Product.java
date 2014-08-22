package com.github.t1.somemake;

import static com.github.t1.somemake.Repositories.*;
import static java.util.stream.Collectors.*;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.*;
import java.util.stream.*;
import java.util.stream.Stream.Builder;

public abstract class Product {
    public Type type() {
        return id().type();
    }

    public Id id() {
        return version().id();
    }

    public abstract Version version();

    public String versionString() {
        return version().versionString();
    }

    public Product versionString(@SuppressWarnings("unused") String version) {
        throw changeUnsupportet("version");
    }

    public UnsupportedOperationException changeUnsupportet(String what) {
        return new UnsupportedOperationException("changing the " + what + " is not supported by "
                + getClass().getSimpleName());
    }


    public abstract String name();

    public Product name(@SuppressWarnings("unused") String name) {
        throw changeUnsupportet("name");
    }


    public abstract String description();

    Product description(@SuppressWarnings("unused") String description) {
        throw changeUnsupportet("description");
    }


    public abstract LocalDateTime releaseTimestamp();

    public Product releaseTimestamp(@SuppressWarnings("unused") LocalDateTime releaseTimestamp) {
        throw changeUnsupportet("releaseTimestamp");
    }


    public Stream<Product> features() {
        Builder<Product> out = Stream.builder();
        resolve(out, unresolvedFeatures());
        return out.build().map(merged());
    }

    /**
     * Resolution is a two-step process: First the feature in this product is merged with the feature of the same name
     * in a repository. Second all features in the merged feature and all features referenced from there are "pulled"
     * into this product.
     */
    private void resolve(Builder<Product> out, Stream<Product> products) {
        products.map(merged()).forEach(p -> {
            out.add(p);
            resolve(out, p.features().map(merged()));
        });
    }

    private Function<Product, Product> merged() {
        return f -> repositories().merge(f);
    }

    public Product add(@SuppressWarnings("unused") Product feature) {
        throw new UnsupportedOperationException("adding features is not supported by " + getClass().getSimpleName());
    }

    protected abstract Stream<Product> unresolvedFeatures();

    public Stream<Product> features(Predicate<? super Product> predicate) {
        return features().filter(predicate);
    }

    public Product get(Id id) {
        List<Product> matching = features(f -> id.equals(f.id())).collect(toList());
        if (matching.size() == 0)
            throw new IllegalArgumentException("not found: " + id + " in " + this.version());
        if (matching.size() > 1)
            throw new IllegalArgumentException("multiple features with id " + id + " in " + this.version() + ":\n"
                    + matching);
        return matching.get(0);
    }

    @Override
    public String toString() {
        return version().toString();
    }

    public Product property(@SuppressWarnings("unused") Path path, @SuppressWarnings("unused") String value) {
        throw changeUnsupportet("property");
    }

    public abstract String property(Path path);

    public abstract Stream<Path> properties();

    public abstract boolean hasChildProperties(Path property);
}
