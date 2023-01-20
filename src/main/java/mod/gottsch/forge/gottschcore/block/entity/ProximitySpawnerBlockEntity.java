/*
 * This file is part of  Treasure2.
 * Copyright (c) 2019 Mark Gottschling (gottsch)
 *
 * Treasure2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Treasure2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Treasure2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.gottschcore.block.entity;

import java.util.Optional;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.someguyssoftware.gottschcore.GottschCore;

import mod.gottsch.forge.gottschcore.random.RandomHelper;
import mod.gottsch.forge.gottschcore.size.Quantity;
import mod.gottsch.forge.gottschcore.spatial.Coords;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import mod.gottsch.forge.gottschcore.world.WorldInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.DungeonHooks;

/**
 * @author Mark Gottschling on Jul 12, 2019
 *
 */
public class ProximitySpawnerBlockEntity extends AbstractProximityBlockEntity {
	private static final String MOB_NAME = "mobName";
	private static final String MIN_MOBS = "mobNumMin";
	private static final String MAX_MOBS = "mobNumMax";

	private ResourceLocation mobName;
	private Quantity mobNum;

	/**
	 * 
	 */
	public ProximitySpawnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	/**
	 * @param proximity
	 */
	public ProximitySpawnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, double proximity) {
		super(type, pos, state, proximity);
	}

	/**
	 * 
	 */
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		try {
			// read the custom name
			if (tag.contains(MOB_NAME, 8)) {
				this.mobName = new ResourceLocation(tag.getString(MOB_NAME));
			} else {
				// select a random mob
				EntityType<?> entityType = DungeonHooks.getRandomDungeonMob(new Random());
				this.mobName = entityType.getRegistryName();
			}
			if (getMobName() == null || StringUtils.isBlank(getMobName().toString())) {
				defaultMobSpawnerSettings();
				return;
			}

			int min = 1;
			int max = 1;
			if (tag.contains(MIN_MOBS)) {
				min = tag.getInt(MIN_MOBS);
			}
			if (tag.contains(MAX_MOBS)) {
				max = tag.getInt(MAX_MOBS);
			}
			this.mobNum = new Quantity(min, max);
		} catch (Exception e) {
			GottschCore.LOGGER.error("error reading ProximitySpanwerBlockEntity properties from tag:", e);
		}
	}

	/**
	 * 
	 */
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (getMobName() == null || StringUtils.isBlank(getMobName().toString())) {        	
			defaultMobSpawnerSettings();
		}
		tag.putString(MOB_NAME, getMobName().toString());
		tag.putInt(MIN_MOBS, getMobNum().getMinInt());
		tag.putInt(MAX_MOBS, getMobNum().getMaxInt());
	}

	/**
	 * 
	 */
	private void defaultMobSpawnerSettings() {
		setMobName(new ResourceLocation("minecraft", "zombie"));
		setMobNum(new Quantity(1, 1));
		setProximity(5.0D);
	}

	/**
	 * 
	 */
	@Override
	public void execute(Level world, Random random, ICoords blockCoords, ICoords playerCoords) {
		if (world.isClientSide()) {
			return;
		}
		ServerLevel level = (ServerLevel)world;

		Optional<EntityType<?>> entityType = EntityType.byString(getMobName().toString());
		if (!entityType.isPresent()) {
			GottschCore.LOGGER.debug("unable to get entityType -> {}", getMobName());
			selfDestruct();
			return;
		}
		GottschCore.LOGGER.debug("got entityType -> {}", getMobName());

		int count = 0;
		int numberOfMobs = RandomHelper.randomInt(random, getMobNum().getMinInt(), getMobNum().getMaxInt());
		for (int x = 0; x < numberOfMobs; x++) {
			for (int i = 0; i < 20; i++) { // 20 tries
				int spawnX = blockCoords.getX() + Mth.nextInt(random, 1, 2) * Mth.nextInt(random, -1, 1);
				int spawnY = blockCoords.getY() + Mth.nextInt(random, 1, 2) * Mth.nextInt(random, -1, 1);
				int spawnZ = blockCoords.getZ() + Mth.nextInt(random, 1, 2) * Mth.nextInt(random, -1, 1);

				ICoords spawnCoords = new Coords(spawnX, spawnY, spawnZ);

				boolean isSpawned = false;
				if (!WorldInfo.isClientSide(level)) {
					SpawnPlacements.Type placement = SpawnPlacements.getPlacementType(entityType.get());
					if (NaturalSpawner.isSpawnPositionOk(placement, level, spawnCoords.toPos(), entityType.get())) {
						Entity mob = entityType.get().create(level);
						mob.setPos((double)spawnX, (double)spawnY, (double)spawnZ);
						level.addFreshEntityWithPassengers(mob);
						isSpawned = true;
					}

					if (isSpawned) {
						for (int p = 0; p < 20; p++) {
							double xSpeed = random.nextGaussian() * 0.02D;
							double ySpeed = random.nextGaussian() * 0.02D;
							double zSpeed = random.nextGaussian() * 0.02D;
							level.sendParticles(ParticleTypes.POOF, spawnCoords.getX() + 0.5D, spawnCoords.getY(), spawnCoords.getZ() + 0.5D, 1, xSpeed, ySpeed, zSpeed, (double)0.15F);
						}
						count++;
						// if the desired count is met within this try block then break
						if (count >= numberOfMobs) {
							break;
						}
					}
				}
			}
			// if the desired count is already met then break else do any 20x tries
			if (count >= numberOfMobs) {
				break;
			}
		}
		
		//		 get a list of adjacent unoccupied blocks
		//		List<BlockPos> availableSpawnBlocks = new ArrayList<>();
		//		BlockPos proximityPos = getBlockPos();
		//		// TODO update to Coords
		//		if (world.getBlockState(proximityPos.north()).getMaterial().isReplaceable()) availableSpawnBlocks.add(proximityPos.north());
		//		if (world.getBlockState(proximityPos.east()).getMaterial().isReplaceable()) availableSpawnBlocks.add(proximityPos.east());
		//		if (world.getBlockState(proximityPos.south()).getMaterial().isReplaceable()) availableSpawnBlocks.add(proximityPos.south());
		//		if (world.getBlockState(proximityPos.west()).getMaterial().isReplaceable()) availableSpawnBlocks.add(proximityPos.west());
		//
		//		if (world.getBlockState(proximityPos.offset(1, 0, 1)).getMaterial().isReplaceable()) availableSpawnBlocks.add(proximityPos.offset(1, 0, 1));
		//		if (world.getBlockState(proximityPos.offset(1, 0, -1)).getMaterial().isReplaceable()) availableSpawnBlocks.add(proximityPos.offset(1, 0, -1));
		//		if (world.getBlockState(proximityPos.offset(-1, 0, 1)).getMaterial().isReplaceable()) availableSpawnBlocks.add(proximityPos.offset(-1, 0, 1));
		//		if (world.getBlockState(proximityPos.offset(-1, 0, -1)).getMaterial().isReplaceable()) availableSpawnBlocks.add(proximityPos.offset(-1, 0, -1));
		//
		//		for (int i = 0; i < mobCount; i++) {
		//			Optional<EntityType<?>> entityType = EntityType.byString(getMobName().toString());
		//			if (!entityType.isPresent()) {
		//				GottschCore.LOGGER.debug("unable to get entityType -> {}", getMobName());
		//				selfDestruct();
		//				return;
		//			}			
		//			GottschCore.LOGGER.debug("got entityType -> {}", getMobName());
		//			// select a random spawn coords
		//			BlockPos spawnPos = availableSpawnBlocks.get(random.nextInt(availableSpawnBlocks.size()));
		//
		//			// NOTE added APPLE itemstack because mixin mods where looking for that to be there.
		//			if (entityType.get().spawn((ServerLevel)world, new ItemStack(Items.APPLE), null, spawnPos, MobSpawnType.COMMAND, true, true) != null) {
		//				GottschCore.LOGGER.debug("should've created entity(s) at -> {}", getBlockPos());
		//			}
		//			else {
		//				GottschCore.LOGGER.debug("spawn failed");
		//			}
		//		}
		// self destruct
		selfDestruct();
	}

	/**
	 * 
	 */
	private void selfDestruct() {
		GottschCore.LOGGER.debug("self-destructing @ {}", getBlockPos()); 
		this.setDead(true);
		this.getLevel().setBlock(getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
		this.getLevel().removeBlockEntity(getBlockPos());
	}

	/**
	 * @return the mobName
	 */
	public ResourceLocation getMobName() {
		return mobName;
	}

	/**
	 * @param mobName the mobName to set
	 */
	public void setMobName(ResourceLocation mobName) {
		this.mobName = mobName;
	}

	/**
	 * @return the mobNum
	 */
	public Quantity getMobNum() {
		return mobNum;
	}

	/**
	 * @param mobNum the mobNum to set
	 */
	public void setMobNum(Quantity mobNum) {
		this.mobNum = mobNum;
	}

}