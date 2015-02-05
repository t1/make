package com.github.t1.make.cli;

import static com.github.t1.make.cli.HelpCommand.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.*;

import com.github.t1.make.SystemOutRule;

public class MainTest {
    @Rule
    public final SystemOutRule out = new SystemOutRule();

    @Test
    public void shouldRunDefaultCommand() {
        new Main().run();

        assertThat(out.systemOut().trim(), containsString(HELP));
    }

    @Test
    public void shouldRunHelpCommand() {
        new Main("help").run();

        assertThat(out.systemOut().trim(), containsString(HELP));
    }

    @Test
    public void shouldFailToRunBuildCommandWithInvalidRepositoryPath() {
        new Main("build", "--repository=dummy").run();

        assertThat(out.systemErr(),
                containsString("failed to run [build --repository=dummy]: repository path [dummy] not found"));
    }
}