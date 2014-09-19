package com.github.t1.somemake;

import static java.util.stream.Collectors.*;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Set;

import com.google.common.collect.ImmutableList;

public class MergedProduct extends Product {
    public static Product merged(Product product, Product referenced) {
        return new MergedProduct(product, referenced);
    }

    private final Product master;
    private final Product servant;

    public MergedProduct(Product master, Product servant) {
        check(master, servant);

        this.master = master;
        this.servant = servant;
    }

    private void check(Product master, Product servant) {
        if (!master.id().equals(servant.id()))
            throw new IllegalArgumentException("ids of products to be merged don't match\n" //
                    + "master: " + master.id() + "\n" //
                    + "servant: " + servant.id());
        if (master.versionString() != null && !master.version().matches(servant.version()))
            throw new IllegalArgumentException("versions of products to be merged don't match\n" //
                    + "master: " + master.versionString() + "\n" //
                    + "servant: " + servant.versionString());
    }

    @Override
    public Version version() {
        return servant.version();
    }

    @Override
    public String name() {
        if (master.name() != null)
            return master.name();
        return servant.name();
    }

    @Override
    public String description() {
        if (master.description() != null)
            return master.description();
        return servant.description();
    }

    @Override
    public LocalDateTime releaseTimestamp() {
        if (master.releaseTimestamp() != null)
            return master.releaseTimestamp();
        return servant.releaseTimestamp();
    }

    @Override
    public ImmutableList<Product> features() {
        Set<Id> ids = master.features().stream().map(p -> p.id()).collect(toSet());
        ImmutableList.Builder<Product> result = ImmutableList.builder();
        result.addAll(master.features());

        for (Product p : servant.features()) {
            if (!ids.contains(p.id())) {
                result.add(p);
            }
        }
        return result.build();
    }

    @Override
    protected ImmutableList<Product> unresolvedFeatures() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String property(Path path) {
        String value = master.property(path);
        if (value == null)
            value = servant.property(path);
        return value;
    }

    @Override
    public ImmutableList<Path> properties() {
        ImmutableList.Builder<Path> result = ImmutableList.builder();
        result.addAll(master.properties());
        result.addAll(servant.properties());
        return result.build();
    }

    @Override
    public boolean hasChildProperties(Path property) {
        return master.hasChildProperties(property) || servant.hasChildProperties(property);
    }
}
