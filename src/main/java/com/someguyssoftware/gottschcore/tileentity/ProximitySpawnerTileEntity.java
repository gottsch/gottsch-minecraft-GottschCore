package com.someguyssoftware.gottschcore.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.measurement.Quantity;
import com.someguyssoftware.gottschcore.random.RandomHelper;
import com.someguyssoftware.gottschcore.spatial.ICoords;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DungeonHooks;

/**
 * @author Mark Gottschling on Jul 12, 2019
 *
 */
public class ProximitySpawnerTileEntity extends AbstractProximityTileEntity {
	private static final String MOB_NAME = "mobName";

	private ResourceLocation mobName;
	private Quantity mobNum;

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
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		try {
			// read the custom name
			if (nbt.contains(MOB_NAME, 8)) {
				this.mobName = new ResourceLocation(nbt.getString(MOB_NAME));
			} else {
				// select a random mob
				EntityType<?> entityType = DungeonHooks.getRandomDungeonMob(new Random());
				this.mobName = entityType.getRegistryName();
			}
			if (getMobName() == null || StringUtils.isNullOrEmpty(getMobName().toString())) {
				defaultMobSpawnerSettings();
				return;
			}

			int min = 1;
			int max = 1;
			if (nbt.contains("mobNumMin")) {
				min = nbt.getInt("mobNumMin");
			}
			if (nbt.contains("mobNumMax")) {
				max = nbt.getInt("mobNumMax");
			}
			this.mobNum = new Quantity(min, max);
		} catch (Exception e) {
			GottschCore.LOGGER.error("Error reading ProximitySpanwer properties from NBT:", e);
		}
	}

	/**
	 * 
	 */
	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);
		if (getMobName() == null || StringUtils.isNullOrEmpty(getMobName().toString())) {        	
			defaultMobSpawnerSettings();
		}
		tag.putString("mobName", getMobName().toString());
		tag.putInt("mobNumMin", getMobNum().getMinInt());
		tag.putInt("mobNumMax", getMobNum().getMaxInt());
		return tag;
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
	public void execute(World world, Random random, ICoords blockCoords, ICoords playerCoords) {
		if (world.isClientSide()) {
			return;
		}

		int mobCount = RandomHelper.randomInt(random, getMobNum().getMinInt(), getMobNum().getMaxInt());

		// get a list of adjacent unoccupied blocks
		List<BlockPos> availableSpawnBlocks = new ArrayList<>();
		BlockPos proximityPos = getBlockPos();
		// TODO update to Coords
		if (world.getBlockState(proximityPos.north()).getMaterial().isReplaceable()) availableSpawnBlocks.add(proximityPos.north());
		if (world.getBlockState(proximityPos.east()).getMaterial().isReplaceable()) availableSpawnBlocks.add(proximityPos.east());
		if (world.getBlockState(proximityPos.south()).getMaterial().isReplaceable()) availableSpawnBlocks.add(proximityPos.south());
		if (world.getBlockState(proximityPos.west()).getMaterial().isReplaceable()) availableSpawnBlocks.add(proximityPos.west());

		if (world.getBlockState(proximityPos.offset(1, 0, 1)).getMaterial().isReplaceable()) availableSpawnBlocks.add(proximityPos.offset(1, 0, 1));
		if (world.getBlockState(proximityPos.offset(1, 0, -1)).getMaterial().isReplaceable()) availableSpawnBlocks.add(proximityPos.offset(1, 0, -1));
		if (world.getBlockState(proximityPos.offset(-1, 0, 1)).getMaterial().isReplaceable()) availableSpawnBlocks.add(proximityPos.offset(-1, 0, 1));
		if (world.getBlockState(proximityPos.offset(-1, 0, -1)).getMaterial().isReplaceable()) availableSpawnBlocks.add(proximityPos.offset(-1, 0, -1));

		for (int i = 0; i < mobCount; i++) {
			Optional<EntityType<?>> entityType = EntityType.byString(getMobName().toString());
			if (!entityType.isPresent()) {
				GottschCore.LOGGER.debug("unable to get entityType -> {}", getMobName());
				selfDestruct();
				return;
			}			
			GottschCore.LOGGER.debug("got entityType -> {}", getMobName());
			// select a random spawn coords
			BlockPos spawnPos = availableSpawnBlocks.get(random.nextInt(availableSpawnBlocks.size()));

			// NOTE added APPLE itemstack because mixin mods where looking for that to be there.
			if (entityType.get().spawn((ServerWorld)world, new ItemStack(Items.APPLE), null, spawnPos, SpawnReason.COMMAND, true, true) != null) {
				GottschCore.LOGGER.debug("should've created entity(s) at -> {}", getBlockPos());
			}
			else {
				GottschCore.LOGGER.debug("spawn failed");
			}
		}
		// self destruct
		selfDestruct();
	}

	/**
	 * 
	 */
	private void selfDestruct() {
		GottschCore.LOGGER.debug("te self-destructing @ {}", getBlockPos()); 
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