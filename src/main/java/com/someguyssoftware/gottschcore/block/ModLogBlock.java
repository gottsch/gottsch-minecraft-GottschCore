/**
 * 
 */
package com.someguyssoftware.gottschcore.block;

import net.minecraft.block.Block;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MaterialColor;

/**
 * 
 * @author Mark Gottschling on Jan 22, 2021
 *
 */
public class ModLogBlock extends RotatedPillarBlock {
	
	public ModLogBlock(String modID, String name, MaterialColor verticalColorIn, Block.Properties properties) {
		super(properties.strength(2.0F).sound(SoundType.WOOD));
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
