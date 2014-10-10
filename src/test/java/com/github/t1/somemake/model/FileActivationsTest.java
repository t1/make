package com.github.t1.somemake.model;

import static org.junit.Assert.*;


public class FileActivationsTest extends AbstractActivationsTest {
    @Override
    protected Repository repository() {
        return fileRepository;
    }

    @Override
    protected void givenActivation(Version version) {
        makeSureProductExistsInFileRepository(version);
        fileRepository.addActivation(version);
    }

    @Override
    protected void givenProduct(Version version) {
        makeSureProductExistsInFileRepository(version);
    }

    private void makeSureProductExistsInFileRepository(Version version) {
        Product javac = fileRepository.get(version).get();
        assertEquals(version, javac.version());
    }
}
