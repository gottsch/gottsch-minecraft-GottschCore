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
package com.someguyssoftware.gottschcore.world.gen.structure;

import com.someguyssoftware.gottschcore.spatial.ICoords;

import net.minecraft.world.level.block.state.BlockState;

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
