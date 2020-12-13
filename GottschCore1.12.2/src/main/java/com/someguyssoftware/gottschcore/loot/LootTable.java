/**
 * 
 */
package com.someguyssoftware.gottschcore.loot;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.someguyssoftware.gottschcore.GottschCore;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;

/**
 * @author Mark Gottschling on Jun 30, 2018
 *
 */
@Deprecated
public class LootTable {
	public static final LootTable EMPTY_LOOT_TABLE = new LootTable(new LootPool[0]);
	private final List<LootPool> pools;
	private String version;
	
	public LootTable(LootPool[] poolsIn) {
		this.pools = Lists.newArrayList(poolsIn);
	}

	/**
	 * returns a list of loot generated from a bunch of pools
	 */
	public List<ItemStack> generateLootFromPools(Random rand, LootContext context) {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();

		if (context.addLootTable(this)) {
			for (LootPool lootpool : this.pools) {
				// Treasure.logger.debug("lootPool -> {}", lootpool.getName());
				lootpool.generateLoot(list, rand, context);
			}

			context.removeLootTable(this);
		} else {
			GottschCore.logger.warn("Detected infinite loop in loot tables");
		}

		return list;
	}

	/**
	 * 
	 * @param inventory
	 * @param rand
	 * @param context
	 */
	public void fillInventory(IInventory inventory, Random rand, LootContext context) {
		List<ItemStack> list = this.generateLootFromPools(rand, context);
		List<Integer> list1 = this.getEmptySlotsRandomized(inventory, rand);
		this.shuffleItems(list, list1.size(), rand);

		for (ItemStack itemstack : list) {
			if (list1.isEmpty()) {
				// NOTE this shouldn't result in an empty chest - something else must be the issue
				GottschCore.logger.warn("Tried to over-fill a container");
				return;
			}

			if (itemstack.isEmpty()) {
				inventory.setInventorySlotContents(((Integer) list1.remove(list1.size() - 1)).intValue(), ItemStack.EMPTY);
			} 
			else {
				inventory.setInventorySlotContents(((Integer) list1.remove(list1.size() - 1)).intValue(), itemstack);
			}
		}
	}

	/**
	 * shuffles items by changing their order and splitting stacks
	 */
	private void shuffleItems(List<ItemStack> stacks, int emptySlotsSize, Random rand) {
		// removing the splitting of stacks.
		// TODO could process all items where count > 1 at the end, so that a stack of 30 arrows doesn't fill the entire chest
		// before other items gets added.
		
//		List<ItemStack> list = Lists.<ItemStack>newArrayList();
//		Iterator<ItemStack> iterator = stacks.iterator();
//
//		while (iterator.hasNext()) {
//			ItemStack itemstack = iterator.next();
//
//			if (itemstack.isEmpty()) {
//				iterator.remove();
//			} 
//			else if (itemstack.getCount() > 1) {
//				list.add(itemstack);
//				iterator.remove();
//			}
//		}
//
//		emptySlotsSize = emptySlotsSize - stacks.size();
//
//		while (emptySlotsSize > 0 && !list.isEmpty()) {
//			ItemStack itemstack2 = list.remove(MathHelper.getInt(rand, 0, list.size() - 1));
//			int i = MathHelper.getInt(rand, 1, itemstack2.getCount() / 2);
//			ItemStack itemstack1 = itemstack2.splitStack(i);
//
//			if (itemstack2.getCount() > 1 && rand.nextBoolean()) {
//				list.add(itemstack2);
//			} 
//			else {
//				stacks.add(itemstack2);
//			}
//
//			if (itemstack1.getCount() > 1 && rand.nextBoolean()) {
//				list.add(itemstack1);
//			} else {
//				stacks.add(itemstack1);
//			}
//		}
//
//		stacks.addAll(list);
		Collections.shuffle(stacks, rand);
	}

	private List<Integer> getEmptySlotsRandomized(IInventory inventory, Random rand) {
		List<Integer> list = Lists.<Integer>newArrayList();

		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			if (inventory.getStackInSlot(i).isEmpty()) {
				list.add(Integer.valueOf(i));
			}
		}

		Collections.shuffle(list, rand);
		return list;
	}

	// ======================== FORGE START
	// =============================================
	private boolean isFrozen = false;

	public void freeze() {
		this.isFrozen = true;
		for (LootPool pool : this.pools)
			pool.freeze();
	}

	public boolean isFrozen() {
		return this.isFrozen;
	}

	private void checkFrozen() {
		if (this.isFrozen())
			throw new RuntimeException("Attempted to modify TreasureLootTable after being finalized!");
	}

	public LootPool getPool(String name) {
		for (LootPool pool : this.pools) {
			if (name.equals(pool.getName()))
				return pool;
		}
		return null;
	}

	public LootPool removePool(String name) {
		checkFrozen();
		for (LootPool pool : this.pools) {
			if (name.equals(pool.getName())) {
				this.pools.remove(pool);
				return pool;
			}
		}

		return null;
	}

	public void addPool(LootPool pool) {
		checkFrozen();
		for (LootPool p : this.pools) {
			if (p == pool || p.getName().equals(pool.getName()))
				throw new RuntimeException("Attempted to add a duplicate pool to loot table: " + pool.getName());
		}
		this.pools.add(pool);
	}
	// ======================== FORGE END
	// ===============================================

	// ===============================================
	// ======================== TREASURE 2 MODIFIED
	public static class Serializer implements JsonDeserializer<LootTable>, JsonSerializer<LootTable> {
		public LootTable deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonobject = JsonUtils.getJsonObject(element, "loot table");
			String version = "";
			try {
				version = JsonUtils.getString(jsonobject, "version");
			}
			catch(JsonSyntaxException e) {
				// do nothing - leave version as empty
			}

			LootPool[] alootpool = (LootPool[]) JsonUtils.deserializeClass(jsonobject, "pools", new LootPool[0], context, LootPool[].class);
			LootTable lootTable = new LootTable(alootpool);
			lootTable.setVersion(version);
			return lootTable;
		}

		public JsonElement serialize(LootTable p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
			JsonObject jsonobject = new JsonObject();
			jsonobject.add("pools", p_serialize_3_.serialize(p_serialize_1_.pools));
			return jsonobject;
		}
	}
	// ======================== TREASURE 2 MODIFIED END
	// ===============================================

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
