/*
 * This file is part of  GottschCore.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
 * 
 * All rights reserved.
 *
 * GottschCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GottschCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GottschCore.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package com.someguyssoftware.gottschcore.block;

import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.FallingBlock;


/**
 * @author Mark Gottschling on Mar 2, 2018
 *
 */
public class ModFallingBlock extends FallingBlock {

	/**
	 * 
	 * @param modID
	 * @param name
	 * @param properties
	 */
	public ModFallingBlock(String modID, String name, AirBlock.Properties properties) {
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
	 * @param state
	 * @param worldIn
	 * @param pos
	 * @param rand
	 */
//	   public void tick(BlockState blockState, ServerLevel level, BlockPos pos, Random random) {
//		      if (isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
//		         FallingBlockEntity fallingblockentity = new FallingBlockEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, level.getBlockState(pos));
//		         this.falling(fallingblockentity);
//		         level.addFreshEntity(fallingblockentity);
//		      }
//		   }
//	   
//	   protected void falling(FallingBlockEntity blockEntity) {
//	   }
//
//	   public static boolean isFree(BlockState state) {
//		      Material material = state.getMaterial();
//		      return state.isAir() || state.is(BlockTags.FIRE) || material.isLiquid() || material.isReplaceable();
//		   }

//	public void onEndFalling(World worldIn, BlockPos pos, BlockState fallingState, BlockState hitState) {
//	}
//
//	public void onBroken(World worldIn, BlockPos pos) {
//	}
}
