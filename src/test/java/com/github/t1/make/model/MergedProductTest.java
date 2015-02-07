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
    public void shouldNotMergeDifferentType() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("types of products to be merged don't match");

        merged(master, new ProductEntity(Version.parse("stuff:x:1.0")));
    }

    @Test
    public void shouldNotMergeDifferentId() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("ids of products to be merged don't match");

        merged(master, new ProductEntity(Version.parse("product:y:1.0")));
    }

    @Test
    public void shouldNotMergeDifferentVersion() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("versions of products to be merged don't match");

        merged(master, new ProductEntity(Version.parse("product:x:2.0")));
    }

    @Test
    public void shouldMergeEquals() {
        MergedProduct merged1 = new MergedProduct(master, servant);
        MergedProduct merged2 = new MergedProduct(master, servant);

        assertEquals(merged2, merged1);
    }

    @Test
    public void shouldMergeTypeIdAndVersion() {
        MergedProduct merged = new MergedProduct(master, servant);

        assertEquals(VERSION.type(), merged.type());
        assertEquals(VERSION.id(), merged.id());
        assertEquals(VERSION, merged.version());
    }

    @Test
    public void shouldMergeWildcardVersion() {
        MergedProduct merged = new MergedProduct(new ProductEntity(Version.parse("product:x:1.*")), servant);

        assertEquals(VERSION, merged.version());
    }

    @Test
    public void shouldMergeEmptyName() {
        MergedProduct merged = new MergedProduct(master, servant);

        assertEquals(Optional.empty(), merged.name());
    }

    @Test
    public void shouldMergeMasterName() {
        master.name("master");
        servant.name("servant");

        MergedProduct merged = new MergedProduct(master, servant);

        assertEquals("master", merged.name().get());
    }

    @Test
    public void shouldMergeServantName() {
        servant.name("servant");

        MergedProduct merged = new MergedProduct(master, servant);

        assertEquals("servant", merged.name().get());
    }

    @Test
    public void shouldMergeEmptyDescription() {
        MergedProduct merged = new MergedProduct(master, servant);

        assertEquals(Optional.empty(), merged.description());
    }

    @Test
    public void shouldMergeMasterDescription() {
        master.description("master");
        servant.description("servant");

        MergedProduct merged = new MergedProduct(master, servant);

        assertEquals("master", merged.description().get());
    }

    @Test
    public void shouldMergeServantDescription() {
        servant.description("servant");

        MergedProduct merged = new MergedProduct(master, servant);

        assertEquals("servant", merged.description().get());
    }

    @Test
    public void shouldMergeEmptyReleaseTimestamp() {
        MergedProduct merged = new MergedProduct(master, servant);

        assertEquals(Optional.empty(), merged.releaseTimestamp());
    }

    @Test
    public void shouldMergeMasterReleaseTimestamp() {
        master.releaseTimestamp(LocalDateTime.now());
        servant.releaseTimestamp(LocalDateTime.now().plusDays(1));

        MergedProduct merged = new MergedProduct(master, servant);

        assertEquals(master.releaseTimestamp(), merged.releaseTimestamp());
    }

    @Test
    public void shouldMergeServantReleaseTimestamp() {
        servant.releaseTimestamp(LocalDateTime.now());

        MergedProduct merged = new MergedProduct(master, servant);

        assertEquals(servant.releaseTimestamp(), merged.releaseTimestamp());
    }

    @Test
    public void shouldMergeEmptyValue() {
        MergedProduct merged = new MergedProduct(master, servant);

        assertEquals(Optional.empty(), merged.value());
    }

    @Test
    public void shouldMergeMasterValue() {
        master.value("master");
        servant.value("servant");

        MergedProduct merged = new MergedProduct(master, servant);

        assertEquals("master", merged.value().get());
    }

    @Test
    public void shouldMergeServantValue() {
        servant.value("servant");

        MergedProduct merged = new MergedProduct(master, servant);

        assertEquals("servant", merged.value().get());
    }

    @Test
    public void shouldNotImplementUnresolvedFeatures() {
        expectedException.expect(UnsupportedOperationException.class);

        MergedProduct merged = new MergedProduct(master, servant);

        merged.unresolvedFeatures();
    }

    @Test
    public void shouldMergeEmptyGetAttribute() {
        MergedProduct merged = new MergedProduct(master, servant);

        assertEquals(Optional.empty(), merged.attribute("atr"));
    }

    @Test
    public void shouldMergeMasterGetAttribute() {
        master.attribute("atr", "master");
        servant.attribute("atr", "servant");

        MergedProduct merged = new MergedProduct(master, servant);

        assertEquals("master", merged.attribute("atr").get());
    }

    @Test
    public void shouldMergeServantGetAttribute() {
        servant.attribute("atr", "servant");

        MergedProduct merged = new MergedProduct(master, servant);

        assertEquals("servant", merged.attribute("atr").get());
    }

    @Test
    public void shouldMergeEmptySetAttributeToMaster() {
        MergedProduct merged = new MergedProduct(master, servant);

        merged.attribute("atr", "value");

        assertEquals("value", master.attribute("atr").get());
        assertEquals(Optional.empty(), servant.attribute("atr"));
    }

    @Test
    public void shouldMergeMasterSetAttributeToMaster() {
        master.attribute("atr", "master");
        servant.attribute("atr", "servant");
        MergedProduct merged = new MergedProduct(master, servant);

        merged.attribute("atr", "value");

        assertEquals("value", master.attribute("atr").get());
        assertEquals("servant", servant.attribute("atr").get());
    }

    @Test
    public void shouldMergeServantSetAttributeToServant() {
        servant.attribute("atr", "servant");
        MergedProduct merged = new MergedProduct(master, servant);

        merged.attribute("atr", "value");

        assertEquals(Optional.empty(), master.attribute("atr"));
        assertEquals("value", servant.attribute("atr").get());
    }

    @Test
    public void shouldMergeEmptyFeatures() {
        MergedProduct merged = new MergedProduct(master, servant);

        assertEquals(ImmutableList.of(), merged.features());
    }

    @Test
    public void shouldMergeMasterFeature() {
        master.add(FEATURE);

        MergedProduct merged = new MergedProduct(master, servant);

        assertEquals(ImmutableList.of(FEATURE), merged.features());
    }

    @Test
    public void shouldMergeServantFeature() {
        servant.add(FEATURE);

        MergedProduct merged = new MergedProduct(master, servant);

        assertEquals(ImmutableList.of(FEATURE), merged.features());
    }

    @Test
    public void shouldMergeFeature() {
        master.add(FEATURE);
        servant.add(new ProductEntity(FEATURE_ID.version("2")));

        MergedProduct merged = new MergedProduct(master, servant);

        assertEquals(ImmutableList.of(FEATURE), merged.features());
    }

    @Test
    public void shouldNotCrashOnToString() {
        new MergedProduct(master, servant).toString();
    }
}
