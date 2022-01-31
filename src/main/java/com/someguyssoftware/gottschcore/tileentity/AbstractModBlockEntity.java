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
package com.someguyssoftware.gottschcore.tileentity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author Mark Gottschling onJan 3, 2018
 *
 */
public abstract class AbstractModBlockEntity extends BlockEntity {
	/**
	 * 
	 * @param type
	 * @param pos
	 * @param state
	 */
	public AbstractModBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	@Nullable
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	/**
	 * Data to send to client for sync-ing
	 * ex.
	 *   CompoundTag tag = new CompoundTag();
      *  ContainerHelper.saveAllItems(tag, this.items, true);
	 */
	@Override
	public CompoundTag getUpdateTag() {
		return super.getUpdateTag();
	}

	/**
	 * Not necessary to override
	 */
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
	}

	/*
	 * Creates a tag containing the TileEntity information, used by vanilla to
	 * transmit from server to client
	 * Not necessary to override
	 */
	@Override
	public CompoundTag getTileData() {
		return super.getTileData();
//		return save(new CompoundTag());
	}
}
