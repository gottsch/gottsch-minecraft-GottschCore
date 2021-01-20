package com.someguyssoftware.gottschcore.property;

import net.minecraft.block.BlockState;

public interface IPropertyCopier {

	BlockState copy(BlockState source, BlockState dest);

}