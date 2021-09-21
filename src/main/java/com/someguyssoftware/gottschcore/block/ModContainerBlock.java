/**
 * 
 */
package com.someguyssoftware.gottschcore.block;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This class replaces BlockContainer so that it can extend ModBlock
 * 
 * @author Mark Gottschling onJan 2, 2018
 *
 */
public abstract class ModContainerBlock extends BaseEntityBlock /*ModBlock /*implements ITileEntityProvider*/ {

	/**
	 * 
	 * @param modID
	 * @param name
	 * @param properties
	 */
	public ModContainerBlock(String modID, String name, Block.Properties properties) {
		super(modID, name, properties);
	}

	// TODO copy main things from ModBlock
}
