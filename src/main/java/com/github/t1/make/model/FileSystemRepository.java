package com.github.t1.make.model;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import com.github.t1.xml.*;
import com.google.common.collect.ImmutableList;

@Slf4j
public class FileSystemRepository extends Repository {
    @Getter
    private final Path repositoryRoot;
    private final Map<Activation, Version> activations = new LinkedHashMap<>();

    public FileSystemRepository(Path repositoryRoot) {
        this.repositoryRoot = check(resolved(repositoryRoot));

        if (Files.isReadable(activationsPath())) {
            loadActivations();
        }
    }

    private Path resolved(Path path) {
        if (path.startsWith("~/"))
            path = Paths.get(System.getProperty("user.home")).resolve(path.toString().substring(2));
        return path;
    }

    public URI activationsUri() {
        return activationsPath().toUri();
    }

    public Path activationsPath() {
        return repositoryRoot.resolve("activations.xml");
    }

    @SneakyThrows(IOException.class)
    private Path check(Path repositoryRoot) {
        if (!Files.exists(repositoryRoot))
            Files.createDirectories(repositoryRoot);
        if (!Files.isDirectory(repositoryRoot))
            throw new IllegalArgumentException("repository path [" + repositoryRoot + "] is not a directory");
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
        Product product = loadFromDirectory(path);
        checkVersion(version, product.version(), path);
        return product;
    }

    private Optional<Path> resolvePath(Version version) {
        if (!version.id().idString().isEmpty()) {
            Path idPath = repositoryRoot.resolve(version.id().path());
            if (Files.exists(idPath)) {
                Optional<String> resolvedVersion = version.resolve(files(idPath));
                if (resolvedVersion.isPresent()) {
                    Path versionPath = idPath.resolve(resolvedVersion.get());
                    if (Files.exists(versionPath)) {
                        return Optional.of(versionPath);
                    }
                } else if (Files.exists(idPath.resolve(Version.ANY))) {
                    return Optional.of(idPath.resolve(Version.ANY));
                }
            }
        }
        return Optional.empty();
    }

    private Stream<String> files(Path path) {
        try {
            return Files.list(path).map(p -> p.getFileName().toString());
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

    public Product loadFromDirectory(Path path) {
        Path file = path.resolve("product.xml");
        if (Files.exists(file))
            return new XmlStoredProduct(file.toUri());
        file = path.resolve("product.json");
        if (Files.exists(file))
            return new JsonStoredProduct(file.toUri());
        throw new UnsupportedOperationException("no supported product file found in: " + path);
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
            Version version = Version.parse(element.getAttribute(Id.ATTRIBUTE));
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
                    .setAttribute(Id.ATTRIBUTE, activation.getValue().toString()) //
                    .addText(activation.getKey().toString());
        }

        xml.save();
    }

    public void store(Product product) {
        Path dir = repositoryRoot.resolve(product.version().path());
        mkdirs(dir);
        log.debug("save {} to {}", product.version(), dir);
        product.saveTo(dir);
    }

    private static void mkdirs(Path dir) {
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
