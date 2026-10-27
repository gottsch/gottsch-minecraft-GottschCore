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

import com.mojang.serialization.Codec;
import mod.gottsch.forge.gottschcore.json.StrictCodecs;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * One decoration behaviour of
 * {@link DecorationProcessor}:
 * how often it fires, and what it may place.
 *
 * <p>Unlike the aging chains, {@code probability} here is <strong>absolute</strong> &mdash;
 * one roll per candidate position, no conditional composition with anything else.</p>
 *
 * <p>A rule is <em>inactive</em> unless both a non-zero probability and a non-empty palette
 * are given, so every behaviour is off until a datapack turns it on. That's deliberate:
 * these palettes name blocks from other mods, and a motif that doesn't want a behaviour
 * shouldn't have to name blocks to disable it.</p>
 *
 * @author Mark Gottschling on Jul 28, 2026
 */
public record DecorationRule(float probability, List<WeightedGrowth> blocks) {

    public static final DecorationRule NONE = new DecorationRule(0.0F, List.of());

    /** Builds an unweighted rule, for callers that assemble a palette in code rather than JSON. */
    public static DecorationRule of(float probability, List<Block> blocks) {
        return new DecorationRule(probability, WeightedGrowth.unweighted(blocks));
    }

    public static final Codec<DecorationRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StrictCodecs.strictOptionalFieldOf(Codec.FLOAT, "probability", 0.0F).forGetter(DecorationRule::probability),
            StrictCodecs.strictOptionalFieldOf(WeightedGrowth.LIST_CODEC, "blocks", List.of()).forGetter(DecorationRule::blocks)
    ).apply(instance, DecorationRule::new));

    public DecorationRule {
        blocks = List.copyOf(blocks);
    }

    /**
     * A rule with no probability, an empty palette, or a palette whose weights are all zero does
     * nothing. The last of those is why this asks for the total rather than just {@code isEmpty}:
     * an all-zero palette has entries but nothing drawable, and treating it as active would put the
     * null from {@link WeightedGrowth#pick} into a caller that has no reason to expect one.
     */
    public boolean isActive() {
        return probability > 0.0F && WeightedGrowth.totalWeight(blocks) > 0;
    }

    /** Weighted pick. Draws from {@code random} even for a single-entry palette, so the
     *  number of draws at a position doesn't depend on how the palette was authored. An unweighted
     *  palette draws exactly as it did before weights existed -- see {@link WeightedGrowth}. */
    public WeightedGrowth pick(RandomSource random) {
        return WeightedGrowth.pick(blocks, random);
    }
}
