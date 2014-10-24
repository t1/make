package com.github.t1.somemake.cli;

import static com.github.t1.somemake.model.AbstractTest.*;
import static org.junit.Assert.*;

import java.nio.file.*;

import org.junit.Test;

public class BuildCommandTest {
    private static final Path REPOSITORY = Paths.get("target/test-classes/repository");
    private static final Path ECHO = Paths.get("/bin/echo");

    @Test
    public void shouldRun() {
        String beforePom = readFile(BuildCommand.POM);
        BuildCommand buildCommand = new BuildCommand().repository(REPOSITORY).maven(ECHO);

        buildCommand.run();

        assertEquals(beforePom, readFile(BuildCommand.POM));
    }
}
