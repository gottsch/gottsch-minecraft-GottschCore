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

import java.util.Random;

import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.spatial.ICoords;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 
 * @author Mark Gottschling on Feb 1, 2019
 *
 */
public abstract class AbstractProximityBlockEntity extends AbstractModBlockEntity implements IProximityTileEntity {
	private double proximity;
	private boolean isDead = false;

	/**
	 * 
	 * @param type
	 * @param pos
	 * @param state
	 */
	public AbstractProximityBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	/**
	 * 
	 * @param type
	 * @param pos
	 * @param state
	 * @param proximity
	 */
	public AbstractProximityBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, double proximity) {
		super(type, pos, state);
		setProximity(proximity);
	}
	   
	/**
	 * 
	 */
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		try {
			// read the custom name
			if (tag.contains("proximity", 8)) {
				this.proximity = tag.getDouble("proximity");
			}
			else {
				this.proximity = 5;
			}
			if (tag.contains("isDead")) {
				this.isDead = tag.getBoolean("isDead");
			}
		} catch (Exception e) {
			GottschCore.LOGGER.error("Error reading AbstractProximity properties from NBT:", e);
		}
	}

	/**
	 * 
	 */
	@Override
	public CompoundTag save(CompoundTag tag) {
		super.save(tag);
		tag.putDouble("proximity", getProximity());
		tag.putBoolean("isDead", isDead());
		return tag;
	}

//  tick() and related methods are no longer part of TileEntity (BlockEntity), but are part of Block, and the ticker is called from BlockBehaviour
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see net.minecraft.util.ITickable#update()
//	 */
//	public void tick(Level level, BlockPos pos, BlockState state, T p_155256_) {
////		public void tick() {
//	
//		if (WorldInfo.isClientSide(level)) {
//			return;
//		}
//
//		// get all players within range
//		boolean isTriggered = false;
//		double proximitySq = getProximity() * getProximity();
//		if (proximitySq < 1)
//			proximitySq = 1;
//
//		// for each player
//		for (Player player : getLevel().players()) {
//
//			double distanceSq = player.distanceToSqr((double) getBlockPos().getX(), (double) getBlockPos().getY(),
//					(double) getBlockPos().getZ());
//			if (!isTriggered && !this.isDead && (distanceSq < proximitySq)) {
//				isTriggered = true;
//				// exectute action
//				execute(this.getLevel(), new Random(), new Coords(this.getBlockPos()), new Coords(player.blockPosition()));
//
//				// NOTE: does not self-destruct that is up to the execute action to perform
//			}
//
//			if (this.isDead)
//				break;
//		}
//	}

	@Override
	abstract public void execute(Level world, Random random, ICoords blockCoords, ICoords playerCoords);

	/**
	 * @return the proximity
	 */
	@Override
	public double getProximity() {
		return proximity;
	}

	/**
	 * @param proximity the proximity to set
	 */
	@Override
	public void setProximity(double proximity) {
		this.proximity = proximity;
	}

	/**
	 * @return the isDead
	 */
	public boolean isDead() {
		return isDead;
	}

	/**
	 * @param isDead the isDead to set
	 */
	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}

}
