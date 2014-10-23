package com.github.t1.somemake.model;

import static com.github.t1.somemake.model.Repositories.*;
import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.*;

import com.google.common.collect.ImmutableList;

public abstract class Product {
    public static Predicate<? super Product> matching(Type type) {
        return f -> type.equals(f.type());
    }

    public static Predicate<? super Product> matching(Id id) {
        return f -> id.equals(f.id());
    }

    public static Predicate<? super Product> matching(Version version) {
        return f -> version.equals(f.version());
    }

    private static final Id NAME = Type.property("name");
    private static final Id DESCRIPTION = Type.property("description");
    private static final Id RELEASETIMESTAMP = Type.property("releaseTimestamp");

    public Type type() {
        return id().type();
    }

    public boolean hasId() {
        return !id().isEmpty();
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

    public Product description(String description) {
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
        return unsupported("changing the " + what);
    }

    private UnsupportedOperationException unsupported(String what) {
        return new UnsupportedOperationException(what //
                + " is not supported by " + getClass().getSimpleName() + ": " + version());
    }


    public ImmutableList<Product> features() {
        // TODO cache features, probably
        List<Product> out = new ArrayList<>();
        for (Product feature : unresolvedFeatures()) {
            resolve(out, feature);
        }
        return ImmutableList.copyOf(out);
    }

    private void resolve(List<Product> out, Product feature) {
        if (feature.hasId()) {
            Product merged = repositories().merge(feature);
            merge(out, merged);
        } else {
            out.add(feature);
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
        return feature(matching(id));
    }

    public Product feature(Predicate<? super Product> predicate) {
        Optional<Product> result = optionalFeature(predicate);
        if (!result.isPresent())
            throw new IllegalArgumentException("no features found [" + predicate + "] in " + this.version());
        return result.get();
    }

    public Optional<Product> optionalFeature(Type type) {
        return optionalFeature(matching(type));
    }

    public Optional<Product> optionalFeature(Id id) {
        return optionalFeature(matching(id));
    }

    public Optional<Product> optionalFeature(Predicate<? super Product> predicate) {
        List<Product> matching = features(predicate);
        if (matching.size() > 1)
            throw new IllegalArgumentException("multiple features [" + predicate + "] in " + this.version() + ":\n"
                    + info(matching));
        return (matching.isEmpty()) ? Optional.empty() : Optional.of(matching.get(0));
    }

    public List<Product> features(Predicate<? super Product> predicate) {
        return features().stream().filter(predicate).collect(toList());
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

    public boolean hasFeatures() {
        return !features().isEmpty();
    }

    public boolean hasFeature(Id id) {
        return features().stream().anyMatch(matching(id));
    }

    public Product set(Id id, @SuppressWarnings("unused") String value) {
        throw unsupported("setting feature '" + id + "'");
    }

    public Product add(@SuppressWarnings("unused") Product feature) {
        throw unsupported("adding features");
    }

    public Product remove(@SuppressWarnings("unused") Product feature) {
        throw unsupported("removing features");
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

    public abstract Optional<String> attribute(String name);
}
