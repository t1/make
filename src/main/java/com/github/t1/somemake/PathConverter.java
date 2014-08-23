package com.github.t1.somemake;

import java.nio.file.*;

import org.joda.convert.StringConverter;

public class PathConverter implements StringConverter<Path> {
    @Override
    public String convertToString(Path path) {
        return path.toString();
    }

    @Override
    public Path convertFromString(Class<? extends Path> cls, String str) {
        return Paths.get(str);
    }
}
