package com.github.t1.make;

import java.io.*;

import org.junit.rules.ExternalResource;

public class SystemOutRule extends ExternalResource {
    private final PrintStream realSystemOut = System.out;
    private final PrintStream realSystemErr = System.err;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    @Override
    public void before() {
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));
    }

    @Override
    public void after() {
        System.setOut(realSystemOut);
        System.setErr(realSystemErr);
    }

    public String systemOut() {
        return out.toString();
    }

    public String systemErr() {
        return err.toString();
    }
}
