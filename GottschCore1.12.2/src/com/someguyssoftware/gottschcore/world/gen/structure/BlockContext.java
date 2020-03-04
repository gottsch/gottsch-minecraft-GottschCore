package com.someguyssoftware.gottschcore.world.gen.structure;

import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.block.state.IBlockState;

/**
 * 
 * @author Mark Gottschling on Feb 8, 2020
 *
 */
public class BlockContext {
	private ICoords coords;
	private IBlockState state;
	
	public BlockContext() {}

	public BlockContext(ICoords coords, IBlockState state) {
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

	@Override
	public String toString() {
		return "BlockContext [coords=" + coords + ", state=" + state + "]";
	}
}
