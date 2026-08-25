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
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mod.gottsch.forge.gottschcore.json.StrictCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * One entry of a decoration palette: what grows, and how often relative to its siblings.
 *
 * <h2>A palette entry may be a block OR an entity</h2>
 * <p>Some things that read as scenery are mobs. GMM's Shrieker and Violet Fungus are rooted plant
 * monsters &mdash; they never move, and they belong on a patch of decayed dirt exactly as a mushroom
 * does. Making the palette able to name one directly is what lets a datapack treat them as what they
 * look like:</p>
 *
 * <pre>
 * "blocks": [
 *   "minecraft:brown_mushroom",
 *   { "block":  "minecraft:dead_bush", "weight": 3 },
 *   { "entity": "dungeons2:shrieker",  "weight": 1 }
 * ]
 * </pre>
 *
 * <p>An entry names <strong>exactly one</strong> of {@code block} or {@code entity}; both or neither
 * is a load error rather than a guess. A bare id is a block at weight 1, so every palette written
 * before any of this keeps working unchanged, and encoding sends a weight-1 block back out as a bare
 * id so a round trip does not rewrite a pack into the verbose form.</p>
 *
 * <h2>Why the entity does not need a marker block</h2>
 * <p>It is tempting to think a processor cannot make an entity, since {@code processBlock} is handed
 * only a {@code LevelReader} and returns a {@code BlockState}. But {@code finalizeProcessing} &mdash;
 * the neighbour-aware pass {@link DecorationProcessor} already runs in &mdash; receives a real
 * {@code ServerLevelAccessor}, and the {@code StructurePlaceSettings} it is given carry the chunk
 * bounding box, which is the same clip vanilla's own {@code StructureTemplate.placeEntities} uses to
 * spawn each entity exactly once across the chunks a piece spans. So the processor can both create
 * the entity and dedupe it, and a marker block standing in for one is a workaround for a problem
 * that does not exist.</p>
 *
 * <h2>An unweighted palette draws EXACTLY as it did before</h2>
 * <p>{@link #pick} rolls {@code nextInt(totalWeight)} and walks the cumulative weights. When every
 * weight is 1, {@code totalWeight == size} and the walk lands on the index the roll named &mdash; the
 * same result, from the same single draw, as the {@code blocks.get(random.nextInt(size))} this
 * replaced. These palettes are drawn with a position-seeded random during worldgen, so a draw that
 * consumed a different amount of randomness would silently re-decorate every existing world. Pinned
 * by {@code WeightedGrowthTest}.</p>
 *
 * <h2>Weight 0 is allowed and means never</h2>
 * <p>It lets a pack park an entry without deleting it. A palette whose weights are all 0 is
 * {@code totalWeight 0} and its rule reports itself inactive rather than drawing at random. Negative
 * weights are rejected at decode, because they would corrupt the cumulative walk into selecting an
 * arbitrary entry rather than failing.</p>
 *
 * @author Mark Gottschling on Aug 19, 2026
 */
public record WeightedGrowth(@Nullable Block block, @Nullable ResourceLocation entity, int weight) {

    /**
     * The explicit object form. {@code weight} is <strong>required</strong> here, and that is load
     * bearing rather than strict for its own sake.
     *
     * <p>DFU's {@code optionalFieldOf(name, default)} does not distinguish "absent" from "present
     * and invalid": {@code OptionalFieldCodec} turns a failed decode into an empty optional, so the
     * default is substituted either way. With a default of 1, {@code "weight": -3} decoded
     * <em>silently as weight 1</em> rather than failing the file, and only a test caught it. Making
     * the field required removes the swallowing path entirely, and an object that does not state a
     * weight has no reason to be an object at all: a bare id already means a block at weight 1.</p>
     *
     * <p>{@code block} and {@code entity} use {@link StrictCodecs} for the same reason &mdash; a
     * malformed one must fail rather than quietly read as absent and turn the entry into the other
     * kind.</p>
     */
    private static final Codec<WeightedGrowth> OBJECT = RecordCodecBuilder.<WeightedGrowth>create(
                    instance -> instance.group(
                            StrictCodecs.strictOptionalFieldOf(BlockIds.CODEC, "block")
                                    .forGetter(entry -> Optional.ofNullable(entry.block())),
                            StrictCodecs.strictOptionalFieldOf(ResourceLocation.CODEC, "entity")
                                    .forGetter(entry -> Optional.ofNullable(entry.entity())),
                            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("weight")
                                    .forGetter(WeightedGrowth::weight)
                    ).apply(instance, (block, entity, weight) ->
                            new WeightedGrowth(block.orElse(null), entity.orElse(null), weight)))
            .flatXmap(WeightedGrowth::exactlyOne, WeightedGrowth::exactlyOne);

    /**
     * Bare id or object, either way round.
     *
     * <p>The two forms are a string and a map, so {@code either} can never mistake one for the
     * other &mdash; this is not the ambiguous kind of union.</p>
     */
    public static final Codec<WeightedGrowth> CODEC =
            Codec.either(BlockIds.CODEC, OBJECT).xmap(
                    either -> either.map(WeightedGrowth::of, entry -> entry),
                    entry -> entry.isBlock() && entry.weight() == 1
                            ? Either.left(entry.block())
                            : Either.right(entry));

    /** A palette. */
    public static final Codec<List<WeightedGrowth>> LIST_CODEC = CODEC.listOf();

    // No validation in the canonical constructor, and that is required rather than lax.
    //
    // A DFU error result can carry a PARTIAL value, and RecordCodecBuilder's `ap2` still calls the
    // record constructor to build that partial -- even when a field codec has already failed. So a
    // constructor that throws on a bad value turns what should be a clean DataResult error into an
    // IllegalArgumentException escaping the decode, which during a datapack reload is a crash
    // rather than a reported bad file. `exactlyOne` and the codec's own range enforce it instead.

    /** A block entry at weight 1 &mdash; what a bare id in the JSON means. */
    public static WeightedGrowth of(Block block) {
        return new WeightedGrowth(block, null, 1);
    }

    public static WeightedGrowth of(Block block, int weight) {
        return new WeightedGrowth(block, null, weight);
    }

    public static WeightedGrowth ofEntity(ResourceLocation entity, int weight) {
        return new WeightedGrowth(null, entity, weight);
    }

    public boolean isBlock() {
        return block != null;
    }

    public boolean isEntity() {
        return entity != null;
    }

    /** Wraps plain blocks at weight 1, for the callers that build a palette in code. */
    public static List<WeightedGrowth> unweighted(List<Block> blocks) {
        return blocks.stream().map(WeightedGrowth::of).toList();
    }

    /** Total of a palette's weights; {@code 0} means nothing in it can be drawn. */
    public static int totalWeight(List<WeightedGrowth> palette) {
        int total = 0;
        for (WeightedGrowth entry : palette) {
            total += entry.weight();
        }
        return total;
    }

    /**
     * Weighted draw. Consumes exactly one {@code nextInt}, whatever the palette looks like &mdash;
     * see the class notes for why the draw count is load-bearing.
     *
     * @return the drawn entry, or {@code null} when the palette is empty or all-zero. A caller that
     *         has checked its rule is active cannot see null.
     */
    @Nullable
    public static WeightedGrowth pick(List<WeightedGrowth> palette, RandomSource random) {
        int total = totalWeight(palette);
        if (total <= 0) {
            return null;
        }
        int roll = random.nextInt(total);
        for (WeightedGrowth entry : palette) {
            roll -= entry.weight();
            if (roll < 0) {
                return entry;
            }
        }
        // Unreachable: roll < total and the weights sum to total. Kept over an exception because
        // this runs inside worldgen, where the last entry is a better outcome than a dead chunk.
        return palette.get(palette.size() - 1);
    }

    /**
     * Exactly one of {@code block} / {@code entity}, as a {@link DataResult} rather than a throw.
     *
     * <p>Neither means the entry does nothing and the palette silently shrinks; both means the
     * author expressed two intentions and the code would have to pick one. Either way a guess is
     * worse than a named error in the log.</p>
     */
    private static DataResult<WeightedGrowth> exactlyOne(WeightedGrowth entry) {
        if (entry.isBlock() && entry.isEntity()) {
            return DataResult.error(() -> "a growth entry names both a block ("
                    + entry.block() + ") and an entity (" + entry.entity() + "); it must name one");
        }
        if (!entry.isBlock() && !entry.isEntity()) {
            return DataResult.error(() -> "a growth entry names neither a block nor an entity");
        }
        return DataResult.success(entry);
    }
}
