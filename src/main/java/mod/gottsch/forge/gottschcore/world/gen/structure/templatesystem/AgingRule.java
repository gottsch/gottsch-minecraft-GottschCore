/*
 * This file is part of  GottschCore.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
 *
 * All rights reserved.
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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * One decay chain for one source block: {@code stone_bricks} &rarr;
 * {@code cracked_stone_bricks} &rarr; {@code cobblestone} &rarr; {@code gravel},
 * each step gated on its own probability and only reachable if the step before it
 * was reached.
 *
 * <p>Field names ({@code block} / {@code output_blocks}) match the JSON shape Village
 * Dungeons' {@code DynamicStateAgedProcessor} used, so existing datapack files work
 * against this implementation unchanged.</p>
 *
 * @author Mark Gottschling on Jul 27, 2026
 */
public record AgingRule(Block block, List<AgingStage> outputBlocks) {

    public static final Codec<AgingRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(AgingRule::block),
            AgingStage.CODEC.listOf().fieldOf("output_blocks").forGetter(AgingRule::outputBlocks)
    ).apply(instance, AgingRule::new));
}
