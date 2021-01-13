/**
 * 
 */
package com.someguyssoftware.gottschcore.property;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

/**
 * @author Mark Gottschling on Dec 23, 2019
 *
 */
public class FenceGatePropertyCopier implements IPropertyCopier {
	private static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool OPEN = PropertyBool.create("open");
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyBool IN_WALL = PropertyBool.create("in_wall");

	@Override
	public IBlockState copy(IBlockState source, IBlockState dest) {
		if (dest.getProperties().containsKey(FACING)) {
			dest = dest.withProperty(FACING, (EnumFacing)source.getProperties().get(FACING));
		}
		if (dest.getProperties().containsKey(OPEN)) {
			dest = dest.withProperty(OPEN, ((Boolean)source.getProperties().get(OPEN)).booleanValue());
		}
		if (dest.getProperties().containsKey(POWERED)) {
			dest = dest.withProperty(POWERED, ((Boolean)source.getProperties().get(POWERED)).booleanValue());
		}
		if (dest.getProperties().containsKey(IN_WALL)) {
			dest = dest.withProperty(IN_WALL, ((Boolean)source.getProperties().get(IN_WALL)).booleanValue());
		}
		return dest;
	}
}
