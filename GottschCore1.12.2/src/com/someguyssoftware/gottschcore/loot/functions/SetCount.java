/**
 * 
 */
package com.someguyssoftware.gottschcore.loot.functions;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.someguyssoftware.gottschcore.loot.LootContext;
import com.someguyssoftware.gottschcore.loot.conditions.LootCondition;

import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.RandomValueRange;

/**
 * @author Mark Gottschling on Nov 7, 2018
 *
 */
public class SetCount extends LootFunction
	{

	private final RandomValueRange countRange;

	public SetCount(LootCondition[] conditionsIn, RandomValueRange countRangeIn) {
		super(conditionsIn);
		this.countRange = countRangeIn;
	}

	public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
		stack.setCount(this.countRange.generateInt(rand));
		return stack;
	}

	public static class Serializer extends LootFunction.Serializer<SetCount> {
		protected Serializer() {
			super(new ResourceLocation("set_count"), SetCount.class);
		}

		public void serialize(JsonObject object, SetCount functionClazz, JsonSerializationContext serializationContext) {
			object.add("count", serializationContext.serialize(functionClazz.countRange));
		}

		public SetCount deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
			return new SetCount(conditionsIn, (RandomValueRange) JsonUtils.deserializeClass(object, "count", deserializationContext, RandomValueRange.class));
		}
	}
}