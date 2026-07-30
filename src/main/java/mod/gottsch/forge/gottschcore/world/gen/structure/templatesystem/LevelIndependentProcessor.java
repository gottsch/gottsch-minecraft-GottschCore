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

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;

/**
 * Marker for a {@link StructureProcessor} that <strong>never touches the level it is
 * handed</strong> &mdash; it decides purely from the block list, the block states in it,
 * and their positions.
 *
 * <p>That single property is what licenses a caller to run a processor over a
 * procedurally-built piece's <em>whole</em> block list instead of just the part inside the
 * chunk currently being generated. Reading the world outside the active
 * {@code WorldGenRegion} is illegal, so a processor that reads must be clipped to it; one
 * that doesn't, needn't be. A caller that runs processors this way is expected to split them
 * into two passes on this marker &mdash; unclipped for the marked ones, chunk-clipped for
 * the rest.</p>
 *
 * <p>Vanilla's own placement path never needs this: it hands processors a real level and a
 * fully materialised template. The marker exists for callers that generate blocks
 * procedurally, chunk by chunk, and want the same authored processor list to apply to them.</p>
 *
 * <h2>Why anything wants to be unclipped</h2>
 * <ul>
 *   <li><strong>Neighbour-aware processors must be.</strong> A neighbour map built from one
 *       chunk's slice is missing everything across the seam, so the piece would decorate
 *       differently on each side of it. {@link DecorationProcessor} is in this category.</li>
 *   <li><strong>Per-block processors benefit.</strong> Being unclipped puts them in the same
 *       pass as the neighbour-aware ones, which preserves the order they were authored in.
 *       {@link AgingProcessor} is marked for this reason: it crumbles some blocks to air and
 *       turns others to dirt, and decoration should see that, the way it does for a
 *       jigsaw-placed prefab.</li>
 * </ul>
 *
 * <h2>What implementing this commits you to</h2>
 * <ul>
 *   <li>No level access at all. In particular <em>not</em>
 *       {@code BlockState#isSolidRender(BlockGetter, BlockPos)}, which looks like a pure state
 *       query but falls through to {@code getOcclusionShape(level, pos)} for blocks with a
 *       dynamic shape. Use {@code canOcclude()}.</li>
 *   <li>Positional determinism: seed from {@code Mth.getSeed(pos)}, never
 *       {@code level.getRandom()} or {@code settings.getRandom()}. A piece is processed once
 *       per chunk it overlaps, and a block on a chunk seam must resolve identically every
 *       time.</li>
 * </ul>
 *
 * <p>An <em>unmarked</em> processor that nonetheless decides from the whole block list &mdash;
 * {@code minecraft:capped}, say &mdash; is in neither category and belongs in no such
 * processor list: it would land in the clipped pass and apply its cap per chunk.</p>
 *
 * @author Mark Gottschling on Jul 28, 2026
 */
public interface LevelIndependentProcessor {
}
