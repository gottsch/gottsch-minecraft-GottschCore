/**
 * 
 */
package com.someguyssoftware.gottschcore.property;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;

/**
 * @author Mark Gottschling on Dec 23, 2019
 *
 */
public class StairsPropertyCopier implements IPropertyCopier {
    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;

	@Override
	public BlockState copy(BlockState source, BlockState dest) {
		if (dest.getProperties().contains(FACING)) {
			dest = dest.setValue(FACING, source.getValue(FACING));
		}
		if (dest.getProperties().contains(HALF)) {
			dest = dest.setValue(HALF, source.getValue(HALF));
		}
		if (dest.getProperties().contains(SHAPE)) {
			dest = dest.setValue(SHAPE, source.getValue(SHAPE));
		}
		return dest;
	}

}
