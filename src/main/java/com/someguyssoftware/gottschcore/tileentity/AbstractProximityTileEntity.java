/**
 * 
 */
package com.someguyssoftware.gottschcore.tileentity;

import java.util.Random;

import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.spatial.Coords;
import com.someguyssoftware.gottschcore.spatial.ICoords;
import com.someguyssoftware.gottschcore.world.WorldInfo;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;

/**
 * 
 * @author Mark Gottschling on Feb 1, 2019
 *
 */
public abstract class AbstractProximityTileEntity extends AbstractModTileEntity implements IProximityTileEntity, ITickableTileEntity {
	private double proximity;
	private boolean isDead = false;

	/**
	 * 
	 * @param type
	 */
	public AbstractProximityTileEntity(TileEntityType<?> type) {
		super(type);
	}

	/**
	 * 
	 */
	public AbstractProximityTileEntity(TileEntityType<?> type, double proximity) {
		super(type);
		setProximity(proximity);
	}

	/**
	 * 
	 */
	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		try {
			// read the custom name
			if (nbt.contains("proximity", 8)) {
				this.proximity = nbt.getDouble("proximity");
			}
			else {
				this.proximity = 5;
			}
		} catch (Exception e) {
			GottschCore.LOGGER.error("Error reading AbstractProximity properties from NBT:", e);
		}
	}

	/**
	 * 
	 */
	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.putDouble("proximity", getProximity());
		return nbt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.minecraft.util.ITickable#update()
	 */
	@Override
	public void tick() {
		if (WorldInfo.isClientSide(getLevel())) {
			return;
		}

		// get all players within range
		boolean isTriggered = false;
		double proximitySq = getProximity() * getProximity();
		if (proximitySq < 1)
			proximitySq = 1;

		// for each player
		for (PlayerEntity player : getLevel().players()) {

			double distanceSq = player.distanceToSqr((double) getBlockPos().getX(), (double) getBlockPos().getY(),
					(double) getBlockPos().getZ());
			GottschCore.LOGGER.info("distanceSq -> {} to proximity TE @ -> {}; proximitySq -> {}", distanceSq, getBlockPos(), proximitySq);
			if (!isTriggered && !this.isDead && (distanceSq < proximitySq)) {
				GottschCore.LOGGER.info("PTE proximity was met.");
				isTriggered = true;
				// exectute action
				execute(this.getLevel(), new Random(), new Coords(this.getBlockPos()), new Coords(player.blockPosition()));

				// NOTE: does not self-destruct that is up to the execute action to perform
			}

			if (this.isDead)
				break;
		}
	}

	@Override
	abstract public void execute(World world, Random random, ICoords blockCoords, ICoords playerCoords);

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
