package com.github.t1.somemake;

import static com.github.t1.somemake.Repositories.*;

import java.io.*;
import java.nio.file.*;

import lombok.*;

@Getter
@Setter
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
        Product product = fileSystemRepository.load(input);

        try (FileWriter out = new FileWriter(output.toFile())) {
            new PomWriter(product, out).write();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
