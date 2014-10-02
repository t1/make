package com.github.t1.somemake.model;

import static com.github.t1.somemake.model.Id.*;
import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

public abstract class Product {
    private static final Id NAME = Type.type("name").id(EMPTY);
    private static final Id DESCRIPTION = Type.type("description").id(EMPTY);
    private static final Id RELEASETIMESTAMP = Type.type("releaseTimestamp").id(EMPTY);

    public Type type() {
        return id().type();
    }

    public Id id() {
        return version().id();
    }

    public Product id(Id id) {
        return version(type().id(id.idString()).version(versionString()));
    }

    public abstract Version version();

    public String versionString() {
        return version().versionString();
    }

    public Product version(@SuppressWarnings("unused") Version version) {
        throw changeUnsupportet("version");
    }

    public Product versionString(String version) {
        return version(id().version(version));
    }


    public Optional<String> name() {
        if (!hasFeature(NAME))
            return Optional.empty();
        return feature(NAME).value();
    }

    public Product name(String name) {
        return set(NAME, name);
    }


    public Optional<String> description() {
        if (!hasFeature(DESCRIPTION))
            return Optional.empty();
        return feature(DESCRIPTION).value();
    }

    Product description(String description) {
        return set(DESCRIPTION, description);
    }


    public Optional<LocalDateTime> releaseTimestamp() {
        if (!hasFeature(RELEASETIMESTAMP))
            return Optional.empty();
        Product feature = feature(RELEASETIMESTAMP);
        Optional<String> stringValue = feature.value();
        return stringValue.map(releaseTimestamp -> LocalDateTime.parse(releaseTimestamp));
    }

    public Product releaseTimestamp(LocalDateTime releaseTimestamp) {
        return set(RELEASETIMESTAMP, releaseTimestamp.toString());
    }


    public UnsupportedOperationException changeUnsupportet(String what) {
        return new UnsupportedOperationException("changing the " + what + " is not supported by "
                + getClass().getSimpleName() + ": " + version());
    }


    public ImmutableList<Product> features() {
        // TODO cache features, probably
        List<Product> out = new ArrayList<>();
        for (Product feature : unresolvedFeatures()) {
            resolve(out, feature);
        }
        return ImmutableList.copyOf(out);
    }

    /**
     * Resolution is a two-step process: First a feature in this product is merged with the feature of the same id and
     * matching version in any repository. Second all features in the merged feature and all features referenced from
     * there are "pulled" into this product.
     */
    private void resolve(List<Product> out, Product feature) {
        if (feature.id().isEmpty()) {
            if (!out.stream().anyMatch(p -> p.id().equals(feature.id())))
                out.add(feature);
            resolveSub(out, feature);
        } else {
            Product merged = Repositories.merge(feature);
            merge(out, merged);
            resolveSub(out, merged);
        }
    }

    private void resolveSub(List<Product> out, Product feature) {
        for (Product sub : feature.features()) {
            resolve(out, sub);
        }
    }

    protected abstract ImmutableList<Product> unresolvedFeatures();

    private void merge(List<Product> out, Product feature) {
        for (Product product : out) {
            if (product.id().equals(feature.id())) {
                // merge
                return;
            }
        }
        out.add(feature);
    }

    public Product feature(Id id) {
        List<Product> matching = features(id);
        if (matching.size() < 1)
            throw new IllegalArgumentException("no features found with id '" + id + "' in " + this.version());
        if (matching.size() > 1)
            throw new IllegalArgumentException("multiple features with id '" + id + "' in " + this.version() + ":\n"
                    + info(matching));
        return matching.get(0);
    }

    public List<Product> features(Id id) {
        return features().stream().filter(f -> id.equals(f.id())).collect(toList());
    }

    private String info(List<Product> matching) {
        Function<Product, String> mapper;
        if (matching.stream().allMatch(p -> p.version().isAny())) {
            mapper = p -> p.value().orElse("?");
        } else {
            mapper = p -> p.id().toString();
        }
        return matching.stream().map(mapper).collect(toList()).toString();
    }

    public boolean hasFeature(Id id) {
        return features().stream().anyMatch(feature -> id.equals(feature.id()));
    }

    public Product set(Id id, @SuppressWarnings("unused") String value) {
        throw new UnsupportedOperationException("setting feature '" + id + "' is not supported by "
                + getClass().getSimpleName());
    }

    public Product add(@SuppressWarnings("unused") Product feature) {
        throw new UnsupportedOperationException("adding features is not supported by " + getClass().getSimpleName());
    }

    public abstract Optional<String> value();

    @Override
    public String toString() {
        return toString("");
    }

    private String toString(String prefix) {
        StringBuilder out = new StringBuilder();
        out.append(prefix).append(version().toString()).append("\n");
        for (Product feature : unresolvedFeatures()) {
            out.append(feature.toString(prefix + "\t"));
        }
        return out.toString();
    }
}
