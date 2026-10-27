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

import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Optional fields accept being omitted, and refuse to be wrong.
 *
 * <h2>What was broken</h2>
 * <p>DFU's {@code optionalFieldOf(name, default)} substitutes the default when the field is absent
 * <em>and</em> when it is present but fails to decode &mdash; {@code OptionalFieldCodec} maps any
 * decode failure to an empty optional. Every field in this package was written that way, so a
 * malformed value did not fail the pack; it made the behaviour quietly disappear:</p>
 *
 * <ul>
 *   <li>a bad {@code probability} became {@code 0.0} and the rule went inert;</li>
 *   <li>ONE bad entry in a {@code blocks} palette emptied the whole palette, since a list codec
 *       fails as a unit &mdash; and an empty palette makes its rule report itself inactive;</li>
 *   <li>a bad {@code floor_growth} object became {@code NONE}, losing the behaviour entirely.</li>
 * </ul>
 *
 * <p>No error, no log line, pack loads. The author gets a plainer dungeon than they wrote and
 * nothing to go on. These tests are the difference between that and a named field in a stack trace.</p>
 */
class StrictFieldsTest {

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    private static DataResult<DecorationRule> rule(String json) {
        return DecorationRule.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(json));
    }

    private static DataResult<DecorationProcessor> processor(String json) {
        return DecorationProcessor.codec(() -> null)
                .parse(JsonOps.INSTANCE, JsonParser.parseString(json));
    }

    /** The case that started this: one bad entry used to empty the whole palette. */
    @Test
    void oneMalformedPaletteEntryFailsTheFileInsteadOfEmptyingThePalette() {
        DataResult<DecorationRule> result = rule("""
                { "probability": 0.35, "blocks": [ "minecraft:fern", { "weight": 4 } ] }
                """);
        assertTrue(result.error().isPresent(),
                "a palette with a blockless entry decoded; it would have silently grown nothing");
        assertTrue(result.error().get().message().contains("blocks"),
                "the error should name the field: " + result.error().get().message());
    }

    @Test
    void aMalformedProbabilityFailsInsteadOfSilentlyDisablingTheRule() {
        DataResult<DecorationRule> result = rule("""
                { "probability": "often", "blocks": [ "minecraft:fern" ] }
                """);
        assertTrue(result.error().isPresent(),
                "probability decoded as its default, so the rule would never have fired");
        assertTrue(result.error().get().message().contains("probability"),
                "the error should name the field: " + result.error().get().message());
    }

    @Test
    void aMalformedRuleFailsInsteadOfBecomingNONE() {
        DataResult<DecorationProcessor> result = processor("""
                {
                  "processor_type": "gottschcore:decoration",
                  "floor_growth": { "probability": 0.35, "blocks": "minecraft:fern" }
                }
                """);
        assertTrue(result.error().isPresent(),
                "floor_growth decoded as NONE, losing the whole behaviour without a word");
        assertTrue(result.error().get().message().contains("floor_growth"),
                "the error should name the field: " + result.error().get().message());
    }

    @Test
    void aMalformedWallGrowthNumberFailsToo() {
        assertTrue(processor("""
                {
                  "processor_type": "gottschcore:decoration",
                  "wall_growth": { "probability": 0.1, "max": "lots",
                                   "blocks": [ "minecraft:glow_lichen" ] }
                }
                """).error().isPresent(), "wall_growth max decoded as its default");
    }

    @Test
    void aMalformedBlockMatchFailsToo() {
        assertTrue(processor("""
                {
                  "processor_type": "gottschcore:decoration",
                  "dirt": { "tags": "minecraft:dirt" }
                }
                """).error().isPresent(), "dirt tags decoded as an empty list");
    }

    /**
     * The other half, and the half that keeps every existing pack loading: omitting a field is
     * still perfectly legal and still yields the default. Strict is about wrong, not about absent.
     */
    @Test
    void anOmittedFieldStillTakesItsDefault() {
        DecorationRule blocksOnly = rule("""
                { "blocks": [ "minecraft:fern" ] }
                """).getOrThrow(false, message -> { throw new AssertionError(message); });
        assertEquals(0.0F, blocksOnly.probability());

        DecorationRule probabilityOnly = rule("""
                { "probability": 0.5 }
                """).getOrThrow(false, message -> { throw new AssertionError(message); });
        assertTrue(probabilityOnly.blocks().isEmpty());

        // ...and a whole processor with nothing configured is still the all-NONE processor.
        DecorationProcessor bare = processor("""
                { "processor_type": "gottschcore:decoration" }
                """).getOrThrow(false, message -> { throw new AssertionError(message); });
        assertTrue(bare != null);
    }

    /** A well-formed pack is unaffected in every particular. */
    @Test
    void aWellFormedRuleIsUntouched() {
        DecorationRule decoded = rule("""
                {
                  "probability": 0.35,
                  "blocks": [ "minecraft:fern", { "block": "minecraft:dead_bush", "weight": 3 } ]
                }
                """).getOrThrow(false, message -> { throw new AssertionError(message); });
        assertEquals(0.35F, decoded.probability());
        assertEquals(2, decoded.blocks().size());
        assertEquals(WeightedGrowth.of(Blocks.FERN, 1), decoded.blocks().get(0));
        assertEquals(WeightedGrowth.of(Blocks.DEAD_BUSH, 3), decoded.blocks().get(1));
        assertTrue(decoded.isActive());
    }
}
