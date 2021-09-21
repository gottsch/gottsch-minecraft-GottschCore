/**
 * 
 */
package com.someguyssoftware.gottschcore.tileentity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(getBlockPos(), 0, getUpdateTag());
   }
   
	/**
	 * 
	 */
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		save(pkt.getTag());
	}

	/*
	 * Creates a tag containing the TileEntity information, used by vanilla to
	 * transmit from server to client
	 */
	@Override
	public CompoundTag getTileData() {
		return save(new CompoundTag());
	}
}
