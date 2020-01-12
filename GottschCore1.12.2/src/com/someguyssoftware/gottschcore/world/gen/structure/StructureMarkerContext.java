package com.someguyssoftware.gottschcore.world.gen.structure;

import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.block.state.IBlockState;

public class StructureMarkerContext {
	private ICoords coords;
	private IBlockState state;
	
	public StructureMarkerContext() {}

	public StructureMarkerContext(ICoords coords, IBlockState state) {
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

	public IBlockState getState() {
		return state;
	}

	public void setState(IBlockState state) {
		this.state = state;
	}
	
}
