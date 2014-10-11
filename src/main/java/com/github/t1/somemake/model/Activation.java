package com.github.t1.somemake.model;

import java.nio.file.*;
import java.util.regex.*;

import lombok.AllArgsConstructor;

public interface Activation {
    @AllArgsConstructor
    public static final class FolderActivation implements Activation {
        private static final Pattern PATTERN = Pattern.compile("folder\\((.*)\\)");

        private final Path path;

        public FolderActivation(String expression) {
            Matcher matcher = PATTERN.matcher(expression);
            if (!matcher.matches()) {
                this.path = null;
            } else {
                this.path = Paths.get(matcher.group(1));
            }
        }

        @Override
        public boolean matches() {
            return path != null;
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
        Activation activation = new FolderActivation(activationExpression);
        if (activation.matches())
            return activation;
        throw new IllegalArgumentException("unsupported activation expression [" + activationExpression + "]");
    }

    public boolean matches();

    public boolean active();
}
