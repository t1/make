package com.github.t1.somemake.model;

import java.nio.file.*;


public interface Activation {
    public static final class FolderActivation implements Activation {
        private final Path path;

        public FolderActivation(String path) {
            this.path = Paths.get(path);
        }

        @Override
        public boolean active() {
            return Files.isDirectory(path);
        }
    }

    public static final Id ACTIVATION = Type.property("activation");

    public static Activation of(Product product) {
        String activationExpression = product.feature(ACTIVATION).value().get();
        if (activationExpression.startsWith("folder(") && activationExpression.endsWith(")"))
            return new FolderActivation(activationExpression.substring(7, activationExpression.length() - 1));
        throw new IllegalArgumentException("unsupported activation expression [" + activationExpression + "]");
    }

    public boolean active();
}
