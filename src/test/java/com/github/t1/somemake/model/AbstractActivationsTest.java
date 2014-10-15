package com.github.t1.somemake.model;

import static com.github.t1.somemake.model.Type.*;
import static java.util.stream.Collectors.*;
import static org.junit.Assert.*;

import org.junit.*;

import com.google.common.collect.ImmutableList;

public abstract class AbstractActivationsTest extends AbstractFileRepositoryTest {
    protected static final Id JAVAC = plugin("compiler.java");
    protected static final Version JAVAC_3_1 = JAVAC.version("3.1");
    protected static final Version JAVAC_0_0 = JAVAC.version("0.0");
    protected static final Version SCALA_0_1 = plugin("compiler.scala").version("0.1");

    protected static final Version TEST_PRODUCT_VERSION = product("product:test-product").version("1.0");

    @Before
    @After
    public void cleanActivations() {
        repository().clearAllActivations();
    }

    protected abstract void givenActivation(Version version);

    protected abstract void givenProduct(Version version);

    protected abstract Repository repository();

    @Test
    public void shouldAddActivation() {
        givenActivation(JAVAC_3_1);

        repository().addActivation(JAVAC_3_1);

        assertTrue(activatedVersions().contains(JAVAC_3_1));
    }

    public ImmutableList<Version> activatedVersions() {
        return ImmutableList.copyOf(repository().activations().stream() //
                .map(a -> repository().version(a)).collect(toList()));
    }

    @Test
    public void shouldRemoveActivation() {
        givenActivation(JAVAC_3_1);

        repository().removeActivation(JAVAC_3_1);

        assertFalse("activation not removed", activatedVersions().contains(JAVAC_3_1));
    }

    @Test
    public void shouldClearAllActivation() {
        givenActivation(JAVAC_3_1);

        repository().clearAllActivations();

        assertTrue(activatedVersions().isEmpty());
    }

    @Test
    public void shouldActivateJavac() {
        givenActivation(JAVAC_3_1);
        givenProduct(TEST_PRODUCT_VERSION);

        Product product = repository().get(TEST_PRODUCT_VERSION);

        assertTrue("did not activate javac", product.hasFeature(JAVAC));
        assertEquals(JAVAC_3_1, product.feature(JAVAC).version());
    }

    @Test
    public void shouldNotActivateScalaWhenFolderDoesntExist() {
        givenActivation(SCALA_0_1);
        givenProduct(TEST_PRODUCT_VERSION);

        Product product = repository().resolve(TEST_PRODUCT_VERSION).get();

        assertFalse("didn't expect to activate javac", product.hasFeature(JAVAC));
    }

    @Test
    public void shouldNotActivateOldVersionWithNonExistingPathEvenWhenNewerVersionWouldActivateWithExistingPath() {
        givenActivation(JAVAC_0_0);
        givenProduct(TEST_PRODUCT_VERSION);

        Product product = repository().resolve(TEST_PRODUCT_VERSION).get();

        assertFalse("didn't expect to activate javac", product.hasFeature(JAVAC));
    }
}
