/*
 * This file is part of  GottschCore.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
 *
 * GottschCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GottschCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GottschCore.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.gottschcore.world.gen.structure.templatesystem;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Weighted decoration palettes.
 *
 * <p>The headline test is {@link #anUnweightedPaletteDrawsExactlyAsItDidBefore}. These palettes are
 * drawn during worldgen from a position-seeded random, so a draw that consumed a different amount of
 * randomness, or landed on a different index, would silently re-decorate every world that already
 * exists &mdash; with no error and no way back. Weights had to be added without touching the
 * unweighted case at all.</p>
 */
class WeightedGrowthTest {

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    /**
     * A method, not a {@code static final} field, and that is not a style choice. A static
     * initializer runs when the class is LOADED, which is before {@code @BeforeAll} -- so touching
     * {@code Blocks} from one throws out of {@code Bootstrap}'s reach and, because Gradle runs the
     * whole module in one forked JVM, takes every other test class down with it. The failure reads
     * as "47 tests failed" with no obvious connection to this file.
     */
    private static List<Block> palette() {
        return List.of(Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.MOSS_CARPET,
                Blocks.GRASS, Blocks.FERN, Blocks.DEAD_BUSH);
    }

    /**
     * The old implementation, verbatim: {@code blocks.get(random.nextInt(blocks.size()))}. Run
     * against the same seeds as the new one, they must agree on every draw.
     */
    @Test
    void anUnweightedPaletteDrawsExactlyAsItDidBefore() {
        List<Block> palette = palette();
        List<WeightedGrowth> weighted = WeightedGrowth.unweighted(palette);
        for (long seed = 0; seed < 500; seed++) {
            Block before = palette.get(RandomSource.create(seed).nextInt(palette.size()));
            Block after = WeightedGrowth.pick(weighted, RandomSource.create(seed)).block();
            assertEquals(before, after,
                    "seed " + seed + " decorates differently than it did before weights existed");
        }
    }

    /** And it must consume the same ONE draw, or everything downstream of it shifts. */
    @Test
    void oneDrawIsConsumedWhateverThePaletteLooksLike() {
        for (List<WeightedGrowth> palette : List.of(
                WeightedGrowth.unweighted(palette()),
                WeightedGrowth.unweighted(List.of(Blocks.FERN)),
                List.of(WeightedGrowth.of(Blocks.FERN, 97), WeightedGrowth.of(Blocks.GRASS, 3)))) {
            RandomSource actual = RandomSource.create(1234L);
            WeightedGrowth.pick(palette, actual);

            RandomSource expected = RandomSource.create(1234L);
            expected.nextInt(WeightedGrowth.totalWeight(palette));

            assertEquals(expected.nextLong(), actual.nextLong(),
                    "the stream is at a different position, so this palette consumed a different"
                            + " number of draws");
        }
    }

    @Test
    void weightsActuallySkewTheDraw() {
        List<WeightedGrowth> palette = List.of(
                WeightedGrowth.of(Blocks.BROWN_MUSHROOM, 10),
                WeightedGrowth.of(Blocks.FERN, 10),
                WeightedGrowth.of(Blocks.DEAD_BUSH, 1));

        Map<Block, Integer> counts = new HashMap<>();
        RandomSource random = RandomSource.create(0xC0FFEEL);
        int draws = 21_000;
        for (int i = 0; i < draws; i++) {
            counts.merge(WeightedGrowth.pick(palette, random).block(), 1, Integer::sum);
        }

        // 10 : 10 : 1 over 21 total -- expect ~47.6% / ~47.6% / ~4.8%.
        double rare = 100.0 * counts.getOrDefault(Blocks.DEAD_BUSH, 0) / draws;
        double common = 100.0 * counts.getOrDefault(Blocks.FERN, 0) / draws;
        assertTrue(rare > 3.8 && rare < 5.8, "weight 1 of 21 drew " + rare + "%, expected ~4.8%");
        assertTrue(common > 45.0 && common < 50.0,
                "weight 10 of 21 drew " + common + "%, expected ~47.6%");
    }

