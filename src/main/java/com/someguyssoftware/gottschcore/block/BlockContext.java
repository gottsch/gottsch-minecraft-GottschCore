/**
 * 
 */
package com.someguyssoftware.gottschcore.block;

import com.someguyssoftware.gottschcore.spatial.Coords;
import com.someguyssoftware.gottschcore.spatial.ICoords;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

/**
 * @author Mark Gottschling on Mar 10, 2020
 *
 */
public class BlockContext {
	private final ICoords coords;
	private final BlockState state;

	/**
	 * 
	 * @param world
	 * @param coords
	 */
	public BlockContext(IWorld world, ICoords coords) {
		this.coords = coords;
		this.state = world.getBlockState(coords.toPos());
	}

	/**
	 * Constructor that takes vanilla minecraft params.
	 * 
	 * @param world
	 * @param pos
	 */
	public BlockContext(IWorld world, BlockPos pos) {
		this(world, new Coords(pos));
	}

	/**
	 * 
	 * @param coords
	 * @param state
	 */
	public BlockContext(ICoords coords, BlockState state) {
		this.coords = coords;
		this.state = state;
	}

	/**
	 * Constructor that takes vanilla minecraft params.
	 * 
	 * @param pos
	 * @param state
	 */
	public BlockContext(BlockPos pos, BlockState state) {
		this(new Coords(pos), state);
	}

	/**
	 * 
	 * @return
	 */
	public Block toBlock() {
		if (state != null) {
			return state.getBlock();
		}
		return null;
	}

	/**
	 * 
	 * @param world
	 * @param coords
	 * @return
	 */
	public static Block toBlock(final World world, final ICoords coords) {
		BlockState blockState = world.getBlockState(coords.toPos());
		if (blockState != null)
			return blockState.getBlock();
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasState() {
//		IBlockState blockState = world.getBlockState(coords.toPos());
//		if (blockState == null) return false;
		if (state == null)
			return false;
		return true;
	}

	/**
	 * 
	 * @param block
	 * @return
	 */
	public boolean equalsBlock(Block block) {
		if (state.getBlock() == block)
			return true;
		return false;
	}

	/**
	 * 
	 * @param material
	 * @return
	 */
	public boolean equalsMaterial(Material material) {
		if (state.getMaterial() == material)
			return true;
		return false;
	}

	/**
	 * Wrapper for Block.isAir()
	 * 
	 * @return
	 */
	public boolean isAir() {
		return state.getMaterial() == Material.AIR;
	}

	/**
	 * Wrapper for Material.isReplaceable();
	 * 
	 * @return
	 */
	public boolean isReplaceable() {
		return state.getMaterial().isReplaceable();
	}

	/**
	 * Wrapper for Material.isSolid()
	 * 
	 * @return
	 */
	public boolean isSolid() {
		return getState().getMaterial().isSolid();
	}

//	/**
//	 * Wrapper for IBlockState.isTopSolid
//	 * @return
//	 */
//	public boolean isTopSolid() {
//		return getState().func_215702_a(worldIn, pos, directionIn)
//	}

	public boolean isLiquid() {
		return getState().getMaterial().isLiquid();
	}

	/**
	 * Wrapper to Block.isBurning()
	 * 
	 * @return
	 */
	public boolean isBurning() {
		return toBlock().isBurning(getState(), (IBlockReader) null, this.coords.toPos());
	}

	/**
	 * @return the coords
	 */
	public ICoords getCoords() {
		return coords;
	}

	/**
	 * 
	 * @return
	 */
	public BlockState getState() {
		return state;
	}

	@Override
	public String toString() {
		return "BlockContext [coords=" + coords.toShortString() + ", state=" + state + "]";
	}
}
