/**
 * 
 */
package com.someguyssoftware.gottschcore.tileentity;

import java.util.Random;

import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.measurement.Quantity;
import com.someguyssoftware.gottschcore.random.RandomHelper;
import com.someguyssoftware.gottschcore.spatial.ICoords;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author Mark Gottschling on Jul 12, 2019
 *
 */
public class ProximitySpawnerTileEntity extends AbstractProximityTileEntity {
	private ResourceLocation mobName;
	private Quantity mobNum;
	private Double spawnRange = 1D;

	/**
	 * 
	 */
	public ProximitySpawnerTileEntity(TileEntityType<?> type) {
		super(type);
	}

	/**
	 * @param proximity
	 */
	public ProximitySpawnerTileEntity(TileEntityType<?> type, double proximity) {
		super(type, proximity);
	}

	/**
	 * 
	 */
	@Override
	public void read(CompoundNBT nbt) {
		super.read(nbt);
		try {
			// read the custom name
			if (nbt.contains("mobName", 8)) {
				this.mobName = new ResourceLocation(nbt.getString("mobName"));
			} else {
				// select a random mob
				EntityType<?> entityType = DungeonHooks.getRandomDungeonMob(new Random());
				this.mobName = entityType.getRegistryName();
			}

			int min = 1;
			int max = 1;
			if (nbt.contains("mobNumMin")) {
				min = nbt.getInt("mobNumMin");
			}
			if (nbt.contains("mobNumMax")) {
				min = nbt.getInt("mobNumMax");
			}
			this.mobNum = new Quantity(min, max);

			if (nbt.contains("spawnRange")) {
				Double spawnRange = nbt.getDouble("spawnRange");
				setSpawnRange(spawnRange);
			}

		} catch (Exception e) {
			GottschCore.LOGGER.error("Error reading ProximitySpanwer properties from NBT:", e);
		}
	}

	/**
	 * 
	 */
	@Override
	public CompoundNBT write(CompoundNBT tag) {
		super.write(tag);
		tag.putString("mobName", getMobName().toString());
		tag.putInt("mobNumMin", getMobNum().getMinInt());
		tag.putInt("mobNumMax", getMobNum().getMaxInt());
		tag.putDouble("spawnRange", getSpawnRange());
		return tag;
	}

	/**
	 * 
	 */
	@Override
	public void execute(World world, Random random, ICoords blockCoords, ICoords playerCoords) {

		int mobCount = RandomHelper.randomInt(random, getMobNum().getMinInt(), getMobNum().getMaxInt());

		for (int i = 0; i < mobCount; i++) {
			Entity entity = ForgeRegistries.ENTITIES.getValue(getMobName()).create(world);
			if (entity == null) {
				GottschCore.LOGGER.debug("unable to create entity -> {}", getMobName());
				selfDestruct();
				return;
			}

			entity.setLocationAndAngles((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D,
					MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);

			if (entity instanceof MobEntity) {
				MobEntity mobEntity = (MobEntity) entity;

				if (mobEntity.isNotColliding(world)) {
					mobEntity.rotationYawHead = mobEntity.rotationYaw;
					mobEntity.renderYawOffset = mobEntity.rotationYaw;

					(mobEntity).onInitialSpawn(world, world.getDifficultyForLocation(new BlockPos(entity)),
							SpawnReason.SPAWNER, (ILivingEntityData) null, (CompoundNBT) null);

					world.addEntity(entity);
					mobEntity.playAmbientSound();
				}
			}
		}
		// self destruct
		selfDestruct();
	}

	/**
	 * 
	 */
	private void selfDestruct() {
		this.setDead(true);
		this.getWorld().setBlockState(getPos(), Blocks.AIR.getDefaultState());
		this.getWorld().removeTileEntity(getPos());
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

	public Double getSpawnRange() {
		return spawnRange;
	}

	public void setSpawnRange(Double spawnRange) {
		this.spawnRange = spawnRange;
	}

}