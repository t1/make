package com.github.t1.make;

import java.io.*;

import org.junit.rules.ExternalResource;

public class SystemOutRule extends ExternalResource {
    private static final PrintStream REAL_SYSTEM_OUT = System.out;
    private static final PrintStream REAL_SYSTEM_ERR = System.err;
    private static final ByteArrayOutputStream OUT = new ByteArrayOutputStream();
    private static final ByteArrayOutputStream ERR = new ByteArrayOutputStream();

    @Override
    public void before() {
        System.setOut(new PrintStream(OUT));
        System.setErr(new PrintStream(ERR));
    }

    @Override
    public void after() {
        System.setOut(REAL_SYSTEM_OUT);
        System.setErr(REAL_SYSTEM_ERR);
    }

    public String systemOut() {
        return OUT.toString();
    }

    public String systemErr() {
        return ERR.toString();
    }
}
