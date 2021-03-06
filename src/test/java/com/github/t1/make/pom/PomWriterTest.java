package com.github.t1.make.pom;

import static com.github.t1.make.model.Repositories.*;
import static com.github.t1.make.model.Type.*;
import static org.junit.Assert.*;

import java.nio.file.*;

import org.junit.*;

import com.github.t1.make.model.*;

public class PomWriterTest extends AbstractTest {
    private static final Path REPOSITORY_ROOT = Paths.get("target", "test-classes", "repository");
    private final FileSystemRepository repository = new FileSystemRepository(REPOSITORY_ROOT);

    @Before
    public void registerFileSystemRepository() {
        repositories().register(repository);
    }

    @After
    public void deregisterFileSystemRepository() {
        repositories().deregister(repository);
    }

    private void shouldConvertTestProductWithVersion(String versionString) {
        Version version = product("product:test-product").version(versionString);

        String pom = convert(version);

        assertPomFile(version, pom);
    }

    private String convert(Version version) {
        Product product = repositories().get(version).get();

        PomWriter writer = new PomWriter(product);

        return writer.writeToString();
    }

    private void assertPomFile(Version version, String pom) {
        assertEquals(readFile(REPOSITORY_ROOT.resolve(version.path()).resolve("pom.xml")), pom);
    }

    @Test(expected = NullPointerException.class)
    public void shouldFailToBuildNullProduct() {
        new PomWriter(null).writeToString();
    }

    @Test
    public void shouldBuildSimpleProductEntity() {
        Version version = product("product:test-product").version("0.9");
        Product product = new ProductEntity(version) //
                .name("Test Product") //
                .description("A product used for tests") //
        ;
        PomWriter writer = new PomWriter(product);

        String pom = writer.writeToString();

        assertPomFile(version, pom);
    }

    @Test
    public void shouldLoadSimpleProduct() {
        shouldConvertTestProductWithVersion("0.9");
    }

    @Test
    public void shouldLoadProductWithDependencyWithOverwrittenScope() {
        shouldConvertTestProductWithVersion("1.0");
    }

    @Test
    public void shouldLoadProductWithDependencyWithOverwrittenScopeAndOneExclusion() {
        shouldConvertTestProductWithVersion("1.0.1");
    }

    @Test
    public void shouldLoadProductWithDependencyWithOverwrittenScopeAndTwoExclusions() {
        shouldConvertTestProductWithVersion("1.0.2");
    }

    @Test
    public void shouldLoadProductWithTwoDependencies() {
        shouldConvertTestProductWithVersion("1.0.3");
    }

    // @Test
    // public void shouldInheritNamePropertyFromNestedFeature() {
    // shouldConvertTestProductWithVersion("1.1");
    // }
    //
    // @Test
    // public void shouldInheritNamePropertyFromDoublyNestedFeature() {
    // shouldConvertTestProductWithVersion("1.1.1");
    // }

    @Test
    public void shouldOverwriteNamePropertyOverNestedFeature() {
        shouldConvertTestProductWithVersion("1.2");
    }

    @Test
    public void shouldLoadSimplePlugin() {
        shouldConvertTestProductWithVersion("2.0");
    }

    @Test
    public void shouldLoadPluginWithConfiguration() {
        shouldConvertTestProductWithVersion("2.1");
    }

    @Test
    public void shouldLoadPluginWithTwoConfigurations() {
        shouldConvertTestProductWithVersion("2.1.1");
    }

    // dependencies 2.2
    // executions 2.3
    // extensions 2.4
    // goals 2.5

    @Test
    public void shouldLoadPluginWithInherited() {
        shouldConvertTestProductWithVersion("2.6");
    }

    @Test
    public void shouldLoadPluginWithDefaultInherited() {
        shouldConvertTestProductWithVersion("2.6.1");
    }

    @Test
    // FIXME
    @Ignore
    public void shouldWriteParametersCompilerArgumentOnJdk7() {
        assertFalse(buildParametersCompilerArgumentOn("1.7"));
    }

    @Test
    // FIXME
    @Ignore
    public void shouldWriteParametersCompilerArgumentOnJdk71() {
        assertFalse(buildParametersCompilerArgumentOn("1.7.1"));
    }

    @Test
    @Ignore
    public void shouldWriteParametersCompilerArgumentOnJdk8() {
        assertTrue(buildParametersCompilerArgumentOn("1.8"));
    }

    @Test
    @Ignore
    public void shouldWriteParametersCompilerArgumentOnJdk9() {
        assertTrue(buildParametersCompilerArgumentOn("1.9"));
    }

    @Test
    @Ignore
    public void shouldWriteParametersCompilerArgumentOnJdk10() {
        assertTrue(buildParametersCompilerArgumentOn("1.10"));
    }

    private boolean buildParametersCompilerArgumentOn(String jdk) {
        String oldVersion = System.getProperty("java.specification.version");
        try {
            System.setProperty("java.specification.version", jdk);

            Product product = repositories().get(product("product:test-product").version("1.1")).get();
            PomWriter writer = new PomWriter(product);

            String pom = writer.writeToString();

            return pom.contains("<compilerArgument>-parameters</compilerArgument>");
        } finally {
            System.setProperty("java.specification.version", oldVersion);
        }
    }
}
