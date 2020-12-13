/**
 * 
 */
package com.someguyssoftware.gottschcore.loot;

import java.util.Collection;
import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.someguyssoftware.gottschcore.loot.conditions.LootCondition;

import net.minecraft.item.ItemStack;

/**
 * @author Mark Gottschling on Nov 6, 2018
 *
 */
@Deprecated
public class LootEntryEmpty extends LootEntry {

	/**
	 * 
	 * @param weightIn
	 * @param qualityIn
	 * @param conditionsIn
	 * @param entryName
	 */
	public LootEntryEmpty(int weightIn, int qualityIn, LootCondition[] conditionsIn, String entryName) {
		super(weightIn, qualityIn, conditionsIn, entryName);
	}

	/**
	 * 
	 */
	@Override
	public void addLoot(Collection<ItemStack> stacks, Random rand, LootContext context) {   }

	/**
	 * 
	 */
	@Override
	protected void serialize(JsonObject json, JsonSerializationContext context) {   }

	/**
	 * 
	 * @param object
	 * @param deserializationContext
	 * @param weightIn
	 * @param qualityIn
	 * @param conditionsIn
	 * @return
	 */
	public static LootEntryEmpty deserialize(
			JsonObject object, 
			JsonDeserializationContext deserializationContext, 
			int weightIn, 
			int qualityIn, 
			LootCondition[] conditionsIn) {

		return new LootEntryEmpty(
				weightIn, 
				qualityIn, 
				conditionsIn, 
				LootTableManager.readLootEntryName(object, "empty"));
	}
}
