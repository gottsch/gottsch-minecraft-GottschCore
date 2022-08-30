package com.someguyssoftware.gottschcore.block;


import net.minecraft.world.level.block.Block;

/**
 * A thin wrapper on Block to add some basic additions/conveniences/
 * 
 * @author Mark Gottschling on Jul 23, 2017
 *
 */
public class ModBlock extends Block {
	
	public ModBlock(Properties properties) {
		super(properties);
	}
	
	/**
	 * 
	 * @param modID
	 * @param name
	 * @param material
	 */
	public ModBlock(String modID, String name, Block.Properties properties) {
		super(properties);
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
