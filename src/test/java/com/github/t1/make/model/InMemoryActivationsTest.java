package com.github.t1.make.model;

import static com.github.t1.make.model.Activation.*;

public class InMemoryActivationsTest extends AbstractActivationsTest {
    @Override
    protected Repository repository() {
        return memRepository;
    }

    @Override
    protected void givenActivation(Version version) {
        if (JAVAC_3_1.equals(version)) {
            givenActivationWithExpression(version, "folder(.)");
        } else if (JAVAC_0_0.equals(version)) {
            givenActivationWithExpression(version, "folder(does/not/exist)");
        } else if (SCALA_0_1.equals(version)) {
            givenActivationWithExpression(version, "folder(src/main/scala)");
        } else {
            throw new IllegalArgumentException("unknown version: " + version);
        }
        memRepository.addActivation(version);
    }

    private void givenActivationWithExpression(Version version, String expression) {
        ProductEntity product = new ProductEntity(version);
        product.addFeature(ACTIVATION, expression);
        product.addFeature(MAVEN_COMPILER_PLUGIN_3_1, "active");
        memRepository.put(product);
    }

    @Override
    protected void givenProduct(Version version) {
        repository().put(newProduct(version));
    }
}
