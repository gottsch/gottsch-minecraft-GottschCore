package com.someguyssoftware.gottschcore.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.measurement.Quantity;
import com.someguyssoftware.gottschcore.random.RandomHelper;
import com.someguyssoftware.gottschcore.spatial.ICoords;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.DungeonHooks;

/**
 * @author Mark Gottschling on Jul 12, 2019
 *
 */
public class ProximitySpawnerTileEntity extends AbstractProximityBlockEntity {
	private static final String MOB_NAME = "mobName";

	private ResourceLocation mobName;
	private Quantity mobNum;

	/**
	 * 
	 */
	public ProximitySpawnerTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	/**
	 * @param proximity
	 */
	public ProximitySpawnerTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, double proximity) {
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
			if (tag.contains("mobNumMin")) {
				min = tag.getInt("mobNumMin");
			}
			if (tag.contains("mobNumMax")) {
				max = tag.getInt("mobNumMax");
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
	   protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (getMobName() == null || StringUtils.isBlank(getMobName().toString())) {        	
			defaultMobSpawnerSettings();
		}
		tag.putString("mobName", getMobName().toString());
		tag.putInt("mobNumMin", getMobNum().getMinInt());
		tag.putInt("mobNumMax", getMobNum().getMaxInt());
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
			if (entityType.get().spawn((ServerLevel)world, new ItemStack(Items.APPLE), null, spawnPos, MobSpawnType.COMMAND, true, true) != null) {
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