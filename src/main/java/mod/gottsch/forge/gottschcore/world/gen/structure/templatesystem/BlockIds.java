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
import mod.gottsch.forge.gottschcore.GottschCore;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * The block-id codec the processors in this package decode with, and the reason they no longer use
 * {@code BuiltInRegistries.BLOCK.byNameCodec()} directly.
 *
 * <h2>The bug this exists to make visible</h2>
 * <p>{@code byNameCodec()} looks correct and reads as strict &mdash; it errors when the registry
 * returns null for an id. But {@code BuiltInRegistries.BLOCK} is a
 * {@code DefaultedMappedRegistry}, and <strong>its {@code get(ResourceLocation)} never returns
 * null</strong>: an unknown id resolves to the registry's default value, which is
 * {@code minecraft:air}. So {@code "dungeonblocks:mosy_bricks"} decodes <em>cleanly</em>, the rule
 * that uses it quietly does nothing, and a decoration palette built on it grows air.</p>
 *
 * <p>Every javadoc in this package that claimed a typo "fails the file loudly at load" was wrong
 * about that, for this reason.</p>
 *
 * <h2>Why it warns rather than failing the load</h2>
 * <p>GottschCore is shared. A hard load error would be right for a mod whose block dependency is
 * mandatory, and wrong for one whose datapack legitimately names blocks from a mod the player may
 * not have installed &mdash; that pack would stop loading entirely rather than losing one rule. So
 * this keeps the lenient result and makes it <strong>loud instead of silent</strong>, which is the
 * half that was actually missing.</p>
 *
 * <p>A consumer that wants strictness for its own content should assert it in a test over its own
 * shipped files, where it can fail the build instead of a player's world. That is the split
 * Dungeons2 uses.</p>
 *
 * <h2>Expect noise in unit tests</h2>
 * <p>A bare {@code Bootstrap.bootStrap()} has no Forge registries, so <em>every</em> modded id
 * warns there &mdash; and decodes to air, which is why an offline test cannot validate modded ids
 * at all and must check them another way. The warnings are honest: in that environment those blocks
 * genuinely do not exist.</p>
 *
 * @author Mark Gottschling on Aug 11, 2026
 */
public final class BlockIds {

    private BlockIds() {}

    private static final ResourceLocation AIR = new ResourceLocation("minecraft", "air");

    /**
     * Drop-in replacement for {@code BuiltInRegistries.BLOCK.byNameCodec()}: same lenient result,
     * plus a warning naming any id that is not registered.
     *
     * <p>Not deduplicated, deliberately. Datapack codecs decode on load and reload rather than per
     * chunk, so the volume is bounded by the pack's own content &mdash; and a warning that stops
     * repeating on reload would look like a problem that had been fixed.</p>
     */
    public static final Codec<Block> CODEC =
            ResourceLocation.CODEC.xmap(BlockIds::resolve, BuiltInRegistries.BLOCK::getKey);

    private static Block resolve(ResourceLocation id) {
        // containsKey, not a null check on get(): see the class notes -- get() cannot return null
        // here, which is the entire bug.
        if (!BuiltInRegistries.BLOCK.containsKey(id)) {
            if (!AIR.equals(id)) {
                GottschCore.LOGGER.warn(
                        "block id '{}' is not registered; it resolves to minecraft:air, so whatever"
                        + " rule or palette names it will do nothing. Check the spelling, and that"
                        + " the mod providing it is installed.", id);
            }
            return Blocks.AIR;
        }
        return BuiltInRegistries.BLOCK.get(id);
    }
}
