package com.github.t1.somemake.cli;

import static com.github.t1.somemake.model.AbstractTest.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.*;

import org.junit.*;

import com.github.t1.somemake.model.*;

public class BuildCommandTest {
    private static final Path REPOSITORY = Paths.get("target", "test-classes", "repository");
    private static final Path POM = Paths.get("pom.xml");

    @Before
    public void createActivationsXml() throws IOException {
        Files.write(activationsPath(), FileActivationsTest.ONE_ACTIVATION.getBytes());
    }

    @After
    public void cleanActivationsXml() throws IOException {
        Files.deleteIfExists(activationsPath());
    }

    private Path activationsPath() {
        return new FileSystemRepository(REPOSITORY).activationsPath();
    }

    @Test
    public void shouldRun() {
        BuildCommand buildCommand = new BuildCommand().repository(REPOSITORY);

        buildCommand.run();

        assertEquals(readFile(POM), readFile(buildCommand.output()));
    }
}
