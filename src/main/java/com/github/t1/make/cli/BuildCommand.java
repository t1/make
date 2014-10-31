package com.github.t1.make.cli;

import static com.github.t1.make.model.Repositories.*;

import java.io.*;
import java.nio.file.*;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import com.github.t1.make.model.*;
import com.github.t1.make.pom.PomWriter;

@Getter
@Setter
@Slf4j
public class BuildCommand implements Runnable {
    @CliArgument
    private Path repository = Paths.get("~/.make");
    @CliArgument
    private Path inputDir = Paths.get(".");
    @CliArgument
    private Path pom = Paths.get("pom.xml");
    @CliArgument
    private Path maven = Paths.get("mvn");

    private FileSystemRepository fileSystemRepository;
    private Product product;

    @Override
    public void run() {
        log.info("build [{}] repository [{}]", inputDir, repository);

        fileSystemRepository = new FileSystemRepository(repository);
        repositories().register(fileSystemRepository);

        timed("  build", () -> {
            timed("    load product", () -> loadProduct());
            timed("    write pom", () -> writePom());
            timed("    run maven", () -> runMaven());
        });
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

    private void loadProduct() {
        Product unactivatedProduct = fileSystemRepository.loadFromDirectory(inputDir);
        this.product = fileSystemRepository.withActivations(unactivatedProduct);
    }

    private void writePom() {
        try (FileWriter out = new FileWriter(pom.toFile())) {
            new PomWriter(product).writeTo(out);
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
