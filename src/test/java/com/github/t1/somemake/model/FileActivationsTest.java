package com.github.t1.somemake.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.*;

import org.junit.*;

import com.github.t1.xml.Xml;

public class FileActivationsTest extends AbstractActivationsTest {
    private static final Path ACTIVATIONS_XML = REPOSITORY_ROOT.resolve("activations.xml");

    @Before
    @After
    public void cleanActivationsXml() throws IOException {
        Files.deleteIfExists(ACTIVATIONS_XML);
    }

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

    @Test
    public void shouldSaveEmptyActivations() {
        fileRepository.saveActivations();

        assertEquals(Xml.createWithRootElement("activations").toXmlString().trim(), readFile(ACTIVATIONS_XML));
    }

    @Test
    public void shouldSaveOneActivation() {
        givenActivation(JAVAC_3_1);

        fileRepository.saveActivations();

        Xml xml = Xml.createWithRootElement("activations");
        xml.addElement("activation").addAttribute("id", JAVAC_3_1.toString()).addText("folder(src/main/java)");
        assertEquals(xml.toXmlString().trim(), readFile(ACTIVATIONS_XML));
    }

    @Test
    public void shouldSaveTwoActivations() {
        givenActivation(JAVAC_3_1);
        givenActivation(SCALA_0_1);

        fileRepository.saveActivations();

        Xml xml = Xml.createWithRootElement("activations");
        xml.addElement("activation").addAttribute("id", JAVAC_3_1.toString()).addText("folder(src/main/java)");
        xml.addElement("activation").addAttribute("id", SCALA_0_1.toString()).addText("folder(src/main/scala)");
        assertEquals(xml.toXmlString().trim(), readFile(ACTIVATIONS_XML));
    }
}
