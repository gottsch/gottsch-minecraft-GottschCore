/**
 * 
 */
package com.someguyssoftware.gottschcore.world.gen.structure;

import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.block.state.IBlockState;

/**
 * @author Mark Gottschling on Dec 9, 2019
 *
 */
public class DecayBlockInfo {
	ICoords coords;
	GottschTemplate.BlockInfo blockInfo;
	IBlockState state;
	/** strength of the block */
	private double strength;
	private int decayIndex;
	private int distance;
	private boolean wall;
	
	/**
	 * 
	 * @param blockInfo
	 * @param state
	 */
	public DecayBlockInfo(GottschTemplate.BlockInfo blockInfo, ICoords coords, IBlockState state) {
		this.blockInfo = blockInfo;
		this.coords = coords;
		this.state = state;
		this.strength = 100.0F;
		this.wall = false;
		this.decayIndex = -1;
		this.distance = 0;
	}

	public GottschTemplate.BlockInfo getBlockInfo() {
		return blockInfo;
	}

	public void setBlockInfo(GottschTemplate.BlockInfo blockInfo) {
		this.blockInfo = blockInfo;
	}

	public IBlockState getState() {
		return state;
	}

	public void setState(IBlockState state) {
		this.state = state;
	}

	public double getStrength() {
		return strength;
	}

	public void setStrength(double strength) {
		this.strength = strength;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public boolean isWall() {
		return wall;
	}

	public void setWall(boolean wall) {
		this.wall = wall;
	}

	public int getDecayIndex() {
		return decayIndex;
	}

	public void setDecayIndex(int decayIndex) {
		this.decayIndex = decayIndex;
	}

	public ICoords getCoords() {
		return coords;
	}

	public void setCoords(ICoords coords) {
		this.coords = coords;
	}
	
	/** convenience methods for comparators */
	
	public int getX() {
		return getCoords().getX();
	}
	
	public int getY() {
		return getCoords().getY();
	}
	
	public int getZ() {
		return getCoords().getZ();
	}
}
