package com.someguyssoftware.gottschcore.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

/**
 * A thin wrapper on Block to add some basic additions/conveniences/
 * 
 * @author Mark Gottschling on Jul 23, 2017
 *
 */
public class ModBlock extends Block {
	private boolean normalCube = true;
	
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
	
	/**
	 * 
	 */
	@Override
	public boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos) {
		return normalCube;
	}
	
	/**
	 * 
	 * @param normalCube
	 */
	protected void setNormalCube(boolean normalCube) {
		this.normalCube = normalCube;
	}
}
