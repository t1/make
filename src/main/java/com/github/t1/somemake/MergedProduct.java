package com.github.t1.somemake;

import static java.util.stream.Collectors.*;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class MergedProduct extends Product {
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
    public Stream<Product> features() {
        Set<Id> ids = master.features().map(p -> p.id()).collect(toSet());
        List<Product> result = master.features().collect(toList());
        servant.features().forEach(p -> {
            if (!ids.contains(p.id()))
                result.add(p);
        });
        return result.stream();
    }

    @Override
    protected Stream<Product> unresolvedFeatures() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String property(Path name) {
        String value = master.property(name);
        if (value == null)
            value = servant.property(name);
        return value;
    }

    @Override
    public Stream<Path> properties() {
        Set<Path> properties = master.properties().collect(toSet());
        properties.addAll(servant.properties().collect(toSet()));
        return properties.stream();
    }
}
