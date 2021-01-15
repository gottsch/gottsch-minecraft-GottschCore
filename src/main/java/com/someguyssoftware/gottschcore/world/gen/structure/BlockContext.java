package com.someguyssoftware.gottschcore.world.gen.structure;

import com.someguyssoftware.gottschcore.spatial.ICoords;

import net.minecraft.block.BlockState;


/**
 * 
 * @author Mark Gottschling on Feb 8, 2020
 *
 */
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
