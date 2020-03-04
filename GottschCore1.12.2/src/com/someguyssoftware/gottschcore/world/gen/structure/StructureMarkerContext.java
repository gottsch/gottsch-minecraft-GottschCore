package com.someguyssoftware.gottschcore.world.gen.structure;

import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.block.state.IBlockState;

/**
 * Kept for legacy compatibility purposes.
 * 
 * @author Mark Gottschling on Mar 2, 2020
 *
 */
@Deprecated
public class StructureMarkerContext extends BlockContext {

	public StructureMarkerContext() {
		super();
	}

	public StructureMarkerContext(ICoords coords, IBlockState state) {
		super(coords, state);
	}
}