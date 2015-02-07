package com.github.t1.make.model;

import static com.github.t1.make.model.MergedProduct.*;
import static com.github.t1.make.model.Type.*;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.*;
import org.junit.rules.ExpectedException;

import com.google.common.collect.ImmutableList;

public class MergedProductTest {
    private static final Version VERSION = product("x").version("1.0");
    private static final Id FEATURE_ID = feature("f");
    private static final Product FEATURE = new ProductEntity(FEATURE_ID.version("1"));

    private final ProductEntity master = new ProductEntity(VERSION);
    private final ProductEntity servant = new ProductEntity(VERSION);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldNotMergeNoProduct() {
        // merged();
    }

    @Test
    public void shouldNotMergeOneProduct() {
        // merged(master);
    }

    @Test
    public void shouldMergeTwoProducts() {
        merged(master, servant);
    }

    @Test
    public void shouldMergeThreeProducts() {
        merged(master, servant, new ProductEntity(VERSION));
    }

    @Test
    public void shouldMergeDifferentTypes() {
        Product mergedProduct = merged(master, new ProductEntity(Version.parse("stuff:x:1.0")));

        assertEquals(master.type(), mergedProduct.type());
    }

    @Test
    public void shouldMergeDifferentId() {
        Product mergedProduct = merged(master, new ProductEntity(Version.parse("product:y:1.0")));

        assertEquals(master.id(), mergedProduct.id());
    }

    @Test
    public void shouldNotMergeDifferentVersion() {
        Product mergedProduct = merged(master, new ProductEntity(Version.parse("product:x:2.0")));

        assertEquals(master.version(), mergedProduct.version());
    }

    @Test
    public void shouldMergeEquals() {
        Product merged1 = merged(master, servant);
        Product merged2 = merged(master, servant);

        assertEquals(merged2, merged1);
    }

    @Test
    public void shouldMergeTypeIdAndVersion() {
        Product mergedProduct = merged(master, servant);

        assertEquals(VERSION.type(), mergedProduct.type());
        assertEquals(VERSION.id(), mergedProduct.id());
        assertEquals(VERSION, mergedProduct.version());
    }

    @Test
    public void shouldMergeWildcardVersion() {
        Product mergedProduct = merged(new ProductEntity(Version.parse("product:x:1.*")), servant);

        assertEquals(VERSION, mergedProduct.version());
    }

    @Test
    public void shouldMergeEmptyName() {
        Product mergedProduct = merged(master, servant);

        assertEquals(Optional.empty(), mergedProduct.name());
    }

    @Test
    public void shouldMergeMasterName() {
        master.name("master");
        servant.name("servant");

        Product mergedProduct = merged(master, servant);

        assertEquals("master", mergedProduct.name().get());
    }

    @Test
    public void shouldMergeServantName() {
        servant.name("servant");

        Product mergedProduct = merged(master, servant);

        assertEquals("servant", mergedProduct.name().get());
    }

    @Test
    public void shouldMergeEmptyDescription() {
        Product mergedProduct = merged(master, servant);

        assertEquals(Optional.empty(), mergedProduct.description());
    }

    @Test
    public void shouldMergeMasterDescription() {
        master.description("master");
        servant.description("servant");

        Product mergedProduct = merged(master, servant);

        assertEquals("master", mergedProduct.description().get());
    }

    @Test
    public void shouldMergeServantDescription() {
        servant.description("servant");

        Product mergedProduct = merged(master, servant);

        assertEquals("servant", mergedProduct.description().get());
    }

    @Test
    public void shouldMergeEmptyReleaseTimestamp() {
        Product mergedProduct = merged(master, servant);

        assertEquals(Optional.empty(), mergedProduct.releaseTimestamp());
    }

    @Test
    public void shouldMergeMasterReleaseTimestamp() {
        master.releaseTimestamp(LocalDateTime.now());
        servant.releaseTimestamp(LocalDateTime.now().plusDays(1));

        Product mergedProduct = merged(master, servant);

        assertEquals(master.releaseTimestamp(), mergedProduct.releaseTimestamp());
    }

