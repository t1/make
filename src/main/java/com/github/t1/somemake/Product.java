package com.github.t1.somemake;

import static com.github.t1.somemake.Repositories.*;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;

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


    public ImmutableList<Product> features() {
        ImmutableList.Builder<Product> out = ImmutableList.builder();
        for (Product product : unresolvedFeatures()) {
            resolve(out, product);
        }
        return out.build();
    }

    /**
     * Resolution is a two-step process: First the feature in this product is merged with the feature of the same name
     * in a repository. Second all features in the merged feature and all features referenced from there are "pulled"
     * into this product.
     */
    private void resolve(ImmutableList.Builder<Product> out, Product product) {
        if (product.id().isEmpty())
            return;
        Product merged = merge(product);
        out.add(merged);
        for (Product sub : merged.features()) {
            resolve(out, sub);
        }
    }

    public Product add(@SuppressWarnings("unused") Product feature) {
        throw new UnsupportedOperationException("adding features is not supported by " + getClass().getSimpleName());
    }

    protected abstract ImmutableList<Product> unresolvedFeatures();

    public ImmutableList<Product> features(Predicate<? super Product> predicate) {
        ImmutableList.Builder<Product> out = ImmutableList.builder();
        for (Product feature : features()) {
            if (predicate.test(feature)) {
                out.add(feature);
            }
        }
        return out.build();
    }

    public Product get(Id id) {
        List<Product> matching = features(f -> id.equals(f.id()));
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

    public abstract ImmutableList<Path> properties();

    public abstract boolean hasChildProperties(Path property);
}
