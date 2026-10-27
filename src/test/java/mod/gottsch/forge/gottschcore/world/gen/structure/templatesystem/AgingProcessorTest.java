/*
 * This file is part of  GottschCore.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
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

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Covers the two things {@link AgingProcessor} exists to do that a vanilla
 * {@code minecraft:rule} processor cannot: carry the source block's state properties
 * onto the replacement, and walk a multi-stage decay chain.
 *
 * <p>{@code processBlock} never touches the {@code LevelReader} it is handed (unlike
 * {@code RuleProcessor}, whose {@code location_predicate} reads the existing world
 * block), so these run against a {@code null} level. That independence is also what
 * makes the processor safe to run over procedurally-built blocks before they exist in
 * the world.</p>
 *
 * @author Mark Gottschling on Jul 27, 2026
 */
class AgingProcessorTest {

    private static final BlockPos POS = new BlockPos(103, 41, -77);
    private static final StructurePlaceSettings SETTINGS = new StructurePlaceSettings();

    /**
     * Registration is a consuming mod's job, so no real {@link StructureProcessorType}
     * exists here. Nothing under test calls {@code getType()}, which is only used when
     * serializing a processor back out.
     */
    private static final Supplier<StructureProcessorType<?>> NO_TYPE = () -> null;

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    /** An always-decaying single-stage chain, so property handling is what's under test. */
    private static AgingProcessor certain(Block from, Block to) {
        return new AgingProcessor(NO_TYPE, 1,
                List.of(new AgingRule(from, List.of(new AgingStage(to, 1.0)))));
    }

    private static BlockState process(AgingProcessor processor, BlockState state, BlockPos pos) {
        StructureTemplate.StructureBlockInfo info =
                new StructureTemplate.StructureBlockInfo(pos, state, null);
        StructureTemplate.StructureBlockInfo out =
                processor.processBlock(null, BlockPos.ZERO, pos, info, info, SETTINGS);
        return out.state();
    }

    @Test
    void stairsKeepFacingHalfAndShape() {
        // The headline case. A vanilla ProcessorRule would emit
        // mossy_stone_brick_stairs' DEFAULT state here, silently spinning the stair
        // north-facing and bottom-half.
        BlockState original = Blocks.STONE_BRICK_STAIRS.defaultBlockState()
                .setValue(StairBlock.FACING, Direction.WEST)
                .setValue(StairBlock.HALF, Half.TOP)
                .setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT);

        BlockState aged = process(
                certain(Blocks.STONE_BRICK_STAIRS, Blocks.MOSSY_STONE_BRICK_STAIRS), original, POS);

