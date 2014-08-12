package com.github.t1.sMake;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jboss.weld.exceptions.UnsupportedOperationException;

public interface Product {
    default public Type type() {
        return id().type();
    }

    default public Id id() {
        return version().id();
    }

    public Version version();

    default public String versionString() {
        return version().versionString();
    }

    default public Product versionString(@SuppressWarnings("unused") String version) {
        throw changeUnsupportet("version");
    }

    public default UnsupportedOperationException changeUnsupportet(String what) {
        return new UnsupportedOperationException("changing the " + what + " is not supported by "
                + getClass().getSimpleName());
    }


    public String name();

    default public Product name(@SuppressWarnings("unused") String name) {
        throw changeUnsupportet("name");
    }


    public String description();

    default Product description(@SuppressWarnings("unused") String description) {
        throw changeUnsupportet("description");
    }


    public LocalDateTime releaseTimestamp();

    default public Product releaseTimestamp(@SuppressWarnings("unused") LocalDateTime releaseTimestamp) {
        throw changeUnsupportet("releaseTimestamp");
    }


    public Stream<Product> features();

    default public Product add(@SuppressWarnings("unused") Product feature) {
        throw new UnsupportedOperationException("adding features is not supported by " + getClass().getSimpleName());
    }


    default public Stream<Product> features(Predicate<? super Product> predicate) {
        return features().filter(predicate);
    }

    default public Product get(Id id) {
        List<Product> matching = features(f -> id.equals(f.id())).collect(toList());
        if (matching.size() == 0)
            throw new IllegalArgumentException("not found: " + id + " in " + this.version());
        if (matching.size() > 1)
            throw new IllegalArgumentException("multiple features with id " + id + " in " + this.version() + ":\n"
                    + matching);
        return Repositories.getInstance().merge(matching.get(0));
    }
}
