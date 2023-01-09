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
package com.someguyssoftware.gottschcore.world;

import com.someguyssoftware.gottschcore.block.BlockContext;
import com.someguyssoftware.gottschcore.spatial.Coords;
import com.someguyssoftware.gottschcore.spatial.ICoords;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * @author Mark Gottschling on May 6, 2017
 *
 */
@Deprecated
public class WorldInfo {
	public static final int MAX_HEIGHT = 319;
	public static final int MIN_HEIGHT = -63;
	public static final int BOTTOM_HEIGHT = -64;
	public static final ICoords EMPTY_COORDS = new Coords(0, BOTTOM_HEIGHT - 1, 0);
	public static final int INVALID_SURFACE_POS = -255;
	public static final int CHUNK_RADIUS = 8;
	public static final int CHUNK_SIZE = CHUNK_RADIUS * 2;

	public static final ResourceLocation OVERWORLD = new ResourceLocation("overworld");
	public static final ResourceLocation THE_NETHER = new ResourceLocation("the_nether");
	public static final ResourceLocation THE_END = new ResourceLocation("the_end");

	public enum SURFACE {
		LAND, WATER, LAVA, OTHER, INVALID
	};


	// ========================================= Find the game side =========================================


	public static boolean isServerSide() {
		return FMLEnvironment.dist == Dist.DEDICATED_SERVER;
	}

	public static boolean isServerSide(Level world) {
		return !world.isClientSide();
	}

	/**
	 * 
	 * Convenience companion method to isServerSide()
	 */
	public static boolean isClientDistribution() {
		return FMLEnvironment.dist == Dist.CLIENT;
	}

	/**
	 * Convenience companion method to isServerSide(Level world)
	 * 
	 * @param world
	 * @return
	 */
	public static boolean isClientSide(Level world) {
		return world.isClientSide();
	}

	// ===========
	/**
	 * 
	 * @param world
	 * @return
	 */
	public static ResourceLocation getDimension(Level world) {
		return world.dimension().location();
	}

	/**
	 * 
	 * @param world
	 * @param dimension
	 * @return
	 */
	public static boolean isCurrentDimension(Level world, ResourceLocation dimension) {
		return getDimension(world).equals(dimension);
	}

	/**
	 * 
	 * @param world
	 * @return
	 */
	public static boolean isSurfaceWorld(Level world) {
		return isCurrentDimension(world, OVERWORLD);
	}

	/**
	 * 
	 * @param world
	 * @param pos
	 * @return
	 */
	public static boolean isSurfaceWorld(Level world, BlockPos pos) {
		BiomeCategory cat = Biome.getBiomeCategory(world.getBiome(pos));
		return cat != Biome.BiomeCategory.NETHER && cat != Biome.BiomeCategory.THEEND;
//		return world.getBiome(pos).getBiomeCategory() != Biome.BiomeCategory.NETHER && world.getBiome(pos).getBiomeCategory() != Biome.BiomeCategory.THEEND;
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
	 * 
	 * @param world
	 * @param coords
	 * @param state
	 */
	public static void setBlockState(Level world, ICoords coords, BlockState state) {
		world.setBlock(coords.toPos(), state, 3);
	}

	/**
	 * 
	 * @param world
	 * @param context
	 */
	public static void setBlockState(Level world, BlockContext context) {
		world.setBlock(context.getCoords().toPos(), context.getState(), 3);
	}

	/**
	 * mc1.16.5 version.
	 * @param world
	 * @param coords
	 * @param state
	 */
	public static void setBlock(Level world, ICoords coords, BlockState state) {
		world.setBlock(coords.toPos(), state, 3);
	}

	// ========================================= Find the topmost block position methods =========================================
	/**
	 * Finds the topmost block position at an Coords position in the world. Wrapper
	 * for BlockPos version.
	 * 
	 * @param world
	 * @param coords
	 * @return
	 */
	public static int getHeight(final Level world, final ICoords coords) {
		return world.getHeight();
	}

	/**
	 * Finds the topmost block position at an BlockPos position in the world
	 * 
	 * @param world
	 * @param pos
	 * @return
	 */
	private static int getHeight(final Level world, final BlockPos pos) {
		BlockPos p = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos);
		return p.getY();
	}

	/**
	 * 
	 * @param coords
	 * @return
	 */
	public static boolean isValidY(final ICoords coords) {
		return isValidY(coords.toPos());
	}

