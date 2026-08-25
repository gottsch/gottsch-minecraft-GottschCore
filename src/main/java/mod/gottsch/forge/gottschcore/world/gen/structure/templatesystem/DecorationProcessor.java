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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import mod.gottsch.forge.gottschcore.GottschCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Decoration that depends on a block's <em>neighbours</em> rather than on the block alone
 * &mdash; the thing neither {@code minecraft:rule} nor {@link AgingProcessor} can express,
 * because both decide one block at a time.
 *
 * <h2>Behaviours</h2>
 * <p>All are off until a datapack gives them a probability and a palette.</p>
 * <ul>
 *   <li><strong>Unsupported</strong> ({@code unsupported}) &mdash; blocks that delete
 *       themselves once nothing is left holding them up. Corbels and ledges jut out of a
 *       wall, so one still hanging in the air after the wall behind it crumbled should go.
 *       Deterministic, no probability; see {@link #hasSupport} for how carefully "nothing"
 *       is defined, since a false positive here deletes authored architecture.</li>
 *   <li><strong>Cobwebs</strong> ({@code cobwebs}) &mdash; air with at least one horizontally
 *       adjacent solid block. Corners and wall faces web up; open floor does not.</li>
 *   <li><strong>Wall growth</strong> ({@code wall_growth}) &mdash; multiface growth (lichen,
 *       mould) in the air <em>beside</em> a <strong>full cube</strong>, attached to that
 *       block's face, and <strong>clustering</strong>: see {@link WallGrowthRule}. Full cube
 *       and not merely solid, because growth clings to a face &mdash; see
 *       {@link #isFullCube}.</li>
 *   <li><strong>Floor / hanging growth</strong> ({@code floor_growth}, {@code hanging_growth})
 *       &mdash; on top of and underneath a block matching {@code dirt}: mushrooms and moss
 *       above, hanging roots below.</li>
 *   <li><strong>Underwater / floating growth</strong> ({@code underwater_growth},
 *       {@code floating_growth}) &mdash; seagrass replacing water that has a solid floor
 *       under it, lily pads on the air above water.</li>
 * </ul>
 *
 * <p>The checks are <strong>independent, not a cascade</strong> (VD's original uses
 * else-if). A dirt block is both "dirt" and "solid", and gets mushrooms above <em>and</em>
 * lichen on its side; the two write to disjoint cells so they don't fight. Where two
 * behaviours do want the same cell, the first one reached in list order takes it.</p>
 *
 * <h2>Only air is ever overwritten</h2>
 * <p>Every behaviour writes into a cell <strong>the piece itself places as air</strong> (the
 * two exceptions being underwater growth, which replaces the piece's own water, and
 * unsupported removal, which clears its own block). Nothing is read from or written to the
 * world outside the block list. So a prefab whose interior is {@code minecraft:structure_void}
 * rather than air decorates to nothing &mdash; a void is neither air nor solid.</p>
 *
 * <h2>Contract &mdash; see {@link LevelIndependentProcessor}</h2>
 * <p>This processor never touches the level, which is what lets a caller hand it a
 * procedurally-built piece's <em>entire</em> block list rather than the current chunk's slice,
 * so the neighbour map is complete. Consequently:</p>
 * <ul>
 *   <li>Solidity comes off the {@link BlockState} ({@code canOcclude}), never through
 *       {@code isSolidRender(BlockGetter, BlockPos)}, which falls through to
 *       {@code getOcclusionShape} &mdash; a world read &mdash; for dynamic-shape blocks.</li>
 *   <li>Every roll is seeded from the <em>target</em> position ({@code Mth.getSeed(pos)}) with
 *       a per-behaviour salt, never from {@code level.getRandom()}. A piece is processed once
 *       per chunk it overlaps, so a position-independent random would decorate the two sides
 *       of a chunk seam differently. (Village Dungeons' {@code SewerDecoProcessorOptimized},
 *       which this is ported from, uses {@code level.getRandom()}.)</li>
 * </ul>
 *
 * <h2>Facing, rotation and mirroring</h2>
 * <p>Processors run <em>before</em> vanilla applies the placement's mirror and rotation
 * ({@code state.mirror(m).rotate(r)} in {@code placeInWorld}), but they see block
 * <em>positions</em> that are already in world space. A face direction derived from two
 * positions is therefore a world direction, and storing it raw would let vanilla rotate it a
 * second time. {@link #storedFacing} applies the inverse transform so the face survives
 * placement at any rotation/mirror. (Procedural pieces are always {@code NONE}/{@code NONE},
 * so this only matters for jigsaw prefabs.)</p>
 *
 * <h2>Registering it</h2>
 * <p>Each consuming mod registers its own {@link StructureProcessorType} under its own
 * namespace &mdash; this class deliberately does not register anything, for the same reason
 * {@link AgingProcessor} doesn't. Wire it up with {@link #codec(Supplier)}, which binds the
 * type the instances will report:</p>
 * <pre>
 * public static final RegistryObject&lt;StructureProcessorType&lt;DecorationProcessor&gt;&gt; DECORATION =
 *         STRUCTURE_PROCESSORS.register("decoration", () -&gt; {
 *             Codec&lt;DecorationProcessor&gt; codec =
 *                     DecorationProcessor.codec(() -&gt; Registration.DECORATION.get());
 *             return () -&gt; codec;
 *         });
 * </pre>
 * <p>See {@link AgingProcessor}'s class doc for why the codec is built inside the supplier
 * and why the type supplier has to qualify the field name.</p>
 *
 * @author Mark Gottschling on Jul 28, 2026
 */
public class DecorationProcessor extends StructureProcessor implements LevelIndependentProcessor {

    /**
     * Per-behaviour salts on the position seed. Without them, two behaviours competing for
     * the same air cell would draw the identical first {@code nextFloat()} there, so the set
     * of cobwebbed cells would be exactly the set of grown cells at equal probability.
     */
    private static final long COBWEB_SALT = 0x9E3779B97F4A7C15L;
    private static final long CORNER_COBWEB_SALT = 0x2545F4914F6CDD1DL;

    private static final long WALL_GROWTH_SALT = 0xC2B2AE3D27D4EB4FL;
    private static final long FLOOR_GROWTH_SALT = 0x165667B19E3779F9L;
    private static final long HANGING_GROWTH_SALT = 0xD1B54A32D192ED03L;
    private static final long UNDERWATER_GROWTH_SALT = 0xBF58476D1CE4E5B9L;
    private static final long FLOATING_GROWTH_SALT = 0x94D049BB133111EBL;

    /**
     * Builds a codec whose instances report {@code type} as their processor type. See
     * the class doc for the registration idiom.
     */
    public static Codec<DecorationProcessor> codec(Supplier<StructureProcessorType<?>> type) {
        return RecordCodecBuilder.create(instance -> instance.group(
            StrictCodecs.strictOptionalFieldOf(DecorationRule.CODEC, "cobwebs", DecorationRule.NONE)
                    .forGetter(processor -> processor.cobwebs),
            StrictCodecs.strictOptionalFieldOf(DecorationRule.CODEC, "corner_cobwebs", DecorationRule.NONE)
                    .forGetter(processor -> processor.cornerCobwebs),
            StrictCodecs.strictOptionalFieldOf(WallGrowthRule.CODEC, "wall_growth", WallGrowthRule.NONE)
                    .forGetter(processor -> processor.wallGrowth),
            StrictCodecs.strictOptionalFieldOf(BlockMatch.CODEC, "dirt", BlockMatch.NONE)
                    .forGetter(processor -> processor.dirt),
            StrictCodecs.strictOptionalFieldOf(DecorationRule.CODEC, "floor_growth", DecorationRule.NONE)
                    .forGetter(processor -> processor.floorGrowth),
            StrictCodecs.strictOptionalFieldOf(DecorationRule.CODEC, "hanging_growth", DecorationRule.NONE)
                    .forGetter(processor -> processor.hangingGrowth),
            StrictCodecs.strictOptionalFieldOf(DecorationRule.CODEC, "underwater_growth", DecorationRule.NONE)
                    .forGetter(processor -> processor.underwaterGrowth),
            StrictCodecs.strictOptionalFieldOf(DecorationRule.CODEC, "floating_growth", DecorationRule.NONE)
                    .forGetter(processor -> processor.floatingGrowth),
            StrictCodecs.strictOptionalFieldOf(BlockMatch.CODEC, "unsupported", BlockMatch.NONE)
                    .forGetter(processor -> processor.unsupported)
        ).apply(instance, (cobwebs, cornerCobwebs, wallGrowth, dirt, floorGrowth, hangingGrowth,
                underwaterGrowth, floatingGrowth, unsupported) -> new DecorationProcessor(
                        type, cobwebs, cornerCobwebs, wallGrowth, dirt, floorGrowth, hangingGrowth,
                        underwaterGrowth, floatingGrowth, unsupported)));
    }

    /** Keeps grown-mob rotation off every other position-seeded stream in this class. */
    private static final long SPAWN_YAW_SALT = 0x6C_0F_9A_11L;

    private final Supplier<StructureProcessorType<?>> type;
    private final DecorationRule cobwebs;
    private final DecorationRule cornerCobwebs;
    private final WallGrowthRule wallGrowth;
    private final BlockMatch dirt;
    private final DecorationRule floorGrowth;
    private final DecorationRule hangingGrowth;
    private final DecorationRule underwaterGrowth;
    private final DecorationRule floatingGrowth;
    private final BlockMatch unsupported;

    /**
     * The pre-{@code corner_cobwebs} signature, kept so adding a behaviour does not break every
     * consumer that builds one directly. This class is shared by several mods and its codec has
     * always been the intended entry point, but tests in particular construct it by hand.
     */
    public DecorationProcessor(Supplier<StructureProcessorType<?>> type,
                               DecorationRule cobwebs, WallGrowthRule wallGrowth, BlockMatch dirt,
                               DecorationRule floorGrowth, DecorationRule hangingGrowth,
                               DecorationRule underwaterGrowth, DecorationRule floatingGrowth,
                               BlockMatch unsupported) {
        this(type, cobwebs, DecorationRule.NONE, wallGrowth, dirt, floorGrowth, hangingGrowth,
                underwaterGrowth, floatingGrowth, unsupported);
    }

    public DecorationProcessor(Supplier<StructureProcessorType<?>> type,
                               DecorationRule cobwebs, DecorationRule cornerCobwebs,
                               WallGrowthRule wallGrowth, BlockMatch dirt,
                               DecorationRule floorGrowth, DecorationRule hangingGrowth,
                               DecorationRule underwaterGrowth, DecorationRule floatingGrowth,
                               BlockMatch unsupported) {
        this.type = type;
        this.cobwebs = cobwebs;
        this.cornerCobwebs = cornerCobwebs;
        this.wallGrowth = wallGrowth;
        this.dirt = dirt;
        this.floorGrowth = floorGrowth;
        this.hangingGrowth = hangingGrowth;
        this.underwaterGrowth = underwaterGrowth;
        this.floatingGrowth = floatingGrowth;
        this.unsupported = unsupported;
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> finalizeProcessing(
            ServerLevelAccessor level, BlockPos origin, BlockPos pos,
            List<StructureTemplate.StructureBlockInfo> originalBlocks,
            List<StructureTemplate.StructureBlockInfo> processedBlocks,
            StructurePlaceSettings settings) {

        if (!hasWork()) {
            return processedBlocks;
        }

        // What the piece occupies. A later duplicate at a position wins, matching the "last
        // write stands" semantics of placing the list in order.
        Map<BlockPos, BlockState> byPos = new HashMap<>(processedBlocks.size());
        for (StructureTemplate.StructureBlockInfo info : processedBlocks) {
            byPos.put(info.pos(), info.state());
        }

        // PHASE 1 -- structural. Decided against the map as it stands, before anything is
        // cleared, so a row of corbels holding each other up collapses one layer per pass
        // rather than unravelling end-to-end in list order.
        Set<BlockPos> cleared = clearUnsupported(processedBlocks, byPos, settings);
        for (BlockPos clearedPos : cleared) {
            byPos.put(clearedPos, Blocks.AIR.defaultBlockState());
        }

        // PHASE 2 -- growth, over the post-clearing geometry, so a cell a corbel just
        // vacated is a candidate like any other air.
        Map<BlockPos, BlockState> replacements = new HashMap<>();
        // Growth placed so far, for the clustering bonus. Kept apart from `replacements` so
        // cobwebs and mushrooms don't seed lichen patches.
        Map<BlockPos, Block> growth = new HashMap<>();
        // Cells that grew an ENTITY rather than a block. Those cells stay air; the mobs are spawned
        // at the end of this method, where the chunk clip is known.
        Map<BlockPos, ResourceLocation> spawns = new HashMap<>();

        for (StructureTemplate.StructureBlockInfo info : processedBlocks) {
            BlockPos blockPos = info.pos();
            // Not info.state(): the block may have been cleared in phase 1, or be an earlier
            // duplicate of a position a later placement overwrites.
            BlockState state = byPos.get(blockPos);

            if (state.isAir()) {
                // Corner webs first: they are the pickier of the two, and a cell that can host one
                // is exactly a cell the plain web would also have taken. Letting the plain web go
                // first would leave the corner behaviour drawing only in the leftovers.
                maybeCornerCobweb(blockPos, byPos, replacements, settings);
                maybeCobweb(blockPos, byPos, replacements);
                continue;
            }
            // Independent checks, not else-if -- see the class doc.
            if (dirt.matches(state)) {
                growInto(blockPos.above(), floorGrowth, FLOOR_GROWTH_SALT, byPos, replacements, spawns);
                growInto(blockPos.below(), hangingGrowth, HANGING_GROWTH_SALT, byPos, replacements, spawns);
            }
            if (state.is(Blocks.WATER)) {
                maybeUnderwaterGrowth(blockPos, byPos, replacements);
                growInto(blockPos.above(), floatingGrowth, FLOATING_GROWTH_SALT, byPos, replacements, spawns);
            }
            // Full cube, not merely solid: growth clings to a FACE, so the block has to have
            // one. See isFullCube -- a stair or a facade passes isSolid and would leave the
            // growth hanging in open air beside it.
            if (isFullCube(state)) {
                maybeWallGrowth(blockPos, byPos, replacements, growth, settings);
            }
        }

        spawnGrown(level, spawns, settings);

        if (replacements.isEmpty() && cleared.isEmpty()) {
            return processedBlocks;
        }

        // Rebuild in place rather than remove-and-append (which is what VD does): the list
        // order is what makes the clustering pass reproducible, and every entry at a changed
        // position must carry the change or a stale duplicate would overwrite it on the way
        // into the world.
        BlockState air = Blocks.AIR.defaultBlockState();
        List<StructureTemplate.StructureBlockInfo> out = new ArrayList<>(processedBlocks.size());
        for (StructureTemplate.StructureBlockInfo info : processedBlocks) {
            BlockState replacement = replacements.get(info.pos());
            if (replacement == null && cleared.contains(info.pos())) {
                replacement = air;
            }
            out.add(replacement == null ? info
                    : new StructureTemplate.StructureBlockInfo(info.pos(), replacement, null));
        }
        return out;
    }

    /** True if any behaviour is configured, so an idle processor costs one check. */
    private boolean hasWork() {
        return cobwebs.isActive() || cornerCobwebs.isActive() || wallGrowth.isActive()
                || floorGrowth.isActive() || hangingGrowth.isActive()
                || underwaterGrowth.isActive() || floatingGrowth.isActive()
                || !unsupported.isEmpty();
    }

    /** Positions holding an {@code unsupported} block with nothing solid horizontally beside it. */
    private Set<BlockPos> clearUnsupported(List<StructureTemplate.StructureBlockInfo> blocks,
                                           Map<BlockPos, BlockState> byPos,
                                           StructurePlaceSettings settings) {
        if (unsupported.isEmpty()) {
            return Set.of();
        }
        Set<BlockPos> cleared = new HashSet<>();
        for (StructureTemplate.StructureBlockInfo info : blocks) {
            if (unsupported.matches(info.state())
                    && !hasSupport(info.state(), info.pos(), byPos, settings)) {
                cleared.add(info.pos());
            }
        }
        return cleared;
    }

    /**
     * Whether anything is holding the block at {@code pos} up.
     *
     * <h3>A block that faces somewhere is held up from BEHIND, and by nothing else</h3>
     * <p>A corbel or a ledge is bracketed onto a wall and juts out from it. It <em>gives</em>
     * support to what sits on top of it; it does not <em>take</em> support from there, nor
     * from the block below, nor from whatever happens to be beside it. So for anything with a
     * {@code facing} property only one neighbour is consulted: the one behind. Lose the wall,
     * lose the corbel &mdash; regardless of what else is around.</p>
     *
     * <p>"Behind" is {@code facing.getOpposite()}: DungeonBlocks' {@code CorbelBlock} and
     * {@code LedgeBlock} both put their backing plate on the face <em>opposite</em> FACING
     * (a north-facing corbel's post occupies z 14&ndash;16, the south of its cell), and their
     * {@code getStateForPlacement} sets FACING to the player's direction reversed. Vanilla's
     * {@code LadderBlock#canSurvive} uses the same convention.</p>
     *
     * <h3>Everything else falls back on "is anything touching it"</h3>
     * <p>A block with no facing has no "behind" to test, so the old rule stands for it: kept
     * unless air is seen all round. Both paths err towards <em>keeping</em> the block, since a
     * false positive deletes architecture somebody authored and a false negative is one ledge
     * that should have fallen and didn't:</p>
     * <ul>
     *   <li><strong>Support is "not air", not "is a full cube."</strong> The behaviour exists
     *       to catch a ledge whose wall <em>crumbled away</em>, and crumbling produces air.
     *       Testing for a full cube instead (which is what VD's {@code isSolidRender} amounts
     *       to) would delete every ledge mounted on a stair, a slab, another ledge, or any
     *       other non-occluding block &mdash; all perfectly good architecture.</li>
     *   <li><strong>A position not in the block list counts as support.</strong> Absent means
     *       "this piece places nothing here", not "here is nothing" &mdash; the wall may
     *       belong to the adjoining piece, or lie outside a prefab's bounds.</li>
     * </ul>
     */
    private static boolean hasSupport(BlockState state, BlockPos pos,
                                      Map<BlockPos, BlockState> byPos,
                                      StructurePlaceSettings settings) {
        Direction facing = facingOf(state);
        if (facing != null) {
            // The stored facing is pre-transform (vanilla rotates the state at write time)
            // while the positions are already world space, so it has to be transformed
            // forward to find the right neighbour. Matters for rotated jigsaw prefabs --
            // stairs_1.nbt places a ledge and is jigsaw-placed at an arbitrary rotation.
            BlockState behind = byPos.get(pos.relative(worldFacing(settings, facing).getOpposite()));
            return behind == null || !behind.isAir();
        }

        BlockState below = byPos.get(pos.below());
        if (below == null || !below.isAir()) {
            return true;
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState neighbour = byPos.get(pos.relative(direction));
            if (neighbour == null || !neighbour.isAir()) {
                return true;
            }
        }
        return false;
    }

    /**
     * The block's {@code facing} direction, or {@code null} if it has none.
     *
     * <p>Looked up <em>by name</em> off the block's own state definition rather than against
     * {@code BlockStateProperties.FACING}, so it covers 4-way {@code HORIZONTAL_FACING} and
     * 6-way {@code FACING} alike &mdash; and any mod's own equivalent. GottschCore's
     * {@code FacingBlock}, which DungeonBlocks' corbels and ledges extend, declares its own
     * {@code EnumProperty.create("facing", Direction.class)} rather than reusing vanilla's.
     * (Those two do compare equal, since {@code Property#equals} is by name and value set, but
     * this doesn't have to rely on that.)</p>
     */
    private static Direction facingOf(BlockState state) {
        for (Map.Entry<Property<?>, Comparable<?>> entry : state.getValues().entrySet()) {
            if (entry.getKey().getName().equals("facing") && entry.getValue() instanceof Direction facing) {
                return facing;
            }
        }
        return null;
    }

    /**
     * The world direction a stored {@code facing} becomes once vanilla applies the placement's
     * mirror and rotation. The exact inverse of {@link #storedFacing}.
     */
    private static Direction worldFacing(StructurePlaceSettings settings, Direction storedFacing) {
        return settings.getRotation().rotate(settings.getMirror().mirror(storedFacing));
    }

    /**
     * The generic "place something in an air cell" behaviour, shared by floor, hanging and
     * floating growth. The roll is keyed on {@code target} &mdash; the cell being written,
     * not the block that triggered the check &mdash; so two dirt blocks sharing a candidate
     * cell can't roll for it twice.
     */
    private void growInto(BlockPos target, DecorationRule rule, long salt,
                          Map<BlockPos, BlockState> byPos, Map<BlockPos, BlockState> replacements,
                          Map<BlockPos, ResourceLocation> spawns) {
        if (!rule.isActive() || replacements.containsKey(target) || spawns.containsKey(target)) {
            return;
        }
        BlockState existing = byPos.get(target);
        if (existing == null || !existing.isAir()) {
            return;
        }
        RandomSource random = RandomSource.create(Mth.getSeed(target) ^ salt);
        if (random.nextFloat() >= rule.probability()) {
            return;
        }
        WeightedGrowth drawn = rule.pick(random);
        if (drawn == null) {
            return;   // all-zero palette; isActive already rules this out, but pick may return null
        }
        if (drawn.isEntity()) {
            // The cell stays AIR and the mob is spawned in finalizeProcessing, once the chunk clip
            // is known. Recorded rather than spawned here because this runs per candidate cell and
            // the clip is a property of the whole pass.
            spawns.put(target, drawn.entity());
            return;
        }
        replacements.put(target, drawn.block().defaultBlockState());
    }

    /**
     * The block a drawn entry names, or {@code null} with a warning when it names an entity.
     *
     * <p>Only the {@code growInto} behaviours &mdash; floor, hanging and floating growth &mdash;
     * can place an entity. The rest of this processor's rules derive a block STATE from geometry:
     * a cobweb picks a corner and a half, wall growth picks a facing. An entity has no such state
     * to carry, so rather than guess at what "a shrieker on a wall, facing east" should mean, an
     * entity in one of those palettes is refused and said so out loud.</p>
     */
    private Block blockOnly(WeightedGrowth drawn, String ruleName) {
        if (drawn == null) {
            return null;
        }
        if (drawn.isEntity()) {
            GottschCore.LOGGER.warn(
                    "the '{}' palette names the entity '{}', but only floor/hanging/floating growth"
                    + " can place one -- this rule derives a block state from geometry. Nothing was"
                    + " placed for that draw.", ruleName, drawn.entity());
            return null;
        }
        return drawn.block();
    }

    /** Webs an air block that has something solid beside it. */
    private void maybeCobweb(BlockPos pos, Map<BlockPos, BlockState> byPos,
                             Map<BlockPos, BlockState> replacements) {
        if (!cobwebs.isActive() || replacements.containsKey(pos)) {
            return;
        }
        RandomSource random = RandomSource.create(Mth.getSeed(pos) ^ COBWEB_SALT);
        if (random.nextFloat() >= cobwebs.probability()) {
            return;
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState neighbour = byPos.get(pos.relative(direction));
            if (neighbour != null && isSolid(neighbour)) {
                Block web = blockOnly(cobwebs.pick(random), "cobwebs");
                if (web != null) {
                    replacements.put(pos, web.defaultBlockState());
                }
                return;
            }
        }
    }

    /**
     * Webs the <strong>junction</strong> between a wall and a floor or ceiling.
     *
     * <h3>Why this is not just another block in the {@code cobwebs} palette</h3>
     * <p>A plain {@code minecraft:cobweb} fills its cell and strings itself across any gap, so
     * "air with something solid beside it" is the whole requirement. A <em>corner</em> web is
     * modelled as a triangular sheet gathering into the angle where two surfaces meet, and tapering
     * away from it. Put one halfway up a bare wall and it has nothing to gather into &mdash; it
     * reads as a sheet hanging in mid-air, which is worse than the flat web it replaced. It needs
     * <strong>two</strong> perpendicular surfaces, and that is a different question from the one
     * {@link #maybeCobweb} asks, not a different palette entry.</p>
     *
     * <h3>Orientation</h3>
     * <p>{@code facing} names the direction the block points <em>away</em> from the surface it is
     * mounted on (its support is behind {@code facing.getOpposite()}), so a <strong>horizontal</strong>
     * {@code facing} pointing out of the wall stands the web's sheet against that wall. That is the
     * same for both junctions.
     *
     * <p>Which junction it fills is <strong>{@code half}</strong>: {@code TOP} gathers the web where
     * the wall meets the ceiling, {@code BOTTOM} where it meets the floor. <strong>It has to be a
     * property and cannot be a rotation</strong> &mdash; learned the hard way, in game, 2026-08-10.
     * A vertical {@code facing} tips the sheet about the X axis and lays it flat on the floor
     * instead of standing it against the wall; and the vertical mirror that would be wanted instead
     * is expressible neither in a blockstate (which rotates only about x and y) nor by vanilla's
     * placement rotation. A web with no {@code half} can only offer the one look, so it is used for
     * ceiling junctions and skipped at floor-only ones rather than placed wrong.
     *
     * <p>The chosen state goes through {@link #storedFor}, without which the web points the wrong
     * way in any piece vanilla mirrors or rotates. Nothing here names a block: a web either carries
     * the properties or it does not.</p>
     */
    private void maybeCornerCobweb(BlockPos pos, Map<BlockPos, BlockState> byPos,
                                   Map<BlockPos, BlockState> replacements,
                                   StructurePlaceSettings settings) {
        if (!cornerCobwebs.isActive() || replacements.containsKey(pos)) {
            return;
        }
        RandomSource random = RandomSource.create(Mth.getSeed(pos) ^ CORNER_COBWEB_SALT);
        if (random.nextFloat() >= cornerCobwebs.probability()) {
            return;
        }

        // A corner web lies against its surfaces, so both have to be full cubes -- isSolid is true
        // of stairs, slabs and facades, none of which present a face to gather against.
        boolean ceiling = isFullCubeAt(pos.above(), byPos);
        boolean floor = isFullCubeAt(pos.below(), byPos);
        if (!ceiling && !floor) {
            return;
        }
        List<Direction> walls = new ArrayList<>(4);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (isFullCubeAt(pos.relative(direction), byPos)) {
                walls.add(direction);
            }
        }
        if (walls.isEmpty()) {
            return;
        }

        Block cornerBlock = blockOnly(cornerCobwebs.pick(random), "corner_cobwebs");
        if (cornerBlock == null) {
            return;
        }
        BlockState web = cornerBlock.defaultBlockState();
        Direction wall = walls.get(random.nextInt(walls.size()));

        // The facing is the same for both junctions -- horizontal, pointing away from the wall, so
        // the web's sheet stands against it. Which junction it fills is `half`, NOT a rotation:
        // a vertical facing would tip the sheet flat onto the floor, and no rotation can mirror the
        // model vertically while leaving it on the same wall.
        BlockState wanted = withFacing(web, wall.getOpposite());
        if (wanted == null) {
            return;
        }

        boolean canFlip = wanted.hasProperty(BlockStateProperties.HALF);
        boolean useCeiling;
        if (ceiling && floor) {
            // Both available: alternate, so a run of webs along one wall does not all gather at the
            // same height. A web that cannot flip has only the one look to offer.
            useCeiling = !canFlip || random.nextBoolean();
        } else if (ceiling) {
            useCeiling = true;
        } else if (canFlip) {
            useCeiling = false;
        } else {
            // Floor only, and this web cannot express that junction -- draw nothing rather than a
            // ceiling web hanging over an open cell.
            return;
        }
        if (canFlip) {
            wanted = wanted.setValue(BlockStateProperties.HALF, useCeiling ? Half.TOP : Half.BOTTOM);
        }
        BlockState stored = storedFor(wanted, settings);
        if (stored == null) {
            return;
        }
        replacements.put(pos, stored);
    }

    /** Sets {@code facing} if the block has one and the value is legal, else null. */
    private static BlockState withFacing(BlockState state, Direction facing) {
        DirectionProperty property = facingPropertyOf(state);
        if (property == null || !property.getPossibleValues().contains(facing)) {
            return null;
        }
        return state.setValue(property, facing);
    }

    /**
     * The state to <strong>store</strong> so that vanilla's {@code state.mirror(m).rotate(r)} at
     * write time produces {@code wanted}. Null when no state does.
     *
     * <h3>Why this searches instead of inverting the transform</h3>
     * <p>{@link #storedFacing} inverts both the mirror and the rotation, which is right for a
     * multiface growth because {@code MultifaceBlock} implements both. **Whether a block
     * transforms a given property at all is up to the block</strong>, and for these webs it is neither
     * uniform nor complete: GottschCore's own {@code FacingBlock} overrides {@code rotate} but not
     * {@code mirror}, and a mod's custom {@code rotation} property is transformed by nothing.
     * Inverting a transform the block does not apply is just as wrong as failing to invert one it
     * does &mdash; and this class cannot know which.
     *
     * <p>So it asks instead of assuming: run every state the block has through the same call
     * vanilla will make, and keep the one that lands on the target. The state space is a handful of
     * variants and this only runs on cells that already passed a probability roll.</p>
     */
    private static BlockState storedFor(BlockState wanted, StructurePlaceSettings settings) {
        Mirror mirror = settings.getMirror();
        Rotation rotation = settings.getRotation();
        if (mirror == Mirror.NONE && rotation == Rotation.NONE) {
            return wanted;
        }
        for (BlockState candidate : wanted.getBlock().getStateDefinition().getPossibleStates()) {
            if (candidate.mirror(mirror).rotate(rotation).equals(wanted)) {
                return candidate;
            }
        }
        return null;
    }

    private static boolean isFullCubeAt(BlockPos pos, Map<BlockPos, BlockState> byPos) {
        BlockState state = byPos.get(pos);
        return state != null && isFullCube(state);
    }

    /**
     * The block's {@code facing} property, or null if it has none.
     *
     * <p>Two distinct properties share the name: the six-way {@link BlockStateProperties#FACING}
     * and the four-way {@link BlockStateProperties#HORIZONTAL_FACING}. They are different objects,
     * so {@code hasProperty} on one says nothing about the other and both have to be tried.</p>
     */
    private static DirectionProperty facingPropertyOf(BlockState state) {
        if (state.hasProperty(BlockStateProperties.FACING)) {
            return BlockStateProperties.FACING;
        }
        return state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
                ? BlockStateProperties.HORIZONTAL_FACING : null;
    }

    /** Replaces water standing on a solid floor. Unlike the rest, this overwrites water, not air. */
    private void maybeUnderwaterGrowth(BlockPos pos, Map<BlockPos, BlockState> byPos,
                                       Map<BlockPos, BlockState> replacements) {
        if (!underwaterGrowth.isActive() || replacements.containsKey(pos)) {
            return;
        }
        BlockState floor = byPos.get(pos.below());
        if (floor == null || !isSolid(floor)) {
            return;
        }
        RandomSource random = RandomSource.create(Mth.getSeed(pos) ^ UNDERWATER_GROWTH_SALT);
        if (random.nextFloat() >= underwaterGrowth.probability()) {
            return;
        }
        Block weed = blockOnly(underwaterGrowth.pick(random), "underwater_growth");
        if (weed == null) {
            return;
        }
        replacements.put(pos, weed.defaultBlockState());
    }

    /**
     * Grows on the faces of the solid block at {@code wallPos}, writing into the adjacent air.
     * Called with the <em>wall</em>, because that is what makes a face direction available;
     * the roll is keyed on the <em>air</em> position being written.
     */
    private void maybeWallGrowth(BlockPos wallPos, Map<BlockPos, BlockState> byPos,
                                 Map<BlockPos, BlockState> replacements,
                                 Map<BlockPos, Block> growth, StructurePlaceSettings settings) {
        if (!wallGrowth.isActive()) {
            return;
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos airPos = wallPos.relative(direction);
            BlockState air = byPos.get(airPos);
            if (air == null || !air.isAir() || replacements.containsKey(airPos)) {
                continue;
            }

            // Clustering: each touching growth block raises the chance, and the first one
            // found decides the species, so a patch spreads as one organism.
            Block species = null;
            int adjacent = 0;
            for (Direction side : Direction.values()) {
                Block neighbour = growth.get(airPos.relative(side));
                if (neighbour != null) {
                    if (species == null) {
                        species = neighbour;
                    }
                    adjacent++;
                }
            }

            RandomSource random = RandomSource.create(Mth.getSeed(airPos) ^ WALL_GROWTH_SALT);
            if (random.nextFloat() >= wallGrowth.chanceWith(adjacent)) {
                continue;
            }
            if (species == null) {
                species = blockOnly(wallGrowth.pick(random), "wall_growth");
            }

            // The growth sits in the air block and clings to the wall, so its face is the
            // direction from the air TOWARDS the wall -- the opposite of the one we walked.
            BooleanProperty face = MultifaceBlock.getFaceProperty(
                    storedFacing(settings, direction.getOpposite()));
            BlockState state = species.defaultBlockState();
            // A non-multiface growth block (moss carpet, say) simply has no face to set.
            replacements.put(airPos, state.hasProperty(face) ? state.setValue(face, true) : state);
            growth.put(airPos, species);
        }
    }

    /**
     * The direction to <em>store</em> so that vanilla's {@code state.mirror(m).rotate(r)} at
     * write time yields {@code worldFacing}. Mirroring a direction is an involution, so the
     * inverse of {@code rotate(mirror(d))} is {@code mirror(rotate⁻¹(d))}.
     */
    private static Direction storedFacing(StructurePlaceSettings settings, Direction worldFacing) {
        return settings.getMirror().mirror(inverse(settings.getRotation()).rotate(worldFacing));
    }

    private static Rotation inverse(Rotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_90 -> Rotation.COUNTERCLOCKWISE_90;
            case COUNTERCLOCKWISE_90 -> Rotation.CLOCKWISE_90;
            // NONE and CLOCKWISE_180 are their own inverses.
            default -> rotation;
        };
    }

    /**
     * "Something is there" &mdash; anything that isn't air or a fluid. Used where the block
     * only has to <em>exist</em>: what a cobweb strings itself across, what water is standing
     * on. A cobweb between two stair treads is fine.
     *
     * <p>{@code canOcclude} is a plain field on the state, so this costs nothing and reads
     * nothing. Note it is <strong>not</strong> a shape test &mdash; see {@link #isFullCube}.</p>
     */
    private static boolean isSolid(BlockState state) {
        return state.canOcclude() && state.getFluidState().isEmpty();
    }

    /**
     * Whether the block fills its cell, so growth clinging to its face has something visible
     * to cling to.
     *
     * <p><strong>{@code canOcclude()} is not this test.</strong> It is a light-occlusion flag
     * and it is {@code true} for stairs, slabs, walls, fences and DungeonBlocks' facade,
     * pillar and corbel shapes &mdash; none of which fill their cell. Growth placed against
     * one of those hangs in the open air beside it with nothing behind it.</p>
     *
     * <p>{@code isSolidRender} is the real shape test ({@code isShapeFullBlock} of the
     * occlusion shape), and it answers from the state's shape cache, which every block has
     * unless it declares {@code dynamicShape()}. The handful that do fall through to
     * {@code getOcclusionShape(getter, pos)} &mdash; hence {@link EmptyBlockGetter}, never the
     * real level: this processor is not allowed to read the world, and an empty getter makes
     * that fallback harmless instead of illegal.</p>
     */
    private static boolean isFullCube(BlockState state) {
        return state.isSolidRender(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return type.get();
    }

    /**
     * Spawns the mobs the growth pass drew, clipped to the chunk this placement is writing.
     *
     * <h2>The clip is the whole reason this can work at all</h2>
     * <p>{@code StructureTemplate.processBlockInfos} is <strong>not</strong> chunk-clipped &mdash;
     * it hands {@code finalizeProcessing} every block of the piece &mdash; and a piece's placement
     * runs once per chunk it overlaps. Blocks survive that because each run rewrites the same states
     * and the writes are clipped downstream. {@code addFreshEntity} has no such idempotence, so an
     * unclipped spawn would add one copy of every mob per overlapping chunk.</p>
     *
     * <p>{@code StructurePlaceSettings#getBoundingBox} is the chunk box during a piece placement
     * ({@code SinglePoolElement} sets it from the box {@code postProcess} was handed), so testing
     * each cell against it gives every spawn exactly one owning chunk &mdash; the same guard
     * vanilla's own {@code StructureTemplate.placeEntities} applies two methods further down. The
     * box is <strong>null unless someone set it</strong>, which is the honest signal for a
     * single-shot caller (a command, a test) that has no chunk to clip to; there, spawn everything.</p>
     *
     * <h2>Rotation</h2>
     * <p>Derived from the cell position, not drawn from a stream: this method runs once per chunk
     * and must produce the same answer each time, or the clip above stops being a correct
     * exactly-once filter. Same reasoning as the growth draw itself.</p>
     *
     * <p>An unresolvable entity id warns and is skipped, matching {@link BlockIds}'
     * degrade-don't-abort convention: losing one mob is not worth killing a chunk over.</p>
     */
    private void spawnGrown(ServerLevelAccessor level, Map<BlockPos, ResourceLocation> spawns,
                            StructurePlaceSettings settings) {
        if (spawns.isEmpty()) {
            return;
        }
        BoundingBox box = settings.getBoundingBox();
        for (Map.Entry<BlockPos, ResourceLocation> spawn : spawns.entrySet()) {
            BlockPos pos = spawn.getKey();
            if (box != null && !box.isInside(pos)) {
                continue;   // another chunk's placement owns this one
            }
            EntityType<?> type = EntityType.byString(spawn.getValue().toString()).orElse(null);
            if (type == null) {
                GottschCore.LOGGER.warn(
                        "growth names the entity '{}', which is not registered; nothing was spawned"
                        + " at {}. Check the spelling, and that the mod providing it is installed.",
                        spawn.getValue(), pos.toShortString());
                continue;
            }
            Entity entity = type.create(level.getLevel());
            if (entity == null) {
                GottschCore.LOGGER.warn("entity type '{}' would not construct at {}",
                        spawn.getValue(), pos.toShortString());
                continue;
            }
            RandomSource random = RandomSource.create(Mth.getSeed(pos) ^ SPAWN_YAW_SALT);
            entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D,
                    random.nextFloat() * 360.0F, 0.0F);
            if (entity instanceof Mob mob) {
                // Without this a mob arrives half-built: vanilla's difficulty setup and any
                // spawn-time work its own class does (rooting itself, reading a config) never run.
                mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos),
                        MobSpawnType.STRUCTURE, null, null);
            }
            level.addFreshEntity(entity);
        }
    }
}
