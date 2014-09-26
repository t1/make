package com.github.t1.somemake;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.*;

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
    public Optional<String> name() {
        if (master.name().isPresent())
            return master.name();
        return servant.name();
    }

    @Override
    public Optional<String> description() {
        if (master.description().isPresent())
            return master.description();
        return servant.description();
    }

    @Override
    public Optional<LocalDateTime> releaseTimestamp() {
        if (master.releaseTimestamp().isPresent())
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
    public Optional<String> value() {
        if (master.value().isPresent())
            return master.value();
        return servant.value();
    }
}
