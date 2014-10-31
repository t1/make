package com.github.t1.make.pom;

public enum Scope {
    provided,
    compile,
    runtime,
    test,
    system;

    public static final Scope DEFAULT = compile;
}