	/**
	 * 
	 * @param blockPos
	 * @return
	 */
	private static boolean isValidY(final BlockPos blockPos) {
		if ((blockPos.getY() < MIN_HEIGHT || blockPos.getY() > MAX_HEIGHT)) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param world
	 * @param coords
	 * @return
	 */
	public static boolean isValidY(final Level world, final ICoords coords) {
		return isValidY(world, coords.toPos());
	}

	/**
	 * 
	 * @param world
	 * @param blockPos
	 * @return
	 */
	private static boolean isValidY(final Level world, final BlockPos blockPos) {
		if ((blockPos.getY() < MIN_HEIGHT || blockPos.getY() > world.getHeight())) {
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
	 * Gets the surface block at a given position (x,z). Any aboveground land
	 * surface or surface of body of water/lava, ie. excludes any vegatation or
	 * replacement material.
	 * 
	 * @param world
	 * @param coords
	 * @return
	 */
	public static ICoords getSurfaceCoords(final Level world, final ICoords coords) {

		boolean isSurfaceBlock = false;
		ICoords newCoords = coords;

		while (!isSurfaceBlock) {
			BlockContext blockContext = new BlockContext(world, newCoords.down(1));

			// exit if not valid Y coordinate
			if (!isValidY(blockContext.getCoords())) {
				return null;
			}

			if (blockContext.equalsMaterial(Material.AIR) || (blockContext.isReplaceable() && !blockContext.isLiquid())
					|| blockContext.equalsMaterial(Material.LEAVES) || blockContext.equalsMaterial(Material.WOOD)
					|| blockContext.isBurning()) {
				newCoords = newCoords.down(1);
			} else {
				isSurfaceBlock = true;
			}

		}
		return newCoords;
	}

	//	/**
	//	 * 
	//	 * @param world
	//	 * @param pos
	//	 * @return
	//	 */
	//	public static ICoords getSurfaceCoords(final Level world, final BlockPos pos) {
	//		return getSurfaceCoords(world, new Coords(pos));
	//	}

	/**
	 * Gets the first valid dry land surface position (not on/under water or lava)
	 * from the given starting point.
	 * 
	 * @param world
	 * @param coords
	 * @return
	 */
	@Deprecated
	public static ICoords getDryLandSurfaceCoords(final ServerLevel world, final ICoords coords) {
		boolean isSurfaceBlock = false;
		ICoords newCoords = coords;

		while (!isSurfaceBlock) {
			// get the blockContext that is 1 below current position
			BlockContext blockContext = new BlockContext(world, newCoords.down(1));
			// exit if not valid Y coordinate
			if (!isValidY(blockContext.getCoords())) {
				return EMPTY_COORDS;
			}
			// test if the block at position is water, lava or ice
			if (blockContext.equalsMaterial(Material.WATER) || blockContext.equalsMaterial(Material.LAVA)
					|| blockContext.equalsMaterial(Material.ICE)) {
				return EMPTY_COORDS;
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
	 * 
	 * @param world
	 * @param generator
	 * @param coords
	 * @return
	 */
	public static ICoords getDryLandSurfaceCoords(final ServerLevel world, final ChunkGenerator generator, final ICoords coords) {
		ChunkAccess chunkAccess = world.getChunk(coords.getX(), coords.getZ());
		// grab height of land. Will stop at first non-air block
		int landHeight = generator.getFirstOccupiedHeight(coords.getX(), coords.getZ(), Heightmap.Types.WORLD_SURFACE_WG, chunkAccess.getHeightAccessorForGeneration());
		// the spawn coords is 1 ablove the land height
		ICoords spawnCoords = coords.withY(landHeight + 1);
		
		// grabs column of blocks at given position
		NoiseColumn columnOfBlocks = generator.getBaseColumn(coords.getX(), coords.getZ(), chunkAccess.getHeightAccessorForGeneration());
		// get the top block of the column (1 below the spawn)
		BlockState topBlock = columnOfBlocks.getBlock(landHeight);
		// test for non-solid state
		if (!topBlock.getFluidState().isEmpty()) {
			return EMPTY_COORDS;
		}
		return spawnCoords;
	}

	/**
	 * Gets the first valid land surface position (could be under water or lava)
	 * from the given starting point.
	 * 
	 * @param world
	 * @param pos
	 * @return
	 */
	public static ICoords getAnyLandSurfaceCoords(final Level world, final ICoords coords) {
		boolean isSurfaceBlock = false;
		ICoords newCoords = coords;

		while (!isSurfaceBlock) {
			BlockContext blockContext = new BlockContext(world, newCoords.down(1));

			// exit if not valid Y coordinate
			if (!isValidY(blockContext.getCoords())) {
				return EMPTY_COORDS;
			}
			if (blockContext.equalsMaterial(Material.AIR) || blockContext.equalsMaterial(Material.WATER)
					|| blockContext.isReplaceable() || blockContext.equalsMaterial(Material.LEAVES)
					|| blockContext.equalsMaterial(Material.WOOD) || blockContext.equalsMaterial(Material.LAVA)
					|| blockContext.equalsMaterial(Material.ICE) || blockContext.isBurning()) {
				newCoords = newCoords.down(1);
			} else {
				isSurfaceBlock = true;
			}
		}
		return newCoords;
	}

	/**
	 * 
	 * @param world
	 * @param pos
	 * @return
	 */
	public static ICoords getOceanFloorSurfaceCoords(final Level world, final ICoords coords) {
		boolean isSurfaceBlock = false;
		ICoords newCoords = coords;

		while (!isSurfaceBlock) {
			BlockContext blockContext = new BlockContext(world, newCoords.down(1));
			// exit if not valid Y coordinate
			if (!isValidY(blockContext.getCoords())) {
				return EMPTY_COORDS;
			}

			// test if the block at position is water, lava or ice
			if (blockContext.equalsMaterial(Material.LAVA)) {
				return EMPTY_COORDS;
			}

			if (blockContext.equalsMaterial(Material.AIR) || blockContext.equalsMaterial(Material.WATER)
					|| blockContext.equalsMaterial(Material.ICE) || blockContext.isReplaceable()
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
	 * 
	 * @param world
	 * @param generator
	 * @param coords
	 * @return
	 */
	public static ICoords getOceanFloorSurfaceCoords(final ServerLevel world, final ChunkGenerator generator, final ICoords coords) {
        ChunkAccess chunkAccess = world.getChunk(coords.getX(), coords.getZ());
        
		// grab height of land. Will stop at first non-air block
		int landHeight = generator.getFirstOccupiedHeight(coords.getX(), coords.getZ(), Heightmap.Types.OCEAN_FLOOR_WG, chunkAccess.getHeightAccessorForGeneration());
		// the spawn coords is 1 ablove the land height
		ICoords spawnCoords = coords.withY(landHeight + 1);
		
		// grabs column of blocks at given position
		NoiseColumn columnOfBlocks = generator.getBaseColumn(coords.getX(), coords.getZ(), chunkAccess.getHeightAccessorForGeneration());
		// get the top block of the column (1 below the spawn)
		BlockState topBlock = columnOfBlocks.getBlock(landHeight);
		// test for non-solid state
		if (!topBlock.getFluidState().isEmpty()) {
			return EMPTY_COORDS;
		}
		return spawnCoords;
	}

	/**
	 * 
	 * @param world
	 * @param coords
	 * @return
	 */
	public static SURFACE getSurface(Level world, ICoords coords) {
		// go down to surface
		ICoords surfaceCoords = getSurfaceCoords(world, coords);

		if (surfaceCoords == null)
			return SURFACE.INVALID;

		BlockContext blockContext = new BlockContext(world, surfaceCoords.down(1));

		// exit if not valid Y coordinate
		if (!isValidY(blockContext.getCoords())) {
			return SURFACE.INVALID;
		}

		SURFACE surface;
		if (blockContext.equalsMaterial(Material.WATER) || blockContext.equalsMaterial(Material.ICE)) {
			surface = SURFACE.WATER;
		} else if (blockContext.equalsMaterial(Material.LAVA)) {
			surface = SURFACE.LAVA;
		} else if (blockContext.isSolid()) {
			surface = SURFACE.LAND;
		} else {
			surface = SURFACE.OTHER;
		}
		return surface;
	}

	/**
	 * Convenience method.
	 * 
	 * @param world
	 * @param coords
	 * @return
	 */
	public static boolean isSurfaceOnLand(Level world, ICoords coords) {
		if (getSurface(world, coords) == SURFACE.LAND)
			return true;
		return false;
	}

	/**
	 * Convenience method.
	 * 
	 * @param world
	 * @param coords
	 * @return
	 */
	public static boolean isSurfaceOnWater(Level world, ICoords coords) {
		if (getSurface(world, coords) == SURFACE.WATER)
			return true;
		return false;
	}

	/*
	 * ========================================= Base check methods
	 * =========================================
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
		double percent = getLiquidBasePercent(world, coords.down(1), width, depth);
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
	public static double getLiquidBasePercent(final Level world, final ICoords coords, final int width, final int depth) {
		double percent = 0.0D;
		int liquidBlocks = 0;

		// process all z, x in base y to count the number of allowable blocks in the
		// world platform
		for (int z = 0; z < depth; z++) {
			for (int x = 0; x < width; x++) {
				// get the blockContext
				BlockContext blockContext = new BlockContext(world, coords.add(x, 0, z));
				if (blockContext.hasState() && blockContext.isLiquid()) {
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
	 * @param world
	 * @param coords
	 * @return
	 */
	public static int getDifferenceWithSurface(Level world, ICoords coords) {
		int ySurface = 0;
		int diff = 0;

		// get a valid surface coords (whether on land or sea)
		ySurface = getHeight(world, coords);
		//		GottschCore.logger.debug("ySurface:" + ySurface);
		ICoords surfaceCoords = getSurfaceCoords(world, coords.resetY(ySurface));
		if (surfaceCoords == null) {
			return INVALID_SURFACE_POS;
		}

		// get the difference betwen ySurface and the Coords Y
		diff = surfaceCoords.getY() - coords.getY();

		return diff;
	}

	/**
	 * 
	 * @param world
	 * @param sourceCoords
	 * @return
	 */
	public static ICoords getClosestPlayerCoords(Level world, ICoords sourceCoords) {
		Player player = world.getNearestPlayer(sourceCoords.getX(), sourceCoords.getY(), sourceCoords.getZ(), 64.0, false);
		ICoords playerCoords = new Coords(player.blockPosition());
		return playerCoords;
	}
}
