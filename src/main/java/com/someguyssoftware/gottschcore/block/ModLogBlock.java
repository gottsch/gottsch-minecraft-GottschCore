/**
 * 
 */
package com.someguyssoftware.gottschcore.block;

import net.minecraft.block.Block;
import net.minecraft.block.LogBlock;
import net.minecraft.block.material.MaterialColor;

/**
 * 
 * @author Mark Gottschling on Jan 22, 2021
 *
 */
public class ModLogBlock extends LogBlock {
	
	public ModLogBlock(String modID, String name, MaterialColor verticalColorIn, Block.Properties properties) {
		super(verticalColorIn, properties);
		setBlockName(modID, name);
	}

	/**
	 * 
	 * @param modID
	 * @param name
	 */
	public void setBlockName(String modID, String name) {
		setRegistryName(modID, name);
	}
}
