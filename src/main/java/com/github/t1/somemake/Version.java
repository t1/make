package com.github.t1.somemake;

import java.util.*;
import java.util.stream.Stream;

import lombok.*;

@Value
public class Version {
    public static final Comparator<String> VERSION = new Comparator<String>() {
        @Override
        public int compare(String thisVersion, String thatVersion) {
            String[] thisParts = split(thisVersion);
            String[] thatParts = split(thatVersion);
            int i;
            for (i = 0; i < thisParts.length; i++) {
                String thisPart = thisParts[i];
                if (thatParts.length < i + 1)
                    return 1;
                String thatPart = thatParts[i];

                if (thisPart.equals(thatPart))
                    continue;

                if (isNumeric(thisPart)) {
                    if (isNumeric(thatPart)) {
                        Integer thisInt = Integer.valueOf(thisPart);
                        Integer thatInt = Integer.valueOf(thatPart);
                        int c = thisInt.compareTo(thatInt);
                        if (c == 0)
                            continue;
                        return c;
                    }
                    return 1; // numbers are always bigger than strings
                }
                if (isNumeric(thatPart))
                    return -1; // strings are always smaller than numbers
                return thisPart.compareToIgnoreCase(thatPart);
            }
            if (thatParts.length > i)
                return -1;
            return 0;
        }

        private String[] split(String thisVersion) {
            return thisVersion.split("(\\.|-)");
        }

        private boolean isNumeric(String string) {
            return string.matches("\\d+");
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
        return versions.filter(v -> matches(v)) //
                // .sorted().peek(v -> System.out.println(v)) //
                .max(VERSION);
    }

    public boolean matches(Version version) {
        return matches(version.versionString());
    }

    public boolean matches(String version) {
        if (versionString.endsWith("*"))
            return wildcardMatches(version);
        return versionString.equals(version);
    }

    private boolean wildcardMatches(String pattern) {
        int i = versionString.indexOf('*');
        return pattern.substring(0, i).equals(versionString.substring(0, i));
    }
}
