/**
 * 
 */
package com.someguyssoftware.gottschcore.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

/**
 * @author Mark Gottschling on Mar 2, 2018
 *
 */
public class ModFallingBlock extends ModBlock {

	/**
	 * 
	 * @param modID
	 * @param name
	 * @param properties
	 */
	public ModFallingBlock(String modID, String name, Block.Properties properties) {
		super(modID, name, properties);
	}

	/**
	 * 
	 * @param state
	 * @param worldIn
	 * @param pos
	 * @param rand
	 */
	public void fall(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		if (worldIn.isEmptyBlock(pos.below()) || canFallThrough(worldIn.getBlockState(pos.below())) && pos.getY() >= 0) {
			FallingBlockEntity fallingblockentity = new FallingBlockEntity(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, worldIn.getBlockState(pos));
			this.falling(fallingblockentity, random);
			worldIn.addFreshEntity(fallingblockentity);
		}
	}

	protected void falling(FallingBlockEntity fallingEntity, Random random) {
	}

	public static boolean canFallThrough(BlockState state) {
		Block block = state.getBlock();
		Material material = state.getMaterial();
		return state.isAir() || block == Blocks.FIRE || material.isLiquid() || material.isReplaceable();
	}

	public void onEndFalling(World worldIn, BlockPos pos, BlockState fallingState, BlockState hitState) {
	}

	public void onBroken(World worldIn, BlockPos pos) {
	}
}
