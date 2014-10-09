package com.github.t1.somemake.model;

import static com.github.t1.somemake.model.Activation.*;
import static com.github.t1.somemake.model.Repositories.*;
import static com.github.t1.somemake.model.Type.*;
import static org.junit.Assert.*;

import org.junit.*;

public class ActivationsTest extends AbstractFileRepositoryTest {
    private static final Id JAVAC = Type.type("plugin").id("compiler.java");
    private static final Version JAVAC_VERSION = JAVAC.version("3.1");
    private static final String ACTIVATION_EXPRESSION = "folder(.)";

    @Before
    @After
    public void cleanActivations() {
        fileRepository.clearActivations();
        memRepository.clearActivations();
    }

    private Product javac() {
        return new ProductEntity(JAVAC_VERSION).name("javac").set(ACTIVATION, ACTIVATION_EXPRESSION);
    }

    @Test
    public void shouldFindJavac() {
        Product product = repositories().get(JAVAC_VERSION).get();

        assertEquals(JAVAC_VERSION, product.version());
    }

    @Test
    public void shouldStoreActivationInMemory() {
        Version fooVersion = feature("foo").version("1.0");
        memRepository.put(newProduct(fooVersion));
        memRepository.put(javac());

        memRepository.activate(JAVAC);
        Product foo = memRepository.get(fooVersion).get();

        assertEquals("3.1", foo.feature(JAVAC).versionString());
    }

    @Test
    public void shouldClearActivationInMemory() {
        memRepository.put(javac());
        memRepository.activate(JAVAC);

        memRepository.clearActivations();

        assertTrue(memRepository.activations().isEmpty());
    }

    @Test
    public void shouldActivateActivationInMemory() {
        memRepository.put(javac());

        memRepository.activate(JAVAC);

        assertTrue(memRepository.activations().contains(JAVAC_VERSION));
    }

    @Test
    public void shouldStoreActivationInFiles() {
        fileRepository.activate(JAVAC);

        assertTrue(fileRepository.activations().contains(JAVAC_VERSION));
    }

    @Test
    public void shouldClearActivationInFiles() {
        fileRepository.activate(JAVAC);

        fileRepository.clearActivations();

        assertTrue(fileRepository.activations().isEmpty());
    }

    @Test
    public void shouldActivateJavacInFiles() {
        fileRepository.activate(JAVAC);

        Version version = product("product:test-product").version("1.0");
        Product product = fileRepository.get(version).get();

        assertEquals(JAVAC_VERSION, product.feature(JAVAC).version());
    }
}
