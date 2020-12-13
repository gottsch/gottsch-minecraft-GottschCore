package com.someguyssoftware.gottschcore.property;

import net.minecraft.block.state.IBlockState;

public interface IPropertyCopier {

	IBlockState copy(IBlockState source, IBlockState dest);

}