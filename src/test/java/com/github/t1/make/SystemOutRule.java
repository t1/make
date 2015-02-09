package com.github.t1.make;

import java.io.*;

import org.junit.rules.ExternalResource;

public class SystemOutRule extends ExternalResource {
    private static final PrintStream REAL_SYSTEM_OUT = System.out;
    private static final PrintStream REAL_SYSTEM_ERR = System.err;
    private static final OutputStream OUT = new ByteArrayOutputStream();
    private static final OutputStream ERR = new ByteArrayOutputStream();

    private static final boolean teeToSystemOut = System.getProperty("infinitest") == null;

    @Override
    public void before() {
        System.setOut(printStream(OUT));
        System.setErr(printStream(ERR));
    }

    private PrintStream printStream(OutputStream stream) {
        return new PrintStream(new FilterOutputStream(stream) {
            @Override
            public void write(int b) throws IOException {
                if (teeToSystemOut)
                    REAL_SYSTEM_OUT.write(b);
                super.write(b);
            }
        });
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
