/**
 * 
 */
package com.someguyssoftware.gottschcore.loot.conditions;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.someguyssoftware.gottschcore.loot.LootContext;

import net.minecraft.util.ResourceLocation;

/**
 * @author Mark Gottschling on Nov 7, 2018
 *
 */
public interface LootCondition {
	boolean testCondition(Random rand, LootContext context);

	/**
	 * 
	 * @author Mark Gottschling on Nov 7, 2018
	 *
	 * @param <T>
	 */
	public abstract static class Serializer<T extends LootCondition> {
		private final ResourceLocation lootTableLocation;
		private final Class<T> conditionClass;

		protected Serializer(ResourceLocation location, Class<T> clazz) {
			this.lootTableLocation = location;
			this.conditionClass = clazz;
		}

		public ResourceLocation getLootTableLocation() {
			return this.lootTableLocation;
		}

		public Class<T> getConditionClass() {
			return this.conditionClass;
		}

		public abstract void serialize(JsonObject json, T value, JsonSerializationContext context);

		public abstract T deserialize(JsonObject json, JsonDeserializationContext context);
	}

}
