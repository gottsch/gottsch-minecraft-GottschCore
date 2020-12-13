/**
 * 
 */
package com.someguyssoftware.gottschcore.property;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

/**
 * @author Mark Gottschling on Dec 23, 2019
 *
 */
public class StairsPropertyCopier implements IPropertyCopier {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyEnum<BlockStairs.EnumHalf> HALF = PropertyEnum.<BlockStairs.EnumHalf>create("half", BlockStairs.EnumHalf.class);
    public static final PropertyEnum<BlockStairs.EnumShape> SHAPE = PropertyEnum.<BlockStairs.EnumShape>create("shape", BlockStairs.EnumShape.class);

	@Override
	public IBlockState copy(IBlockState source, IBlockState dest) {
		if (dest.getProperties().containsKey(FACING)) {
			dest = dest.withProperty(FACING, (EnumFacing)source.getProperties().get(FACING));
		}
		if (dest.getProperties().containsKey(HALF)) {
			dest = dest.withProperty(HALF, (BlockStairs.EnumHalf)source.getProperties().get(HALF));
		}
		if (dest.getProperties().containsKey(SHAPE)) {
			dest = dest.withProperty(SHAPE, (BlockStairs.EnumShape)source.getProperties().get(SHAPE));
		}
		return dest;
	}

}
