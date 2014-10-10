package com.github.t1.somemake.model;

import java.nio.file.*;


public interface Activation {
    public static final class FolderActivation implements Activation {
        public static boolean matches(String expression) {
            return expression.startsWith("folder(") && expression.endsWith(")");
        }

        private final Path path;

        public FolderActivation(String expression) {
            this.path = Paths.get(expression.substring(7, expression.length() - 1));
        }

        @Override
        public boolean active() {
            return Files.isDirectory(path);
        }

        @Override
        public String toString() {
            return "folder(" + path + ")";
        }
    }

    public static final Id ACTIVATION = Type.property("activation");

    public static Activation of(Product product) {
        String activationExpression = product.feature(ACTIVATION).value().get();
        if (FolderActivation.matches(activationExpression))
            return new FolderActivation(activationExpression);
        throw new IllegalArgumentException("unsupported activation expression [" + activationExpression + "]");
    }

    public boolean active();
}
