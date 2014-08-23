package com.github.t1.somemake;

import static com.github.t1.somemake.HelpCommand.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.*;

public class MainTest {
    @Rule
    public final SystemOutRule out = new SystemOutRule();

    @Test
    public void shouldRunDefaultCommand() {
        new Main().run();

        assertEquals(HELP, out.systemOut().trim());
    }

    @Test
    public void shouldRunHelpCommand() {
        new Main("help").run();

        assertEquals(HELP, out.systemOut().trim());
    }

    @Test
    public void shouldRunBuildCommand() {
        new Main("build").run();

        assertThat(out.systemErr(), startsWith("failed to run [build]: repository path ~/.somemake not found"));
    }
}
