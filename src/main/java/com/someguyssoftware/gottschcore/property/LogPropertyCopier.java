/**
 * 
 */
package com.someguyssoftware.gottschcore.property;

import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;

/**
 * 
 * @author Mark Gottschling on Dec 26, 2019
 *
 */
public class LogPropertyCopier implements IPropertyCopier {
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    
    @Override
	public BlockState copy(BlockState source, BlockState dest) {
		if (dest.getProperties().contains(AXIS)) {
			dest = dest.with(AXIS, source.get(AXIS));
		}

		return dest;
	}

}
