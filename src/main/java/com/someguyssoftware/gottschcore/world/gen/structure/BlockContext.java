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
 * TODO rename this BlockContext or just use gottschcore.block.BlockContext
 * @author Mark Gottschling on Feb 8, 2020
 *
 */
@Deprecated
public class BlockContext {
	private ICoords coords;
	private BlockState state;
	
	public BlockContext() {}

	public BlockContext(ICoords coords, BlockState state) {
		super();
		this.coords = coords;
		this.state = state;
	}

	public ICoords getCoords() {
		return coords;
	}

	public void setCoords(ICoords coords) {
		this.coords = coords;
	}

	public BlockState getState() {
		return state;
	}

	public void setState(BlockState state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "BlockContext [coords=" + coords + ", state=" + state + "]";
	}
}
