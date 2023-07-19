/*
 * This file is part of  GottschCore.
 * Copyright (c) 2019 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.gottschcore.block.entity;

import mod.gottsch.forge.gottschcore.GottschCore;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 
 * @author Mark Gottschling on Feb 1, 2019
 *
 */
public abstract class AbstractProximityBlockEntity extends BlockEntity implements IProximityBlockEntity {
	public static final String PROXIMITY_TAG = "proximity";
	public static final String IS_DEAD = "isDead";
	
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
			if (tag.contains(PROXIMITY_TAG, 8)) {
				this.proximity = tag.getDouble(PROXIMITY_TAG);
			}
			else {
				this.proximity = 5;
			}
			if (tag.contains(IS_DEAD)) {
				this.isDead = tag.getBoolean(IS_DEAD);
			}
		} catch (Exception e) {
			GottschCore.LOGGER.error("error reading AbstractProximityBlockEntity properties from nbt:", e);
		}
	}

	/**
	 * 
	 */
	@Override
	   protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putDouble(PROXIMITY_TAG, getProximity());
		tag.putBoolean(IS_DEAD, isDead());
	}

	@Override
	abstract public void execute(Level world, RandomSource random, ICoords blockCoords, ICoords playerCoords);

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
