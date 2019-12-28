/**
 * 
 */
package com.someguyssoftware.gottschcore.property;

import net.minecraft.block.BlockLog;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

/**
 * 
 * @author Mark Gottschling on Dec 26, 2019
 *
 */
public class VinePropertyCopier implements IPropertyCopier {
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    
    @Override
	public IBlockState copy(IBlockState source, IBlockState dest) {
		if (dest.getProperties().containsKey(UP)) {
			dest = dest.withProperty(UP, ((Boolean)source.getValue(UP)).booleanValue());
		}
		if (dest.getProperties().containsKey(NORTH)) {
			dest = dest.withProperty(NORTH, ((Boolean)source.getValue(NORTH)).booleanValue());
		}
		if (dest.getProperties().containsKey(EAST)) {
			dest = dest.withProperty(EAST, ((Boolean)source.getValue(EAST)).booleanValue());
		}
		if (dest.getProperties().containsKey(SOUTH)) {
			dest = dest.withProperty(SOUTH, ((Boolean)source.getValue(SOUTH)).booleanValue());
		}
		if (dest.getProperties().containsKey(WEST)) {
			dest = dest.withProperty(WEST, ((Boolean)source.getValue(WEST)).booleanValue());
		}
		return dest;
	}

}