        assertEquals(Blocks.MOSSY_STONE_BRICK_STAIRS, aged.getBlock());
        assertEquals(Direction.WEST, aged.getValue(StairBlock.FACING));
        assertEquals(Half.TOP, aged.getValue(StairBlock.HALF));
        assertEquals(StairsShape.OUTER_LEFT, aged.getValue(StairBlock.SHAPE));
    }

    @Test
    void waterloggingSurvivesAging() {
        // A per-block-class implementation is liable to hardcode WATERLOGGED=false on
        // stairs, which would drain a flooded stair on decay.
        BlockState original = Blocks.STONE_BRICK_STAIRS.defaultBlockState()
                .setValue(BlockStateProperties.WATERLOGGED, true);

        BlockState aged = process(
                certain(Blocks.STONE_BRICK_STAIRS, Blocks.MOSSY_STONE_BRICK_STAIRS), original, POS);

        assertEquals(Boolean.TRUE, aged.getValue(BlockStateProperties.WATERLOGGED));
    }

    @Test
    void wallsKeepEveryConnectionSide() {
        // Walls are why a per-block-class approach doesn't scale: five connection
        // properties plus waterlogged. The generic property copy handles them with no
        // wall-specific code at all.
        BlockState original = Blocks.STONE_BRICK_WALL.defaultBlockState()
                .setValue(WallBlock.NORTH_WALL, WallSide.TALL)
                .setValue(WallBlock.EAST_WALL, WallSide.LOW)
                .setValue(WallBlock.UP, false);

        BlockState aged = process(
                certain(Blocks.STONE_BRICK_WALL, Blocks.MOSSY_STONE_BRICK_WALL), original, POS);

        assertEquals(Blocks.MOSSY_STONE_BRICK_WALL, aged.getBlock());
        assertEquals(WallSide.TALL, aged.getValue(WallBlock.NORTH_WALL));
        assertEquals(WallSide.LOW, aged.getValue(WallBlock.EAST_WALL));
        assertEquals(Boolean.FALSE, aged.getValue(WallBlock.UP));
    }

    @Test
    void slabsKeepTheirHalf() {
        BlockState original = Blocks.STONE_BRICK_SLAB.defaultBlockState()
                .setValue(BlockStateProperties.SLAB_TYPE, SlabType.TOP);

        BlockState aged = process(
                certain(Blocks.STONE_BRICK_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB), original, POS);

        assertEquals(SlabType.TOP, aged.getValue(BlockStateProperties.SLAB_TYPE));
    }

    @Test
    void blocksWithNoRulePassThroughUntouched() {
        AgingProcessor processor = certain(Blocks.STONE_BRICK_STAIRS, Blocks.MOSSY_STONE_BRICK_STAIRS);
        StructureTemplate.StructureBlockInfo info = new StructureTemplate.StructureBlockInfo(
                POS, Blocks.GOLD_BLOCK.defaultBlockState(), null);

        assertSame(info, processor.processBlock(null, BlockPos.ZERO, POS, info, info, SETTINGS),
                "An unmatched block should be returned as-is, not rebuilt");
    }

    @Test
    void aChainStopsAtTheStageThatMisses() {
        // agings=2 with an impossible second stage: the block must reach stage 1 and
        // stay there. Proves a later stage can't be reached by skipping an earlier one.
        AgingProcessor processor = new AgingProcessor(NO_TYPE, 2, List.of(
                new AgingRule(Blocks.STONE_BRICK_STAIRS, List.of(
                        new AgingStage(Blocks.MOSSY_STONE_BRICK_STAIRS, 1.0),
                        new AgingStage(Blocks.COBBLESTONE_STAIRS, 0.0)))));

        BlockState aged = process(processor, Blocks.STONE_BRICK_STAIRS.defaultBlockState(), POS);
        assertEquals(Blocks.MOSSY_STONE_BRICK_STAIRS, aged.getBlock());
    }

    @Test
    void agingsCapsHowDeepAChainCanGo() {
        // Same chain, both stages certain -- but agings=1 must stop after stage 1.
        List<AgingRule> rules = List.of(new AgingRule(Blocks.STONE_BRICK_STAIRS, List.of(
                new AgingStage(Blocks.MOSSY_STONE_BRICK_STAIRS, 1.0),
                new AgingStage(Blocks.COBBLESTONE_STAIRS, 1.0))));

        BlockState onePass = process(new AgingProcessor(NO_TYPE, 1, rules),
                Blocks.STONE_BRICK_STAIRS.defaultBlockState(), POS);
        BlockState twoPasses = process(new AgingProcessor(NO_TYPE, 2, rules),
                Blocks.STONE_BRICK_STAIRS.defaultBlockState(), POS);

        assertEquals(Blocks.MOSSY_STONE_BRICK_STAIRS, onePass.getBlock());
        assertEquals(Blocks.COBBLESTONE_STAIRS, twoPasses.getBlock());
    }

    @Test
    void laterChainsOnlyRollWhenEarlierOnesMiss() {
        // Alternative chains for the same source block are tried in order and the first
        // that decays wins -- which is why a later chain's authored probability is
        // CONDITIONAL. A certain first chain must starve a certain second one entirely.
        AgingProcessor processor = new AgingProcessor(NO_TYPE, 1, List.of(
                new AgingRule(Blocks.STONE_BRICKS,
                        List.of(new AgingStage(Blocks.MOSSY_STONE_BRICKS, 1.0))),
                new AgingRule(Blocks.STONE_BRICKS,
                        List.of(new AgingStage(Blocks.CRACKED_STONE_BRICKS, 1.0)))));

        BlockState aged = process(processor, Blocks.STONE_BRICKS.defaultBlockState(), POS);
        assertEquals(Blocks.MOSSY_STONE_BRICKS, aged.getBlock(),
                "The first chain that decays must win outright");
    }

    @Test
    void sameWorldPositionAlwaysAgesTheSameWay() {
        // The chunk-safety property: a caller running this over a procedural piece
        // re-processes each block once per chunk the piece overlaps, so a block on a
        // seam is visited twice in separate passes and MUST resolve identically. The
        // random is derived from the world position, so it does.
        AgingProcessor processor = new AgingProcessor(NO_TYPE, 1, List.of(
                new AgingRule(Blocks.STONE_BRICKS,
                        List.of(new AgingStage(Blocks.MOSSY_STONE_BRICKS, 0.5)))));

        for (int i = 0; i < 200; i++) {
            BlockPos pos = new BlockPos(i * 7 - 300, 40 + (i % 13), i * 3 - 100);
            BlockState first = process(processor, Blocks.STONE_BRICKS.defaultBlockState(), pos);
            BlockState second = process(processor, Blocks.STONE_BRICKS.defaultBlockState(), pos);
            assertEquals(first, second, "Aging must be a pure function of world position");
        }
    }

    @Test
    void differentPositionsGiveDifferentResults() {
        // Guards against the opposite failure: a positional hash that collapses to a
        // constant would make every block age (or not) identically, which reads as
        // "the processor is broken" in game but passes a determinism test.
        AgingProcessor processor = new AgingProcessor(NO_TYPE, 1, List.of(
                new AgingRule(Blocks.STONE_BRICKS,
                        List.of(new AgingStage(Blocks.MOSSY_STONE_BRICKS, 0.5)))));

        int aged = 0;
        int total = 400;
        for (int i = 0; i < total; i++) {
            BlockPos pos = new BlockPos(i, 64, i * 2);
            if (process(processor, Blocks.STONE_BRICKS.defaultBlockState(), pos)
                    .is(Blocks.MOSSY_STONE_BRICKS)) {
                aged++;
            }
        }
        assertNotEquals(0, aged, "Nothing aged at 50% over " + total + " positions");
        assertNotEquals(total, aged, "Everything aged at 50% over " + total + " positions");
    }
}
