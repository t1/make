package com.github.t1.make.cli;

import static com.github.t1.make.model.Repositories.*;
import static java.nio.file.StandardCopyOption.*;

import java.io.*;
import java.nio.file.*;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import com.github.t1.make.model.*;
import com.github.t1.make.pom.PomWriter;

@Slf4j
public class BuildCommand implements Runnable {
    @Getter
    @Setter
    @CliArgument
    private Path repository = Paths.get("~/.make");
    @Getter
    @Setter
    @CliArgument
    private Path inputDir = Paths.get(".");
    @Getter
    @Setter
    @CliArgument
    private Path pom = Paths.get("pom.xml");
    @Getter
    @Setter
    @CliArgument
    private Path maven = Paths.get("mvn");

    private Product product;

    @Override
    public void run() {
        log.info("build [{}] repository [{}]", inputDir, repository);
        log.debug("  pom [{}] maven [{}]", pom, maven);

        FileSystemRepository repo = new FileSystemRepository(repository);
        try {
            repositories().register(repo);
            timed("  build", () -> {
                timed("    load product", () -> loadProduct(repo));
                timed("    write pom", () -> writePom());
                timed("    run maven", () -> runMaven());
            });
        } finally {
            repositories().deregister(repo);
        }
    }

    private void timed(String message, Runnable runnable) {
        log.debug("{} start", message);
        long t0 = System.currentTimeMillis();
        try {
            runnable.run();
        } finally {
            long t1 = System.currentTimeMillis();
            log.debug("{} took {}", message, t1 - t0);
        }
    }

    private void loadProduct(FileSystemRepository repo) {
        Product unactivatedProduct = repo.loadFromDirectory(inputDir);
        this.product = repo.withActivations(unactivatedProduct);
    }

    private void writePom() {
        try {
            Path tempFile = Files.createTempFile("temp-pom", ".xml");
            try (FileWriter out = new FileWriter(tempFile.toFile())) {
                new PomWriter(product).writeTo(out);
            }
            Files.move(tempFile, pom, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void runMaven() {
        try {
            ProcessBuilder builder = new ProcessBuilder(maven.toString(), "clean", "package", "--file", pom.toString());
            log.debug("mvn command: {}", builder.command());
            Process process = builder.inheritIO().start();

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
