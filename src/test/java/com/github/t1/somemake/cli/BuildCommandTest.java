package com.github.t1.somemake.cli;

import static com.github.t1.somemake.model.AbstractTest.*;
import static org.junit.Assert.*;

import java.nio.file.*;

import org.junit.*;

public class BuildCommandTest {
    private static final Path REPOSITORY = Paths.get("src", "test", "resources", "repository");
    private static final Path POM = Paths.get("pom.xml");

    @Test
    // FIXME
    @Ignore
    public void shouldRun() {
        BuildCommand buildCommand = new BuildCommand().repository(REPOSITORY);

        buildCommand.run();

        assertEquals(readFile(POM), readFile(buildCommand.output()));
    }
}
