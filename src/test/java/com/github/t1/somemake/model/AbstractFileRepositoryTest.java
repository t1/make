package com.github.t1.somemake.model;

import static com.github.t1.somemake.model.Repositories.*;
import static com.github.t1.somemake.model.Type.*;

import java.nio.file.*;

import org.junit.*;

public class AbstractFileRepositoryTest extends AbstractTest {
    protected static final Path REPOSITORY_ROOT = Paths.get("target", "test-classes", "repository");

    protected static final Id LOMBOK_ID = dependency("org.projectlombok:lombok");
    protected static final Version LOMBOK_VERSION = LOMBOK_ID.version("1.12.6");
    protected static final String LOMBOK_NAME = "Project Lombok";
    protected static final String LOMBOK_DESCRIPTION = "Simplify your code";

    protected final FileSystemRepository fileRepository = new FileSystemRepository(REPOSITORY_ROOT);

    @Before
    public void registerFileSystemRepository() {
        repositories().register(fileRepository);
    }

    @After
    public void deregisterFileSystemRepository() {
        repositories().deregister(fileRepository);
    }
}
