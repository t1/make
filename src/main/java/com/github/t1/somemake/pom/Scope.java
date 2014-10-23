package com.github.t1.somemake.pom;

public enum Scope {
    provided,
    compile,
    runtime,
    test,
    system;

    public static final Scope DEFAULT = compile;
}