    @Test
    public void shouldMergeServantReleaseTimestamp() {
        servant.releaseTimestamp(LocalDateTime.now());

        Product mergedProduct = merged(master, servant);

        assertEquals(servant.releaseTimestamp(), mergedProduct.releaseTimestamp());
    }

    @Test
    public void shouldMergeEmptyValue() {
        Product mergedProduct = merged(master, servant);

        assertEquals(Optional.empty(), mergedProduct.value());
    }

    @Test
    public void shouldMergeMasterValue() {
        master.value("master");
        servant.value("servant");

        Product mergedProduct = merged(master, servant);

        assertEquals("master", mergedProduct.value().get());
    }

    @Test
    public void shouldMergeServantValue() {
        servant.value("servant");

        Product mergedProduct = merged(master, servant);

        assertEquals("servant", mergedProduct.value().get());
    }

    @Test
    public void shouldNotImplementUnresolvedFeatures() {
        expectedException.expect(UnsupportedOperationException.class);

        Product mergedProduct = merged(master, servant);

        mergedProduct.unresolvedFeatures();
    }

    @Test
    public void shouldMergeEmptyGetAttribute() {
        Product mergedProduct = merged(master, servant);

        assertEquals(Optional.empty(), mergedProduct.attribute("atr"));
    }

    @Test
    public void shouldMergeMasterGetAttribute() {
        master.attribute("atr", "master");
        servant.attribute("atr", "servant");

        Product mergedProduct = merged(master, servant);

        assertEquals("master", mergedProduct.attribute("atr").get());
    }

    @Test
    public void shouldMergeServantGetAttribute() {
        servant.attribute("atr", "servant");

        Product mergedProduct = merged(master, servant);

        assertEquals("servant", mergedProduct.attribute("atr").get());
    }

    @Test
    public void shouldMergeEmptySetAttributeToMaster() {
        Product mergedProduct = merged(master, servant);

        mergedProduct.attribute("atr", "value");

        assertEquals("value", master.attribute("atr").get());
        assertEquals(Optional.empty(), servant.attribute("atr"));
    }

    @Test
    public void shouldMergeMasterSetAttributeToMaster() {
        master.attribute("atr", "master");
        servant.attribute("atr", "servant");
        Product mergedProduct = merged(master, servant);

        mergedProduct.attribute("atr", "value");

        assertEquals("value", master.attribute("atr").get());
        assertEquals("servant", servant.attribute("atr").get());
    }

    @Test
    public void shouldMergeServantSetAttributeToServant() {
        servant.attribute("atr", "servant");
        Product mergedProduct = merged(master, servant);

        mergedProduct.attribute("atr", "value");

        assertEquals(Optional.empty(), master.attribute("atr"));
        assertEquals("value", servant.attribute("atr").get());
    }

    @Test
    public void shouldMergeEmptyFeatures() {
        Product mergedProduct = merged(master, servant);

        assertEquals(ImmutableList.of(), mergedProduct.features());
    }

    @Test
    public void shouldMergeMasterFeature() {
        master.add(FEATURE);

        Product mergedProduct = merged(master, servant);

        assertEquals(ImmutableList.of(FEATURE), mergedProduct.features());
    }

    @Test
    public void shouldMergeServantFeature() {
        servant.add(FEATURE);

        Product mergedProduct = merged(master, servant);

        assertEquals(ImmutableList.of(FEATURE), mergedProduct.features());
    }

    @Test
    public void shouldMergeFeature() {
        master.add(FEATURE);
        servant.add(new ProductEntity(FEATURE_ID.version("2")));

        Product mergedProduct = merged(master, servant);

        assertEquals(ImmutableList.of(FEATURE), mergedProduct.features());
    }

    @Test
    public void shouldNotCrashOnToString() {
        merged(master, servant).toString();
    }
}
