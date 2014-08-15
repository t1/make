package com.github.t1.somemake;

import java.util.*;
import java.util.stream.Stream;

import lombok.*;

@Value
public class Version {
    private static final Comparator<String> AS_VERSION = new Comparator<String>() {
        @Override
        public int compare(String version1, String version2) {
            return 0;
        }
    };

    @NonNull
    Id id;
    @NonNull
    String versionString;

    public Type type() {
        return id.type();
    }

    @Override
    public String toString() {
        return id + ":" + versionString;
    }

    public Optional<String> resolve(Stream<String> versions) {
        return versions.filter(v -> matches(v)).sorted(AS_VERSION).findFirst();
    }

    public boolean matches(Version version) {
        return matches(version.versionString());
    }

    public boolean matches(String version) {
        System.out.println("match " + version + " against " + versionString);
        if (versionString.endsWith("*"))
            return starMatches(version);
        return versionString.equals(version);
    }

    private boolean starMatches(String pattern) {
        int i = versionString.indexOf('*');
        return pattern.substring(0, i).equals(versionString.substring(0, i));
    }
}
