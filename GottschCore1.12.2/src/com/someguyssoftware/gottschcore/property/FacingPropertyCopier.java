/**
 * 
 */
package com.someguyssoftware.gottschcore.property;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

/**
 * @author Mark Gottschling on Dec 23, 2019
 *
 */
public class FacingPropertyCopier implements IPropertyCopier {
	private static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	@Override
	public IBlockState copy(IBlockState source, IBlockState dest) {
		if (dest.getProperties().containsKey(FACING)) {
			dest = dest.withProperty(FACING, (EnumFacing)source.getProperties().get(FACING));
		}
		return dest;
	}
}
