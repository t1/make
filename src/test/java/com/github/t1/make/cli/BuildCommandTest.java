package com.github.t1.make.cli;

import static com.github.t1.make.model.AbstractTest.*;
import static org.junit.Assert.*;

import java.nio.file.*;

import org.junit.Test;

public class BuildCommandTest {
    private static final Path REPOSITORY = Paths.get("target/test-classes/repository");
    private static final Path ORIGINAL_POM = Paths.get("pom.xml");
    private static final Path TEST_POM = Paths.get("target/test-pom.xml");
    private static final Path ECHO = Paths.get("/bin/echo");

    @Test
    public void shouldRun() {
        BuildCommand buildCommand = new BuildCommand().repository(REPOSITORY).maven(ECHO).pom(TEST_POM);

        buildCommand.run();

        assertEquals(readFile(ORIGINAL_POM), readFile(buildCommand.pom()));
    }
}
