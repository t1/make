package com.github.t1.sMake;

import java.util.*;

public class Repositories {
    private static final Repositories INSTANCE = new Repositories();

    public static Repositories getInstance() {
        return INSTANCE;
    }


    private final List<Repository> repositories = new ArrayList<>();

    public Repositories register(Repository repository) {
        repositories.add(repository);
        return this;
    }

    public Repositories deregister(Repository repository) {
        repositories.remove(repository);
        return this;
    }

    public Optional<Product> get(Version version) {
        for (Repository repository : repositories) {
            Optional<Product> result = repository.get(version);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }
}
