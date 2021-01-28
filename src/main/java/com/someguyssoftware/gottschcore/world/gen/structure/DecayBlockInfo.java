/**
 * 
 */
package com.someguyssoftware.gottschcore.world.gen.structure;

import com.someguyssoftware.gottschcore.spatial.ICoords;

import net.minecraft.block.BlockState;


/**
 * @author Mark Gottschling on Dec 9, 2019
 *
 */
public class DecayBlockInfo extends BlockInfoContext {

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
	public DecayBlockInfo(GottschTemplate.BlockInfo blockInfo, ICoords coords, BlockState state) {
		super(blockInfo, coords, state);
		this.strength = 100.0F;
		this.wall = false;
		this.decayIndex = -1;
		this.distance = 0;
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
	
	@Override
	public String toString() {
		return "DecayBlockInfo [strength=" + strength + ", decayIndex=" + decayIndex + ", distance=" + distance
				+ ", wall=" + wall + ", coords=" + coords + ", blockInfo=" + blockInfo + ", state=" + state
				+ ", postProcess=" + postProcess + "]";
	}
}
