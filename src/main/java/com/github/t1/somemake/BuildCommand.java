package com.github.t1.somemake;

import static com.github.t1.somemake.Repositories.*;

import java.io.*;
import java.nio.file.*;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class BuildCommand implements Runnable {
    @CliArgument
    private Path repository = Paths.get("~/.somemake");
    @CliArgument
    private Path input = Paths.get("product.xml");
    @CliArgument
    private Path output = Paths.get("target", "pom.xml");

    @Override
    public void run() {
        FileSystemRepository fileSystemRepository = new FileSystemRepository(repository);
        repositories().register(fileSystemRepository);

        log.debug("build {} to {} repository {}", input.toAbsolutePath(), output, repository);
        long t = System.currentTimeMillis();

        Product product = fileSystemRepository.load(input);

        try (FileWriter out = new FileWriter(output.toFile())) {
            new PomWriter(product).writeTo(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.debug("done building {} after {}ms", input, System.currentTimeMillis() - t);
    }
}
