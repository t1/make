package com.github.t1.somemake;

import java.nio.file.Path;

import lombok.Value;

@Value
public class Property {
    Path path;
    String value;

    public String name() {
        return path.getFileName().toString();
    }
}
