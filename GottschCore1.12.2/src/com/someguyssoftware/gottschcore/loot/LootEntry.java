/**
 * 
 */
package com.someguyssoftware.gottschcore.loot;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.someguyssoftware.gottschcore.loot.conditions.LootCondition;

import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.MathHelper;

/**
 * @author Mark Gottschling on Nov 6, 2018
 *
 */
public abstract class LootEntry {
	protected final String entryName;
	protected final int weight;
	protected final int quality;
	protected final LootCondition[] conditions;

	/**
	 * 
	 * @param weightIn
	 * @param qualityIn
	 * @param conditionsIn
	 * @param entryName
	 */
	protected LootEntry(int weightIn, int qualityIn, LootCondition[] conditionsIn, String entryName) {
		this.weight = weightIn;
		this.quality = qualityIn;
		this.conditions = conditionsIn;
		this.entryName = entryName;
	}

	/**
	 * Gets the effective weight based on the loot entry's weight and quality multiplied by looter's luck.
	 */
	public int getEffectiveWeight(float luck) {
		return Math.max(MathHelper.floor((float)this.weight + (float)this.quality * luck), 0);
	}

	/**
	 * 
	 * @return
	 */
	public String getEntryName() { 
		return this.entryName; 
	}

	public abstract void addLoot(Collection<ItemStack> stacks, Random rand, LootContext context);

	protected abstract void serialize(JsonObject json, JsonSerializationContext context);

	/**
	 * 
	 * @author Mark Gottschling on Nov 6, 2018
	 *
	 */
	public static class Serializer implements JsonDeserializer<LootEntry>, JsonSerializer<LootEntry> 	{
		public LootEntry deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonobject = JsonUtils.getJsonObject(element, "loot item");
			String s = JsonUtils.getString(jsonobject, "type");
			int i = JsonUtils.getInt(jsonobject, "weight", 1);
			int j = JsonUtils.getInt(jsonobject, "quality", 0);
			LootCondition[] alootcondition;

			if (jsonobject.has("conditions")) {
				alootcondition = (LootCondition[])JsonUtils.deserializeClass(jsonobject, "conditions", context, LootCondition[].class);
			}
			else {
				alootcondition = new LootCondition[0];
			}

			// TODO change - this method is unnecessary - the called method does nothing
			LootEntry ret = LootTableManager.deserializeJsonLootEntry(s, jsonobject, i, j, alootcondition);
			if (ret != null) return ret;

			if ("item".equals(s)) {
				return LootEntryItem.deserialize(jsonobject, context, i, j, alootcondition);
			}
			else if ("loot_table".equals(s)) {
				return LootEntryTable.deserialize(jsonobject, context, i, j, alootcondition);
			}
			else if ("empty".equals(s)) {
				return LootEntryEmpty.deserialize(jsonobject, context, i, j, alootcondition);
			}
			else {
				throw new JsonSyntaxException("Unknown loot entry type '" + s + "'");
			}
		}

		/**
		 * 
		 */
		public JsonElement serialize(LootEntry lootEntry, Type type, JsonSerializationContext context) {
			JsonObject jsonobject = new JsonObject();
			if (lootEntry.entryName != null && !lootEntry.entryName.startsWith("custom#"))
				jsonobject.addProperty("entryName", lootEntry.entryName);
			jsonobject.addProperty("weight", Integer.valueOf(lootEntry.weight));
			jsonobject.addProperty("quality", Integer.valueOf(lootEntry.quality));

			if (lootEntry.conditions.length > 0) {
				jsonobject.add("conditions", context.serialize(lootEntry.conditions));
			}

			// TODO change - useless - can be removed
			String lootEntryType = LootTableManager.getLootEntryType(lootEntry);
			
			if (lootEntryType != null) jsonobject.addProperty("type", lootEntryType);
			else
				if (lootEntry instanceof LootEntryItem) {
					jsonobject.addProperty("type", "item");
				}
				else if (lootEntry instanceof LootEntryTable) {
					jsonobject.addProperty("type", "loot_table");
				}
				else {
					if (!(lootEntry instanceof LootEntryEmpty)) {
						throw new IllegalArgumentException("Don't know how to serialize " + lootEntry);
					}

					jsonobject.addProperty("type", "empty");
				}

			lootEntry.serialize(jsonobject, context);
			return jsonobject;
		}
	}
}
