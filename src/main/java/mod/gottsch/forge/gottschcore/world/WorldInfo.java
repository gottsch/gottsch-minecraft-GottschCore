/*
 * This file is part of  GottschCore.
 * Copyright (c) 2017 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.gottschcore.world;

import mod.gottsch.forge.gottschcore.block.BlockContext;
import mod.gottsch.forge.gottschcore.spatial.Coords;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * @author Mark Gottschling on May 6, 2017
 *
 */
public class WorldInfo {
	public static final int MAX_HEIGHT = 319;
	public static final int MIN_HEIGHT = -63;
	public static final int BOTTOM_HEIGHT = -64;
	public static final int INVALID_SURFACE_POS = -255;
	public static final int CHUNK_RADIUS = 8;
	public static final int CHUNK_SIZE = CHUNK_RADIUS * 2;

	public static final ResourceLocation OVERWORLD = new ResourceLocation("overworld");
	public static final ResourceLocation THE_NETHER = new ResourceLocation("the_nether");
	public static final ResourceLocation THE_END = new ResourceLocation("the_end");

	public enum SURFACE {
		LAND, WATER, LAVA, OTHER, INVALID
	};

	public static boolean isServerDistribution() {
		return FMLEnvironment.dist == Dist.DEDICATED_SERVER;
	}

	public static boolean isServerSide(Level level) {
		return !isClientSide(level);
	}

	/**
	 * 
	 * Convenience companion method to isServerDistribution()
	 */
	public static boolean isClientDistribution() {
		return FMLEnvironment.dist == Dist.CLIENT;
	}

	/**
	 * Convenience companion method to isServerSide(Level world)
	 * 
	 * @param level
	 * @return
	 */
	public static boolean isClientSide(Level level) {
		return level.isClientSide();
	}

	/**
	 * 
	 * @param level
	 * @return
	 */
	public static ResourceLocation getDimension(Level level) {
		return level.dimension().location();
	}

	/**
	 * 
	 * @param level
	 * @param dimension
	 * @return
	 */
	public static boolean isCurrentDimension(Level level, ResourceLocation dimension) {
		return getDimension(level).equals(dimension);
	}

	/**
	 * 
	 * @param level
	 * @return
	 */
	public static boolean isSurfaceWorld(Level level) {
		return isCurrentDimension(level, OVERWORLD);
	}

	/**
	 * 
	 * @param level
	 * @param pos
	 * @return
	 */
	public static boolean isSurfaceWorld(Level level, BlockPos pos) {
		Holder<Biome> biome = level.getBiome(pos);
		return !biome.is(BiomeTags.IS_NETHER) && !biome.is(BiomeTags.IS_END);
	}

	/**
	 * 
	 * @param world
	 * @return
	 */
	public static boolean isTheNether(Level world) {
		return isCurrentDimension(world, THE_NETHER);
	}

	/**
	 * 
	 * @param world
	 * @return
	 */
	public static boolean isTheEnd(Level world) {
		return isCurrentDimension(world, THE_END);
	}

	/**
	 * Convenience wrapper method
	 * @param level
	 * @param coords
	 * @param state
	 */
	public static void setBlock(Level level, ICoords coords, BlockState state) {
		level.setBlock(coords.toPos(), state, 3);
	}

	// ========================================= Find the topmost block position methods =========================================
	/**
	 * Wrapper method.
	 * Finds the max height of the current dimension of the world.
	 * @param level
	 * @return
	 */
	public static int getHeight(final Level level) {
		return level.getHeight();
	}
	
	/**
	 * Finds the topmost block position at an Coords position in the world. Wrapper
	 * for BlockPos version.
	 * 
	 * @param level
	 * @param coords
	 * @return
	 */
	public static int getHeight(final ServerLevel level, final ChunkGenerator generator, final Heightmap.Types heightmapType, final ICoords coords) {
		return getHeight(level, generator, heightmapType, coords.toPos());
	}

	/**
	 * Finds the topmost block position at an BlockPos position in the world
	 * 
	 * @param level
	 * @param pos
	 * @return
	 */
	private static int getHeight(final ServerLevel level, final ChunkGenerator generator, final Heightmap.Types heightmapType, final BlockPos pos) {
		// grab height at first non-air block
		int occupiedHeight = generator.getFirstOccupiedHeight(
				pos.getX(), 
				pos.getZ(), 
				heightmapType, 
				level,
				level.getChunkSource().randomState());
		return occupiedHeight;
	}

	/**
	 * 
	 * @param coords
	 * @return
	 */
	public static boolean isHeightValid(final ICoords coords) {
		return isHeightValid(coords.toPos());
	}
	
