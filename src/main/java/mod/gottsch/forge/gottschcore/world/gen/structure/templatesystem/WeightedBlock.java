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

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * One entry of a decoration palette: a block, and how often it is drawn relative to its siblings.
 *
 * <h2>The JSON accepts both forms, and that is the point</h2>
 * <p>A palette entry may be a bare id or an object with a weight, in the same list:</p>
 *
 * <pre>
 * "blocks": [
 *   "minecraft:brown_mushroom",
 *   { "block": "minecraft:dead_bush", "weight": 3 }
 * ]
 * </pre>
 *
 * <p>A bare id means {@code weight 1}, so <strong>every palette written before weights existed
 * keeps working unchanged</strong> and nothing has to be migrated. Encoding goes back the way it
 * came: an entry at weight 1 writes as a bare string, so a decode/encode round trip does not
 * rewrite a pack into the verbose form. The object form must state a weight &mdash; see
 * {@link #OBJECT} for why that is not gratuitous strictness.</p>
 *
 * <h2>An unweighted palette draws EXACTLY as it did before</h2>
 * <p>{@link #pick} rolls {@code nextInt(totalWeight)} and walks the cumulative weights. When every
 * weight is 1, {@code totalWeight == size} and the walk lands on the index the roll named &mdash;
 * the same result, from the same single draw, as the {@code blocks.get(random.nextInt(size))} this
 * replaced. That matters more than it looks: these palettes are drawn with a position-seeded random
 * during worldgen, so a draw that consumed a different amount of randomness would silently
 * re-decorate every existing world. Pinned by {@code WeightedBlockTest}.</p>
 *
 * <h2>Weight 0 is allowed and means never</h2>
 * <p>It lets a pack park an entry without deleting it. A palette whose weights are all 0 is
 * {@code totalWeight 0} and its rule reports itself inactive, rather than drawing something at
 * random. Negative weights are rejected at decode, because they would corrupt the cumulative walk
 * into selecting an arbitrary entry rather than failing.</p>
 *
 * @author Mark Gottschling on Aug 19, 2026
 */
public record WeightedBlock(Block block, int weight) {

    /**
     * The explicit object form. {@code weight} is <strong>required</strong> here, and that is load
     * bearing rather than strict for its own sake.
     *
     * <p>DFU's {@code optionalFieldOf(name, default)} does not distinguish "absent" from "present
     * and invalid": {@code OptionalFieldCodec} turns a failed decode into an empty optional, so the
     * default is substituted either way. With a default of 1, {@code "weight": -3} decoded
     * <em>silently as weight 1</em> rather than failing the file &mdash; the same error-swallowing a
     * closed schema exists to prevent, and only a test caught it. Making the field required removes
     * the swallowing path entirely, and an object that does not state a weight has no reason to be
     * written as an object at all: a bare id already means weight 1.</p>
     */
    private static final Codec<WeightedBlock> OBJECT = RecordCodecBuilder.create(instance -> instance.group(
            BlockIds.CODEC.fieldOf("block").forGetter(WeightedBlock::block),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("weight").forGetter(WeightedBlock::weight)
    ).apply(instance, WeightedBlock::new));

    /**
     * Bare id or object, either way round.
     *
     * <p>The two forms are a string and a map, so {@code either} can never mistake one for the
     * other &mdash; this is not the ambiguous kind of union.</p>
     */
    public static final Codec<WeightedBlock> CODEC =
            Codec.either(BlockIds.CODEC, OBJECT).xmap(
                    either -> either.map(block -> new WeightedBlock(block, 1), entry -> entry),
                    entry -> entry.weight() == 1
                            ? Either.left(entry.block())
                            : Either.right(entry));

    /** A palette. */
    public static final Codec<List<WeightedBlock>> LIST_CODEC = CODEC.listOf();

    // No validation in the canonical constructor, and that is required rather than lax.
    //
    // A DFU error result can carry a PARTIAL value, and RecordCodecBuilder's `ap2` still calls the
    // record constructor to build that partial -- even when a field codec has already failed. So a
    // constructor that throws on a bad value turns what should be a clean DataResult error into an
    // IllegalArgumentException escaping the decode, which during a datapack reload is a crash
    // rather than a reported bad file. The range is enforced by `OBJECT` instead, which is the only
    // place untrusted input enters; `pick` is total for any non-negative palette.

    /** Wraps plain blocks at weight 1, for the callers that build a palette in code. */
    public static List<WeightedBlock> unweighted(List<Block> blocks) {
        return blocks.stream().map(block -> new WeightedBlock(block, 1)).toList();
    }

    /** Total of a palette's weights; {@code 0} means nothing in it can be drawn. */
    public static int totalWeight(List<WeightedBlock> palette) {
        int total = 0;
        for (WeightedBlock entry : palette) {
            total += entry.weight();
        }
        return total;
    }

    /**
     * Weighted draw. Consumes exactly one {@code nextInt}, whatever the palette looks like &mdash;
     * see the class notes for why the draw count is load-bearing.
     *
     * @return the drawn block, or {@code null} when the palette is empty or all-zero. A caller that
     *         has checked its rule is active cannot see null.
     */
    public static Block pick(List<WeightedBlock> palette, RandomSource random) {
        int total = totalWeight(palette);
        if (total <= 0) {
            return null;
        }
        int roll = random.nextInt(total);
        for (WeightedBlock entry : palette) {
            roll -= entry.weight();
            if (roll < 0) {
                return entry.block();
            }
        }
        // Unreachable: roll < total and the weights sum to total. Kept over an exception because
        // this runs inside worldgen, where the last entry is a better outcome than a dead chunk.
        return palette.get(palette.size() - 1).block();
    }
}
