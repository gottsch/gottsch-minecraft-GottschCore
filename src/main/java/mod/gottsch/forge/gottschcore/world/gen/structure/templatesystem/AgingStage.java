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
import mod.gottsch.forge.gottschcore.json.StrictCodecs;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Block;

/**
 * One step in an {@link AgingRule}'s decay chain: the block this stage degrades to,
 * and the chance of reaching it <em>given the previous stage was reached</em>.
 *
 * <p>The block is resolved by {@link BlockIds#CODEC}, so an unknown id is <strong>warned</strong>
 * at load. It still resolves to air and the stage still does nothing &mdash; this javadoc used to
 * claim the id "fails the datapack file loudly", and that was never true; see {@link BlockIds} for
 * why {@code byNameCodec()} cannot fail here.</p>
 *
 * @author Mark Gottschling on Jul 27, 2026
 */
public record AgingStage(Block block, double probability) {

    public static final Codec<AgingStage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockIds.CODEC.fieldOf("block").forGetter(AgingStage::block),
            StrictCodecs.strictOptionalFieldOf(Codec.DOUBLE, "probability", 0.0D).forGetter(AgingStage::probability)
    ).apply(instance, AgingStage::new));
}
