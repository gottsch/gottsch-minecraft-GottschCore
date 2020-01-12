/**
 * 
 */
package com.someguyssoftware.gottschcore.property;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

/**
 * 
 * @author Mark Gottschling on Dec 26, 2019
 *
 */
public class OldLogPropertyCopier implements IPropertyCopier {
    public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.<EnumFacing.Axis>create("axis", EnumFacing.Axis.class);
    public static final PropertyEnum<BlockLog.EnumAxis> LOG_AXIS = PropertyEnum.<BlockLog.EnumAxis>create("axis", BlockLog.EnumAxis.class);
    public static final PropertyEnum<BlockPlanks.EnumType> VARIANT = PropertyEnum.<BlockPlanks.EnumType>create("variant", BlockPlanks.EnumType.class, new Predicate<BlockPlanks.EnumType>() {
        public boolean apply(@Nullable BlockPlanks.EnumType type) {
            return type.getMetadata() < 4;
        }
    });
    
    @Override
	public IBlockState copy(IBlockState source, IBlockState dest) {
		if (dest.getProperties().containsKey(VARIANT)) {
			dest = dest.withProperty(VARIANT, (BlockPlanks.EnumType)source.getProperties().get(VARIANT));
		}
		if (dest.getProperties().containsKey(AXIS)) {
			dest = dest.withProperty(AXIS, (EnumFacing.Axis)source.getProperties().get(AXIS));
		}
		if (dest.getProperties().containsKey(LOG_AXIS)) {
			dest = dest.withProperty(LOG_AXIS, (BlockLog.EnumAxis)source.getProperties().get(LOG_AXIS));
		}
		return dest;
	}

}
