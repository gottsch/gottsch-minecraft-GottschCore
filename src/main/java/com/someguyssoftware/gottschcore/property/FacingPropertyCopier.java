/**
 * 
 */
package com.someguyssoftware.gottschcore.property;

import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.Direction;

/**
 * @author Mark Gottschling on Dec 23, 2019
 *
 */
public class FacingPropertyCopier implements IPropertyCopier {
	public static final EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class);

	@Override
	public BlockState copy(BlockState source, BlockState dest) {
		if (dest.getProperties().contains(FACING)) {
			dest = dest.setValue(FACING, source.getValue(FACING));
		}
		return dest;
	}
}
