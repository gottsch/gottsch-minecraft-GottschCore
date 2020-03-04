  
package com.someguyssoftware.gottschcore.block;

import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.Direction;

/**
 * 
 * @author Mark Gottschling on Jan 15, 2020
 *
 */
public interface IBasedBlock extends IGottschCoreBlock {
	public static final EnumProperty<Direction> BASE = EnumProperty.create("base", Direction.class);
	
	Direction getBase(BlockState state);
}