    @Test
    void weightZeroIsNeverDrawnAndAnAllZeroPaletteIsInactive() {
        List<WeightedGrowth> palette = List.of(
                WeightedGrowth.of(Blocks.FERN, 1), WeightedGrowth.of(Blocks.DEAD_BUSH, 0));
        RandomSource random = RandomSource.create(7L);
        for (int i = 0; i < 500; i++) {
            assertEquals(Blocks.FERN, WeightedGrowth.pick(palette, random).block(),
                    "a weight-0 entry was drawn");
        }

        List<WeightedGrowth> allZero = List.of(WeightedGrowth.of(Blocks.FERN, 0));
        assertEquals(0, WeightedGrowth.totalWeight(allZero));
        assertNull(WeightedGrowth.pick(allZero, RandomSource.create(1L)));
        // The rule must report itself inactive rather than handing that null to a caller.
        assertFalse(new DecorationRule(1.0F, allZero).isActive());
        assertFalse(new WallGrowthRule(1.0F, 0.0F, 1.0F, allZero).isActive());
    }

    @Test
    void bothJsonFormsDecodeAndMixInOneList() {
        String json = """
                {
                  "probability": 0.35,
                  "blocks": [
                    "minecraft:brown_mushroom",
                    { "block": "minecraft:fern", "weight": 5 },
                    { "block": "minecraft:dead_bush", "weight": 1 }
                  ]
                }
                """;
        DecorationRule rule = DecorationRule.CODEC
                .parse(JsonOps.INSTANCE, JsonParser.parseString(json))
                .getOrThrow(false, message -> { throw new AssertionError(message); });

        assertEquals(3, rule.blocks().size());
        assertEquals(WeightedGrowth.of(Blocks.BROWN_MUSHROOM, 1), rule.blocks().get(0),
                "a bare id must mean weight 1, or every pre-weights pack changes meaning");
        assertEquals(WeightedGrowth.of(Blocks.FERN, 5), rule.blocks().get(1));
        assertEquals(WeightedGrowth.of(Blocks.DEAD_BUSH, 1), rule.blocks().get(2),
                "an explicit weight of 1 must survive decoding");
        assertEquals(7, WeightedGrowth.totalWeight(rule.blocks()));
    }

    /**
     * A round trip must not rewrite an unweighted pack into the verbose form: someone re-encoding a
     * datapack should get their file back, not a diff on every palette they never touched.
     */
    @Test
    void weightOneEncodesBackToABareId() {
        DecorationRule rule = new DecorationRule(0.35F, List.of(
                WeightedGrowth.of(Blocks.FERN, 1), WeightedGrowth.of(Blocks.DEAD_BUSH, 4)));
        JsonElement encoded = DecorationRule.CODEC
                .encodeStart(JsonOps.INSTANCE, rule)
                .getOrThrow(false, message -> { throw new AssertionError(message); });

        JsonElement blocks = encoded.getAsJsonObject().get("blocks");
        assertTrue(blocks.getAsJsonArray().get(0).isJsonPrimitive(),
                "weight 1 should encode as a bare id, got " + blocks.getAsJsonArray().get(0));
        assertTrue(blocks.getAsJsonArray().get(1).isJsonObject(),
                "a real weight has to survive the round trip");

        DecorationRule back = DecorationRule.CODEC
                .parse(JsonOps.INSTANCE, encoded)
                .getOrThrow(false, message -> { throw new AssertionError(message); });
        assertEquals(rule, back);
    }

    /**
     * A negative weight would make the cumulative walk select an arbitrary entry rather than fail,
     * so it has to be caught at decode.
     *
     * <p>This test is why {@code weight} is a required field. Written as
     * {@code optionalFieldOf("weight", 1)}, DFU turns the failed range check into an ABSENT field
     * and substitutes the default, so {@code -3} decoded happily as {@code 1} and nothing else
     * noticed.</p>
     */
    @Test
    void aNegativeWeightIsRejectedRatherThanCorruptingTheWalk() {
        assertTrue(parseEntry("{ \"block\": \"minecraft:fern\", \"weight\": -3 }").error().isPresent(),
                "a negative weight decoded without complaint");
    }

    /** The object form has to state its weight; a bare id is how you say "weight 1". */
    @Test
    void anObjectWithoutAWeightIsALoadError() {
        assertTrue(parseEntry("{ \"block\": \"minecraft:fern\" }").error().isPresent(),
                "an object form with no weight decoded, which reopens the swallowing path");
    }

