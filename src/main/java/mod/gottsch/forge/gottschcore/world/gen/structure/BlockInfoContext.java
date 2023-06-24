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
package mod.gottsch.forge.gottschcore.world.gen.structure;

import mod.gottsch.forge.gottschcore.spatial.ICoords;
import net.minecraft.world.level.block.state.BlockState;


/**
 * @author Mark Gottschling on Feb 12, 2020
 *
 */
public class BlockInfoContext {
	ICoords coords;
	GottschTemplate.BlockInfo blockInfo;
	BlockState state;
	boolean postProcess;

	/**
	 * 
	 * @param blockInfo
	 * @param coords
	 * @param state
	 */
	public BlockInfoContext(GottschTemplate.BlockInfo blockInfo, ICoords coords, BlockState state) {
		this.blockInfo = blockInfo;
		this.coords = coords;
		this.state = state;
		this.postProcess = false;
	}
	
	/**
	 * 
	 * @param blockInfo
	 * @param coords
	 * @param state
	 * @param postProcessCandidate
	 */
	public BlockInfoContext(GottschTemplate.BlockInfo blockInfo, ICoords coords, BlockState state, boolean postProcess) {
		this(blockInfo, coords, state);
		this.postProcess = postProcess;
	}
	
	public GottschTemplate.BlockInfo getBlockInfo() {
		return blockInfo;
	}

	public void setBlockInfo(GottschTemplate.BlockInfo blockInfo) {
		this.blockInfo = blockInfo;
	}

	public BlockState getState() {
		return state;
	}

	public void setState(BlockState state) {
		this.state = state;
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
	
	public boolean isPostProcess() {
		return postProcess;
	}

	public void setPostProcess(boolean postProcessCandidate) {
		this.postProcess = postProcessCandidate;
	}
}
