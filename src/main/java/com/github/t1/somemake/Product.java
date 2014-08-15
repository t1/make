package com.github.t1.somemake;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.*;
import java.util.stream.Stream;

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
        return unresolvedFeatures().map(merged());
    }

    protected Function<? super Product, ? extends Product> merged() {
        return f -> Repositories.getInstance().merge(f);
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

    public Product property(@SuppressWarnings("unused") String name, @SuppressWarnings("unused") String value) {
        throw changeUnsupportet("property");
    }

    public abstract String property(String name);
}
