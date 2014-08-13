package com.github.t1.sMake;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class MergedProduct extends Product {
    private final Product master;
    private final Product servant;

    public MergedProduct(Product master, Product servant) {
        this.master = master;
        this.servant = servant;

        if (!master.id().equals(servant.id()))
            throw new IllegalArgumentException("ids of products to be merged don't match\n" //
                    + "master: " + master.id() //
                    + "servant: " + servant.id());
        if (master.versionString() != null && !master.version().equals(servant.version()))
            throw new IllegalArgumentException("versions of products to be merged don't match\n" //
                    + "master: " + master.versionString() //
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
}