	/**
	 * 
	 * @param blockPos
	 * @return
	 */
	private static boolean isHeightValid(final BlockPos blockPos) {
		if ((blockPos.getY() < MIN_HEIGHT || blockPos.getY() > MAX_HEIGHT)) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param level
	 * @param coords
	 * @return
	 */
	public static boolean isHeightValid(final Level level, final ICoords coords) {
		return isHeightValid(level, coords.toPos());
	}

	/**
	 * 
	 * @param level
	 * @param blockPos
	 * @return
	 */
	private static boolean isHeightValid(final Level level, final BlockPos blockPos) {
		if ((blockPos.getY() < MIN_HEIGHT || blockPos.getY() > level.getHeight())) {
			return false;
		}
		return true;
	}

	/*
	 * ========================================= 
	 * Surface location methods
	 * =========================================
	 */

	/**
	 * 
	 * @param level
	 * @param generator
	 * @param coords
	 * @return
	 */
	public static ICoords getDryLandSurfaceCoords(final ServerLevel level, final ChunkGenerator generator, final ICoords coords) {
		// grab height of land. Will stop at first non-air block
		int occupiedHeight = getHeight(level, generator, Heightmap.Types.WORLD_SURFACE_WG, coords);
		// the spawn coords is 1 ablove the land height
		ICoords spawnCoords = coords.withY(occupiedHeight + 1);

		// grabs column of blocks at given position
		NoiseColumn columnOfBlocks = generator.getBaseColumn(
				coords.getX(), 
				coords.getZ(), 
				level.getChunk(coords.getX(), 
						coords.getZ()).getHeightAccessorForGeneration(),
				level.getChunkSource().randomState());

		// get the top block of the column (1 below the spawn)
		BlockState topBlock = columnOfBlocks.getBlock(occupiedHeight);
		// test for non-solid state
		if (!topBlock.getFluidState().isEmpty()) {
			return Coords.EMPTY;
		}
		return spawnCoords;
	}

	// TESTING
	public static ICoords getDryLandSurfaceCoords(final ServerLevel level, final ChunkGenerator generator, Heightmap.Types heightMapType, final ICoords coords) {
		// grab height of land. Will stop at first non-air block
		int occupiedHeight = getHeight(level, generator, heightMapType, coords);
		// the spawn coords is 1 ablove the land height
		ICoords spawnCoords = coords.withY(occupiedHeight + 1);
		
		// grabs column of blocks at given position
		NoiseColumn columnOfBlocks = generator.getBaseColumn(
				coords.getX(), 
				coords.getZ(), 
				level,
				level.getChunkSource().randomState());
		
		// get the top block of the column (1 below the spawn)
		BlockState topBlock = columnOfBlocks.getBlock(occupiedHeight);
		// test for non-solid state
		if (!topBlock.getFluidState().isEmpty()) {
			return Coords.EMPTY;
		}
		return spawnCoords;
	}
	
	/**
	 * TESTING
	 * @param level
	 * @param generator
	 * @param coords
	 * @return
	 */
	@Deprecated
	// use Optional<ICoords>
	public static ICoords getDryLandSurfaceCoordsWG(IWorldGenContext context, final ICoords coords) {
		return getDryLandSurfaceCoords((ServerLevel)context.level(), context.chunkGenerator(), Heightmap.Types.WORLD_SURFACE_WG, coords);
	}
	
	/**
	 * 
	 * @param level
	 * @param generator
	 * @param coords
	 * @return
	 */
	public static ICoords getSurfaceCoords(final ServerLevel level, final ChunkGenerator generator, final ICoords coords) {
		// grab height of land. Will stop at first non-air block
		int occupiedHeight = getHeight(level, generator, Heightmap.Types.WORLD_SURFACE_WG, coords);
		// the spawn coords is 1 ablove the land height
		ICoords spawnCoords = coords.withY(occupiedHeight + 1);

		// grabs column of blocks at given position
		NoiseColumn columnOfBlocks = generator.getBaseColumn(
				coords.getX(), 
				coords.getZ(), 
				level.getChunk(coords.getX(), 
						coords.getZ()).getHeightAccessorForGeneration(),
				level.getChunkSource().randomState());

		// get the top block of the column (1 below the spawn)
		BlockState topBlock = columnOfBlocks.getBlock(occupiedHeight);
		return spawnCoords;
	}

	/**
	 * Gets the first valid dry land surface position (not on/under water or lava)
	 * from a subterranean starting point.
	 * NOTE this does not use the ChunkGenerator.
	 * 
	 * @param world
	 * @param coords
	 * @return
	 */
	public static ICoords getSubterraneanSurfaceCoords(final ServerLevel world, final ICoords coords) {
		boolean isSurfaceBlock = false;
		ICoords newCoords = coords;

		while (!isSurfaceBlock) {
			// get the blockContext that is 1 below current position
			BlockContext blockContext = new BlockContext(world, newCoords.down(1));
			// exit if not valid Y coordinate
			if (!isHeightValid(blockContext.getCoords())) {
				return Coords.EMPTY;
			}
			// test if the block at position is water, lava or ice
			if (blockContext.isFluid() || blockContext.equalsMaterial(Material.ICE)) {
				return Coords.EMPTY;
			}
			if (blockContext.equalsMaterial(Material.AIR) || blockContext.isReplaceable()
					|| blockContext.equalsMaterial(Material.LEAVES) || blockContext.equalsMaterial(Material.WOOD)
					|| blockContext.isBurning()) {
				newCoords = newCoords.down(1);
			} else {
				isSurfaceBlock = true;
			}
		}
		return newCoords;
	}

	/**
	 * Gets the first valid land surface position (could be under water or lava)
	 * from the given starting point.
	 * 
	 * @param world
	 * @param pos
	 * @return
	 */
	public static ICoords getAnyLandSurfaceCoords(final ServerLevel level, final ChunkGenerator generator, final ICoords coords) {		
		boolean isSurfaceBlock = false;

		// grab height of land. Will stop at first non-air block
		int occupiedHeight = getHeight(level, generator, Heightmap.Types.WORLD_SURFACE_WG, coords);

		// grabs column of blocks at given position
		NoiseColumn columnOfBlocks = generator.getBaseColumn(
				coords.getX(), 
				coords.getZ(), 
				level.getChunk(coords.getX(), 
						coords.getZ()).getHeightAccessorForGeneration(),
				level.getChunkSource().randomState());

		// get the top block of the column (1 below the spawn)
		BlockState noiseBlock = columnOfBlocks.getBlock(occupiedHeight);

		int index = 0;
		while (!isSurfaceBlock) {			
			noiseBlock = columnOfBlocks.getBlock(occupiedHeight -= index);
			BlockContext blockContext = new BlockContext(coords.withY(occupiedHeight), noiseBlock);
			// exit if not valid Y coordinate
			if (!isHeightValid(blockContext.getCoords())) {
				return Coords.EMPTY;
			}
			if (blockContext.isAir() || blockContext.isReplaceable() || blockContext.isFluid()	
					|| blockContext.isBurning()) {
				index++;
			}
			else {
				isSurfaceBlock = true;
			}
		}
		return coords.withY(occupiedHeight + 1);
	}

	/**
	 * 
	 * @param world
	 * @param generator
	 * @param coords
	 * @return
	 */
	public static ICoords getOceanFloorSurfaceCoords(final ServerLevel level, final ChunkGenerator generator, final ICoords coords) {
		// grab height of land. Will stop at first non-air block
		int occupiedHeight = getHeight(level, generator, Heightmap.Types.OCEAN_FLOOR_WG, coords);
		// the spawn coords is 1 above the land height
		ICoords spawnCoords = coords.withY(occupiedHeight + 1);

		// grabs column of blocks at given position
		NoiseColumn columnOfBlocks = generator.getBaseColumn(
				coords.getX(), 
				coords.getZ(), 
				level.getChunk(coords.getX(), coords.getZ()),
				level.getChunkSource().randomState());

		// get the top block of the column (1 below the spawn)
		BlockState topBlock = columnOfBlocks.getBlock(occupiedHeight);
		// test for non-solid state
		if (!topBlock.getFluidState().isEmpty()) {
			return Coords.EMPTY;
		}
		return spawnCoords;
	}

	/*
	 * ========================================= Base check methods =========================================
	 */

	/**
	 * 
	 * @param world
	 * @param coords
	 * @param width
	 * @param depth
	 * @param groundPercentRequired
	 * @param airPercentRequired
	 * @return
	 */
	public static boolean isValidAboveGroundBase(final Level world, final ICoords coords, final int width,
			final int depth, final double groundPercentRequired, final double airPercentRequired) {
		return isSolidBase(world, coords, width, depth, groundPercentRequired)
				&& isAirBase(world, coords.up(1), width, depth, airPercentRequired);
	}

	/**
	 * Method to check if the ground (base) has a % of solid blocks, and checks the
	 * y+1, and y+2 to see if has a % of air blocks.
	 * 
	 * @param world
	 * @param coords
	 * @param width
	 * @param depth
	 * @param groundPercentRequired
	 * @param airPercentRequired1
	 * @param airPercentRequired2
	 * @return
	 */
	public static boolean isValidAboveGroundBase(final Level world, final ICoords coords, final int width,
			final int depth, final double groundPercentRequired, final double airPercentRequired1,
			final double airPercentRequired2) {
		return isSolidBase(world, coords, width, depth, groundPercentRequired)
				&& isAirBase(world, coords.up(1), width, depth, airPercentRequired1)
				&& isAirBase(world, coords.up(2), width, depth, airPercentRequired2);
	}

	/**
	 * 
	 * @param world
	 * @param coords
	 * @param width
	 * @param depth
	 * @param percentRequired
	 * @return
	 */
	public static boolean isSolidBase(final Level world, final ICoords coords, final int width, final int depth,
			final double percentRequired) {
		double percent = getSolidBasePercent(world, coords.down(1), width, depth);

		if (percent < percentRequired) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param world
	 * @param coords
	 * @param width
	 * @param depth
	 * @return
	 */
	public static double getSolidBasePercent(final Level world, final ICoords coords, final int width,
			final int depth) {
		int platformSize = 0;

		// process all z, x in base y (-1) to count the number of allowable blocks in
		// the Level platform
		for (int z = 0; z < depth; z++) {
			for (int x = 0; x < width; x++) {
				// get the blockContext
				BlockContext blockContext = new BlockContext(world, coords.add(x, 0, z));

				// test the blockContext
				if (blockContext.hasState() && blockContext.isSolid() && !blockContext.isReplaceable()) {
					platformSize++;
				}
			}
		}

		double base = depth * width;
		double percent = ((platformSize) / base) * 100.0D;
		return percent;
	}

	/**
	 * Gets the percent of blocks in an area (WxD) that are air/air-like
	 * (replaceable).
	 * 
	 * @param world
	 * @param coords
	 * @param width
	 * @param depth
	 * @return
	 */
	public static double getAirBasePercent(final Level world, final ICoords coords, final int width, final int depth) {
		double percent = 0.0D;
		int airBlocks = 0;

		// process all z, x in base y to count the number of allowable blocks in the
		// world platform
		for (int z = 0; z < depth; z++) {
			for (int x = 0; x < width; x++) {
				// get the blockContext
				BlockContext blockContext = new BlockContext(world, coords.add(x, 0, z));
				if (blockContext.hasState()
						&& (blockContext.equalsMaterial(Material.AIR) || blockContext.isReplaceable())) {
					airBlocks++;
				}
			}
		}
		double base = depth * width;
		percent = (airBlocks / base) * 100.0D;
		return percent;
	}

	/**
	 * 
	 * @param world
	 * @param coords
	 * @param width
	 * @param depth
	 * @param percentRequired
	 * @return
	 */
	public static boolean isAirBase(final Level world, final ICoords coords, final int width, final int depth,
			double percentRequired) {
		double percent = getAirBasePercent(world, coords.down(1), width, depth);
		if (percent < percentRequired) {
			return false;
		}
		return true;
	}

	public static boolean isLiquidBase(final Level world, final ICoords coords, final int width, final int depth,
			double percentRequired) {
		double percent = getFluidBasePercent(world, coords.down(1), width, depth);
		if (percent < percentRequired) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the percent of blocks in an area (WxD) that are liquid.
	 * 
	 * @param world
	 * @param coords
	 * @param width
	 * @param depth
	 * @return
	 */
	public static double getFluidBasePercent(final Level world, final ICoords coords, final int width, final int depth) {
		double percent = 0.0D;
		int liquidBlocks = 0;

		// process all z, x in base y to count the number of allowable blocks in the
		// world platform
		for (int z = 0; z < depth; z++) {
			for (int x = 0; x < width; x++) {
				// get the blockContext
				BlockContext blockContext = new BlockContext(world, coords.add(x, 0, z));
				if (blockContext.hasState() && blockContext.isFluid()) {
					liquidBlocks++;
				}
			}
		}
		double base = depth * width;
		percent = (liquidBlocks / base) * 100.0D;
		return percent;
	}



	/**
	 * 
	 * @param level
	 * @param sourceCoords
	 * @return
	 */
	public static ICoords getClosestPlayerCoords(Level level, ICoords sourceCoords) {
		Player player = level.getNearestPlayer(sourceCoords.getX(), sourceCoords.getY(), sourceCoords.getZ(), 64.0, false);
		ICoords playerCoords = new Coords(player.blockPosition());
		return playerCoords;
	}
}
