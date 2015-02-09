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
        new Main("build", "--repository=does-not-exist").run();

        assertThat(out.systemErr(), containsString("failed to run [build --repository=does-not-exist]: "
                + "no features found [matching type [plugin]] in packaging:jar"));
    }
}
