package com.github.t1.somemake.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.*;

import org.junit.*;

import com.github.t1.xml.Xml;

public class FileActivationsTest extends AbstractActivationsTest {
    private static final Path ACTIVATIONS_XML = REPOSITORY_ROOT.resolve("activations.xml");

    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

    private static final String NO_ACTIVATION = XML_HEADER + "<activations/>";
    private static final String ONE_ACTIVATION = XML_HEADER //
            + "<activations>\n" //
            + "    <activation id=\"" + JAVAC_3_1 + "\">folder(src/main/java)</activation>\n" //
            + "</activations>";
    private static final String TWO_ACTIVATIONS = XML_HEADER //
            + "<activations>\n" //
            + "    <activation id=\"" + JAVAC_3_1 + "\">folder(src/main/java)</activation>\n" //
            + "    <activation id=\"" + SCALA_0_1 + "\">folder(src/main/scala)</activation>\n" //
            + "</activations>";

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
    public void shouldLoadEmptyActivations() throws IOException {
        Files.write(ACTIVATIONS_XML, NO_ACTIVATION.getBytes());

        fileRepository.loadActivations();

        assertTrue(fileRepository.activations().isEmpty());
    }

    @Test
    public void shouldLoadOneActivation() throws IOException {
        Files.write(ACTIVATIONS_XML, ONE_ACTIVATION.getBytes());

        fileRepository.loadActivations();

        assertEquals(1, fileRepository.activations().size());
        assertEquals(JAVAC_3_1, fileRepository.activations().get(0));
    }

    @Test
    public void shouldLoadTwoActivations() throws IOException {
        Files.write(ACTIVATIONS_XML, TWO_ACTIVATIONS.getBytes());

        fileRepository.loadActivations();

        assertEquals(2, fileRepository.activations().size());
        assertEquals(JAVAC_3_1, fileRepository.activations().get(0));
        assertEquals(SCALA_0_1, fileRepository.activations().get(1));
    }

    @Test
    public void shouldSaveEmptyActivations() {
        fileRepository.saveActivations();

        assertEquals(NO_ACTIVATION, readFile(ACTIVATIONS_XML));
    }

    @Test
    public void shouldSaveOneActivation() {
        givenActivation(JAVAC_3_1);

        fileRepository.saveActivations();

        assertEquals(ONE_ACTIVATION, readFile(ACTIVATIONS_XML));
    }

    @Test
    public void shouldSaveTwoActivations() {
        givenActivation(JAVAC_3_1);
        givenActivation(SCALA_0_1);

        fileRepository.saveActivations();

        assertEquals(TWO_ACTIVATIONS, readFile(ACTIVATIONS_XML));
    }

    @Test
    public void shouldLoadActivationsWhenCreatingFileSystemRepository() {
        Xml xml = Xml.createWithRootElement("activations");
        xml.addElement("activation").addAttribute("id", JAVAC_3_1.toString()).addText("folder(src/main/java)");
        xml.save(ACTIVATIONS_XML.toUri());

        fileRepository.loadActivations();

        assertEquals(1, fileRepository.activations().size());
        assertEquals(JAVAC_3_1, fileRepository.activations().get(0));
    }
}