    /**
     * Asserted against the entry codec, not against a whole {@code DecorationRule}, and the reason
     * is worth knowing before writing any test in this package.
     *
     * <p>Both rules declare their palette as {@code optionalFieldOf("blocks", List.of())}. DFU's
     * optional-with-default treats "present but failed to decode" the same as "absent", so a rule
     * containing ONE malformed entry does not fail &mdash; it decodes with an <strong>empty
     * palette</strong>, reports itself inactive, and that decoration behaviour silently vanishes
     * from the pack. Going through {@code DecorationRule.CODEC} here would therefore have passed no
     * matter what this codec did.</p>
     *
     * <p>That outer leniency is pre-existing and is not what weights changed, so it is left alone
     * &mdash; but it is a real silent-failure path and is worth closing separately, the way
     * Dungeons2 closed the same class of bug for its own configs.</p>
     */
    private static com.mojang.serialization.DataResult<WeightedGrowth> parseEntry(String json) {
        return WeightedGrowth.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(json));
    }

    // --- entity entries -------------------------------------------------------------------------

    /** The point of the whole exercise: a palette can name a mob instead of a block. */
    @Test
    void anEntityEntryDecodes() {
        DecorationRule rule = DecorationRule.CODEC
                .parse(JsonOps.INSTANCE, JsonParser.parseString("""
                        {
                          "probability": 0.35,
                          "blocks": [
                            { "block": "minecraft:brown_mushroom", "weight": 10 },
                            { "entity": "dungeons2:shrieker", "weight": 1 }
                          ]
                        }
                        """))
                .getOrThrow(false, message -> { throw new AssertionError(message); });

        assertEquals(2, rule.blocks().size());
        assertTrue(rule.blocks().get(0).isBlock());
        assertFalse(rule.blocks().get(0).isEntity());

        WeightedGrowth fungus = rule.blocks().get(1);
        assertTrue(fungus.isEntity());
        assertFalse(fungus.isBlock());
        assertEquals("dungeons2:shrieker", fungus.entity().toString());
        assertEquals(1, fungus.weight());
        // An unregistered entity id must NOT be resolved at decode: a datapack may legitimately
        // name a mob from a mod the pack ships alongside, and this decodes at load.
        assertEquals(11, WeightedGrowth.totalWeight(rule.blocks()));
        assertTrue(rule.isActive());
    }

    /** Both is an author expressing two intentions; neither does nothing. Guessing is worse. */
    @Test
    void anEntryMustNameExactlyOneOfBlockOrEntity() {
        assertTrue(parseEntry(
                        "{ \"block\": \"minecraft:fern\", \"entity\": \"minecraft:bat\", \"weight\": 1 }")
                        .error().isPresent(),
                "an entry naming both a block and an entity decoded");
        assertTrue(parseEntry("{ \"weight\": 1 }").error().isPresent(),
                "an entry naming neither decoded, so the palette would silently shrink");
    }

    /** An entity entry is verbose by nature -- there is no bare-id shorthand for one. */
    @Test
    void anEntityEntryRoundTrips() {
        DecorationRule rule = new DecorationRule(0.35F, List.of(
                WeightedGrowth.of(Blocks.FERN, 1),
                WeightedGrowth.ofEntity(new net.minecraft.resources.ResourceLocation(
                        "dungeons2", "violet_fungus"), 2)));
        JsonElement encoded = DecorationRule.CODEC
                .encodeStart(JsonOps.INSTANCE, rule)
                .getOrThrow(false, message -> { throw new AssertionError(message); });
        DecorationRule back = DecorationRule.CODEC
                .parse(JsonOps.INSTANCE, encoded)
                .getOrThrow(false, message -> { throw new AssertionError(message); });
        assertEquals(rule, back);

        JsonElement entry = encoded.getAsJsonObject().get("blocks").getAsJsonArray().get(1);
        assertTrue(entry.isJsonObject() && entry.getAsJsonObject().has("entity"),
                "an entity entry must survive as an object with an 'entity' key, got " + entry);
    }

    /** Weighting works the same whichever kind of entry wins. */
    @Test
    void anEntityEntryTakesItsShareOfTheDraw() {
        List<WeightedGrowth> palette = List.of(
                WeightedGrowth.of(Blocks.FERN, 9),
                WeightedGrowth.ofEntity(new net.minecraft.resources.ResourceLocation(
                        "dungeons2", "shrieker"), 1));
        RandomSource random = RandomSource.create(0xF0E1L);
        int entities = 0;
        int draws = 10_000;
        for (int i = 0; i < draws; i++) {
            if (WeightedGrowth.pick(palette, random).isEntity()) {
                entities++;
            }
        }
        double rate = 100.0 * entities / draws;
        assertTrue(rate > 8.0 && rate < 12.0, "1 of 10 drew " + rate + "%, expected ~10%");
    }
}
