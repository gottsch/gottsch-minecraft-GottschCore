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
 * Wall growth: a {@link DecorationRule} plus the two knobs that make it <em>cluster</em>
 * instead of speckling evenly.
 *
 * <p>A candidate's chance is {@code probability + n × bonus}, capped at {@code max}, where
 * {@code n} is how many of its six neighbours are already growth. With a low
 * {@code probability} and a high {@code bonus}, growth is rare to start but spreads once
 * started, which is what makes it read as patches on a wall rather than static.</p>
 *
 * @author Mark Gottschling on Jul 28, 2026
 */
public record WallGrowthRule(float probability, float bonus, float max, List<WeightedGrowth> blocks) {

    public static final WallGrowthRule NONE = new WallGrowthRule(0.0F, 0.0F, 1.0F, List.of());

    /** Builds an unweighted rule; see {@code DecorationRule#of}. */
    public static WallGrowthRule of(float probability, float bonus, float max, List<Block> blocks) {
        return new WallGrowthRule(probability, bonus, max, WeightedGrowth.unweighted(blocks));
    }

    public static final Codec<WallGrowthRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StrictCodecs.strictOptionalFieldOf(Codec.FLOAT, "probability", 0.0F).forGetter(WallGrowthRule::probability),
            StrictCodecs.strictOptionalFieldOf(Codec.FLOAT, "bonus", 0.0F).forGetter(WallGrowthRule::bonus),
            StrictCodecs.strictOptionalFieldOf(Codec.FLOAT, "max", 1.0F).forGetter(WallGrowthRule::max),
            StrictCodecs.strictOptionalFieldOf(WeightedGrowth.LIST_CODEC, "blocks", List.of()).forGetter(WallGrowthRule::blocks)
    ).apply(instance, WallGrowthRule::new));

    public WallGrowthRule {
        blocks = List.copyOf(blocks);
    }

    public boolean isActive() {
        return probability > 0.0F && WeightedGrowth.totalWeight(blocks) > 0;
    }

    /** The chance for a candidate with {@code adjacentGrowth} growth blocks touching it. */
    public float chanceWith(int adjacentGrowth) {
        return Math.min(max, probability + adjacentGrowth * bonus);
    }

    public WeightedGrowth pick(RandomSource random) {
        return WeightedGrowth.pick(blocks, random);
    }
}
