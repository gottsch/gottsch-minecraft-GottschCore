/**
 * 
 */
package com.someguyssoftware.gottschcore.cube;

import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

/**
 * For Forge 1.8+
 * Wrapper class for Block/BlockState. Immutable.
 * TODO remove all the redundant getBlockState() calls and use this.getState()
 * @author Mark Gottschling on May 6, 2017
 *
 */
public class Cube {

	private final ICoords coords;
	private final IBlockState state;
	
	/**
	 * 
	 * @param world
	 * @param coords
	 */
	public Cube(World world, ICoords coords) {
		this.coords = coords;
		this.state = world.getBlockState(coords.toPos());
	}
	
	/**
	 * 
	 * @param coords
	 * @param state
	 */
	public Cube(ICoords coords, IBlockState state) {
		this.coords = coords;
		this.state = state;
	}
	
	/**
	 * 
	 * @return
	 */
	public IBlockState getState() {
		return state;
	}
	
	/**
	 * 
	 * @return
	 */
	public Block toBlock() {
		if (state != null) return state.getBlock();
		return null;
	}
	
	/**
	 * 
	 * @param world
	 * @param coords
	 * @return
	 */
	public static Block toBlock(final World world, final ICoords coords) {
		IBlockState blockState = world.getBlockState(coords.toPos());
		if (blockState != null) return blockState.getBlock();
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean hasState() {
		if (state == null) return false;
		return true;
	}
	
	/**
	 * 
	 * @param block
	 * @return
	 */
	public boolean equalsBlock(Block block) {
		if (state.getBlock() == block) return true;
		return false;
	}

	/**
	 * 
	 * @param material
	 * @return
	 */
	public boolean equalsMaterial(Material material) {		
		if (state.getMaterial() == material) return true;
		return false;	
	}
	
	/**
	 * Wrapper for Block.isAir()
	 * @return
	 */
	public boolean isAir() {
		return state.getMaterial() == Material.AIR;
	}
	
	/**
	 * Wrapper for Material.isReplaceable();
	 * @return
	 */
	public boolean isReplaceable() {
		return state.getMaterial().isReplaceable();
	}
	
	/**
	 * Wrapper for Material.isSolid()
	 * @return
	 */
	public boolean isSolid() {
		return getState().getMaterial().isSolid();
	}
	
	/**
	 * Wrapper for IBlockState.isTopSolid
	 * @return
	 */
	public boolean isTopSolid() {
		return getState().isTopSolid();		
	}
	
	public boolean isLiquid() {
		return getState().getMaterial().isLiquid();
	}
	
	/**
	 * Wrapper to Block.isBurning()
	 * @return
	 */
	public boolean isBurning(World world) {
		return toBlock().isBurning(world, this.coords.toPos());
	}
	
	/**
	 * @return the coords
	 */
	public ICoords getCoords() {
		return coords;
	}
	
	@Override
	public String toString() {
		return "Cube [coords=" + coords.toShortString() + ", state=" + state + "]";
	}
}
