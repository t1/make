package com.github.t1.somemake.model;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

import lombok.NonNull;

import com.github.t1.xml.*;
import com.google.common.collect.ImmutableList;

public class FileSystemRepository extends Repository {
    private final Path repositoryRoot;
    private final Map<Activation, Version> activations = new LinkedHashMap<>();

    public FileSystemRepository(Path repositoryRoot) {
        this.repositoryRoot = check(repositoryRoot);

        if (Files.isReadable(activationsPath())) {
            loadActivations();
        }
    }

    public URI activationsUri() {
        return activationsPath().toUri();
    }

    public Path activationsPath() {
        return repositoryRoot.resolve("activations.xml");
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
    public Optional<Product> resolve(@NonNull Version version) {
        Product product = load(version);
        if (product == null)
            return Optional.empty();
        return Optional.of(product);
    }

    private Product load(Version version) {
        Optional<Path> resolvedPath = resolvePath(version);
        if (!resolvedPath.isPresent())
            return null;
        Path path = resolvedPath.get();
        Product product = load(path);
        checkVersion(version, product.version(), path);
        return product;
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

    @Override
    public ImmutableList<Activation> activations() {
        return ImmutableList.copyOf(activations.keySet());
    }

    @Override
    public Version version(Activation activation) {
        return activations.get(activation);
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
    public void addActivation(Version version) {
        Optional<Product> optional = resolve(version);
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

    public void loadActivations() {
        Xml xml = Xml.load(activationsUri());
        for (XmlElement element : xml.elements()) {
            Version version = Version.parse(element.getAttribute("id"));
            Optional<Product> product = resolve(version);
            Activation activation = Activation.of(product.get());
            activations.put(activation, version);
        }
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
}
