/**
 * 
 */
package com.someguyssoftware.gottschcore.tileentity;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

/**
 * @author Mark Gottschling onJan 3, 2018
 *
 */
public abstract class AbstractModTileEntity extends TileEntity {

	public AbstractModTileEntity(TileEntityType<?> type) {
		super(type);
	}

	/**
	 * 
	 */
	@Override
	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(getBlockPos(), 0, getUpdateTag());
	}

	/**
	 * 
	 */
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		save(pkt.getTag());
	}

	/*
	 * Creates a tag containing the TileEntity information, used by vanilla to
	 * transmit from server to client
	 */
	@Override
	public CompoundNBT getUpdateTag() {
		return save(new CompoundNBT());
	}

	/*
	 * Populates this TileEntity with information from the tag, used by vanilla to
	 * transmit from server to client
	 */
	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		this.load(state, tag);
	}
}
