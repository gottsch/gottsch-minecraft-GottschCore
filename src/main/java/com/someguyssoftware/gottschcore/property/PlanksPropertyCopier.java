/**
 * 
 */
package com.someguyssoftware.gottschcore.property;

import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;

/**
 * 
 * @author Mark Gottschling on Dec 28, 2019
 *
 */
public class PlanksPropertyCopier implements IPropertyCopier {
    public static final PropertyEnum<BlockPlanks.EnumType> VARIANT = PropertyEnum.<BlockPlanks.EnumType>create("variant", BlockPlanks.EnumType.class);

	@Override
	public IBlockState copy(IBlockState source, IBlockState dest) {
		if (dest.getProperties().containsKey(VARIANT)) {
			dest = dest.withProperty(VARIANT, (BlockPlanks.EnumType)source.getProperties().get(VARIANT));
		}
		return dest;
	}
}
