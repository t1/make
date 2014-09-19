package com.github.t1.somemake;

import java.nio.file.*;

import lombok.*;

@Value
@AllArgsConstructor
public class Id {
    private static final String DELIMITER = ":";

    @NonNull
    Type type;
    @NonNull
    String idString;

    public Id(Type type, String groupId, String artifactId) {
        this(type, groupId + DELIMITER + artifactId);
    }

    public String groupId() {
        return idSplit()[0];
    }

    public String artifactId() {
        String[] split = idSplit();
        return split.length > 1 ? split[1] : "";
    }

    private String[] idSplit() {
        return idString.split(DELIMITER, 2);
    }

    public Version version(String version) {
        return new Version(this, version);
    }

    @Override
    public String toString() {
        return type + DELIMITER + idString;
    }

    public Path path() {
        return Paths.get(type.typeName(), idPath());
    }

    private String[] idPath() {
        return idString.split("(\\.|" + DELIMITER + ")");
    }

    public boolean isEmpty() {
        return idString.isEmpty();
    }
}
