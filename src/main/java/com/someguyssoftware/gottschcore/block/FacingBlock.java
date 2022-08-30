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

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;


/**
 * 
 * @author Mark Gottschling on Jan 14, 2020
 *
 */
public class FacingBlock extends ModBlock implements IFacingBlock {
	
	/**
	 * 
	 * @param properties
	 */
	public FacingBlock(Block.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}
	
	/**
	 * 
	 * @param modID
	 * @param name
	 * @param materialIn
	 */
	@Deprecated
	public FacingBlock(String modID, String name, Block.Properties properties) {
		super(modID, name, properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}
	
	/**
	 * 
	 */
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

    
	/**
	 * 
	 */
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
    	return this.defaultBlockState().setValue(FACING, context.getPlayer().getDirection().getOpposite());
    }
    
	/**
	 * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
	 * fine.
	 */
    @Deprecated
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(getFacing(state)));
	}
	
	/**
	 * 
	 * @param state
	 * @return
	 */
	@Override
	public Direction getFacing(BlockState state) {
		return state.getValue(FACING);
	}
}
