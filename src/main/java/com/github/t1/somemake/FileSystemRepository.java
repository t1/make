package com.github.t1.somemake;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.NonNull;

import com.github.t1.xml.Xml;

public class FileSystemRepository implements Repository {
    private final Path repositoryRoot;

    public FileSystemRepository(Path repositoryRoot) {
        this.repositoryRoot = check(repositoryRoot);
    }

    private Path check(Path repositoryRoot) {
        if (!Files.isDirectory(repositoryRoot)) {
            throw new IllegalArgumentException("repository path " + repositoryRoot + " not found");
        }
        return repositoryRoot;
    }

    @Override
    public void put(Product product) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Product> get(@NonNull Version version) {
        return resolvePath(version).map((Path path) -> {
            XmlStoredProduct product = load(path);
            checkVersion(version, product.version(), path);
            return product;
        });
    }

    private Optional<Path> resolvePath(Version version) {
        if (!version.id().idString().isEmpty()) {
            Path idPath = repositoryRoot.resolve(version.id().path());
            if (Files.exists(idPath)) {
                Optional<String> resolvedVersion = version.resolve(versions(idPath));
                if (resolvedVersion.isPresent()) {
                    Path versionPath = idPath.resolve(resolvedVersion.get());
                    if (Files.exists(versionPath)) {
                        return Optional.of(versionPath.resolve("product.xml"));
                    }
                }
            }
        }
        return Optional.empty();
    }

    private Stream<String> versions(Path basePath) {
        try {
            return Files.list(basePath).map(p -> p.getFileName().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public XmlStoredProduct load(Path path) {
        Xml xml = Xml.load(path.toUri());
        return new XmlStoredProduct(xml);
    }

    private void checkVersion(Version version, Version productVersion, Path path) {
        if (!version.matches(productVersion))
            throw new IllegalStateException("unexpected version in " + path + "\n" //
                    + "  expected: " + version + "\n" //
                    + "     found: " + productVersion);
    }
}
