package com.someguyssoftware.gottschcore.block;

import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.Direction;

/**
 * 
 * @author Mark Gottschling on Jan 11, 2020
 *
 */
public interface IFacingBlock extends IGottschCoreBlock {
	public static final EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class);
	
	Direction getFacing(BlockState state);
}
