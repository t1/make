package com.github.t1.somemake.model;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

import lombok.NonNull;

import com.github.t1.xml.Xml;
import com.google.common.collect.ImmutableList;

public class FileSystemRepository implements Repository {
    private final Path repositoryRoot;
    private final Map<Activation, Version> activations = new HashMap<>();

    public FileSystemRepository(Path repositoryRoot) {
        this.repositoryRoot = check(repositoryRoot);
    }

    private Path check(Path repositoryRoot) {
        if (!Files.isDirectory(repositoryRoot))
            throw new IllegalArgumentException("repository path " + repositoryRoot + " not found");
        return repositoryRoot;
    }

    @Override
    public void put(Product product) {
        throw new UnsupportedOperationException("can't put into file repository");
    }

    @Override
    public Optional<Product> get(@NonNull Version version) {
        Optional<Path> resolvedPath = resolvePath(version);
        if (!resolvedPath.isPresent())
            return Optional.empty();
        Path path = resolvedPath.get();
        Product product = load(path);
        checkVersion(version, product.version(), path);
        product = withActivations(product);
        return Optional.of(product);
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

    private Product withActivations(Product product) {
        Product productActivations = null;
        for (Map.Entry<Activation, Version> entry : activations.entrySet()) {
            Version version = entry.getValue();
            if (version.equals(product.version()))
                continue;
            if (entry.getKey().active()) {
                if (productActivations == null)
                    productActivations = new ProductEntity(product.version());
                productActivations.add(get(version).get());
            }
        }
        if (productActivations == null)
            return product;
        return new MergedProduct(product, productActivations);
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

    @Override
    public ImmutableList<Version> activations() {
        return ImmutableList.copyOf(activations.values());
    }

    @Override
    public void addActivation(Version version) {
        Optional<Product> optional = get(version);
        if (!optional.isPresent())
            throw new IllegalStateException("version to activate not found: " + version);
        Product product = optional.get();
        activations.put(Activation.of(product), product.version());
    }

    @Override
    public void removeActivation(Version version) {
        Iterator<Map.Entry<Activation, Version>> iter = activations.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Activation, Version> entry = iter.next();
            if (entry.getValue().equals(version)) {
                iter.remove();
            }
        }
    }

    @Override
    public void clearAllActivations() {
        activations.clear();
    }

    public void saveActivations() {
        Xml xml = Xml.createWithRootElement("activations");
        xml.uri(activationsUri());

        for (Entry<Activation, Version> activation : activations.entrySet()) {
            xml.addElement("activation") //
                    .addAttribute("id", activation.getValue().toString()) //
                    .addText(activation.getKey().toString());
        }

        xml.save();
    }

    private URI activationsUri() {
        return repositoryRoot.resolve("activations.xml").toUri();
    }
}
