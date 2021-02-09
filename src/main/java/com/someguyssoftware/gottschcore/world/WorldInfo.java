/**
 * 
 */
package com.someguyssoftware.gottschcore.world;

import com.someguyssoftware.gottschcore.block.BlockContext;
import com.someguyssoftware.gottschcore.spatial.Coords;
import com.someguyssoftware.gottschcore.spatial.ICoords;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * @author Mark Gottschling on May 6, 2017
 *
 */
public class WorldInfo {
	private static final int MAX_HEIGHT = 256;
	private static final int MIN_HEIGHT = 1;
	public static final ICoords EMPTY_COORDS = new Coords(-1, -1, -1);
	public static final int INVALID_SURFACE_POS = -255;
	public static final int CHUNK_RADIUS = 8;
	public static final int CHUNK_SIZE = CHUNK_RADIUS * 2;

	public enum SURFACE {
		LAND, WATER, LAVA, OTHER, INVALID
	};

	/*
	 * ========================================= Find the game side.
	 * =========================================
	 */

	public static boolean isServerSide() {
		return FMLEnvironment.dist == Dist.DEDICATED_SERVER;
	}

	public static boolean isServerSide(World world) {
		return !world.isRemote;
	}

	/**
	 * 
	 * Convenience companion method to isServerSide()
	 */
	public static boolean isClientDistribution() {
		return FMLEnvironment.dist == Dist.CLIENT;
	}

	/**
	 * Convenience companion method to isServerSide(World world)
	 * 
	 * @param world
	 * @return
	 */
	public static boolean isClientSide(World world) {
		return world.isRemote;
	}

	/**
	 * 
	 * @param world
	 * @param coords
	 * @param state
	 */
	public static void setBlockState(IWorld world, ICoords coords, BlockState state) {
		world.setBlockState(coords.toPos(), state, 3);
	}
	
	/**
	 * 
	 * @param world
	 * @param context
	 */
	public static void setBlockState(IWorld world, BlockContext context) {
		world.setBlockState(context.getCoords().toPos(), context.getState(), 3);
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
	public static int getHeightValue(final IWorld world, final ICoords coords) {
		return world.getHeight(Heightmap.Type.WORLD_SURFACE, coords.toPos()).getY();
	}

	/**
	 * Finds the topmost block position at an BlockPos position in the world
	 * 
	 * @param world
	 * @param pos
	 * @return
	 */
	private static int getHeightValue(final IWorld world, final BlockPos pos) {
		BlockPos p = world.getHeight(Heightmap.Type.WORLD_SURFACE, pos);
		return p.getY();
	}

	/*
	 * ========================================= Determine valid height (y-pos)
	 * methods =========================================
	 */

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
	public static boolean isValidY(final World world, final ICoords coords) {
		return isValidY(world, coords.toPos());
	}

	/**
	 * 
	 * @param world
	 * @param blockPos
	 * @return
	 */
	private static boolean isValidY(final World world, final BlockPos blockPos) {
		if ((blockPos.getY() < MIN_HEIGHT || blockPos.getY() > world.getActualHeight())) {
			return false;
		}
		return true;
	}

	/*
	 * ========================================= Surface location methods
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
	public static ICoords getSurfaceCoords(final World world, final ICoords coords) {

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
//	public static ICoords getSurfaceCoords(final World world, final BlockPos pos) {
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
	public static ICoords getDryLandSurfaceCoords(final IWorld world, final ICoords coords) {
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
	 * Gets the first valid land surface position (could be under water or lava)
	 * from the given starting point.
	 * 
	 * @param world
	 * @param pos
	 * @return
	 */
	public static ICoords getAnyLandSurfaceCoords(final World world, final ICoords coords) {
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
	public static ICoords getOceanFloorSurfaceCoords(final IWorld world, final ICoords coords) {
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
	 * @param coords
	 * @return
	 */
	public static SURFACE getSurface(World world, ICoords coords) {
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
	public static boolean isSurfaceOnLand(World world, ICoords coords) {
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
	public static boolean isSurfaceOnWater(World world, ICoords coords) {
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
	public static boolean isValidAboveGroundBase(final IWorld world, final ICoords coords, final int width,
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
	public static boolean isValidAboveGroundBase(final World world, final ICoords coords, final int width,
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
	public static boolean isSolidBase(final IWorld world, final ICoords coords, final int width, final int depth,
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
	public static double getSolidBasePercent(final IWorld world, final ICoords coords, final int width,
			final int depth) {
		int platformSize = 0;

		// process all z, x in base y (-1) to count the number of allowable blocks in
		// the world platform
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
	public static double getAirBasePercent(final IWorld world, final ICoords coords, final int width, final int depth) {
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
	public static boolean isAirBase(final IWorld world, final ICoords coords, final int width, final int depth,
			double percentRequired) {
		double percent = getAirBasePercent(world, coords.down(1), width, depth);
		if (percent < percentRequired) {
			return false;
		}
		return true;
	}
	
	public static boolean isLiquidBase(final IWorld world, final ICoords coords, final int width, final int depth,
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
	public static double getLiquidBasePercent(final IWorld world, final ICoords coords, final int width, final int depth) {
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
	public static int getDifferenceWithSurface(World world, ICoords coords) {
		int ySurface = 0;
		int diff = 0;

		// get a valid surface coords (whether on land or sea)
		ySurface = getHeightValue(world, coords);
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
	public static ICoords getClosestPlayerCoords(World world, ICoords sourceCoords) {
		PlayerEntity player = world.getClosestPlayer(sourceCoords.getX(), sourceCoords.getY(), sourceCoords.getZ());
		ICoords playerCoords = new Coords(player.getPosition());
		return playerCoords;
	}
}
