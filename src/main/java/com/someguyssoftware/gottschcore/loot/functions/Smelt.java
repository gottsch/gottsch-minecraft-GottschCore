package com.someguyssoftware.gottschcore.loot.functions;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.loot.LootContext;
import com.someguyssoftware.gottschcore.loot.conditions.LootCondition;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;

/**
 * 
 * @author Mark Gottschling on Nov 7, 2018
 *
 */
@Deprecated
public class Smelt extends LootFunction {

	public Smelt(LootCondition[] conditionsIn) {
		super(conditionsIn);
	}

	public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
		if (stack.isEmpty()) {
			return stack;
		} else {
			ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(stack);

			if (itemstack.isEmpty()) {
				GottschCore.logger.warn("Couldn't smelt {} because there is no smelting recipe", (Object) stack);
				return stack;
			} else {
				ItemStack itemstack1 = itemstack.copy();
				itemstack1.setCount(stack.getCount());
				return itemstack1;
			}
		}
	}

	public static class Serializer extends LootFunction.Serializer<Smelt> {
		protected Serializer() {
			super(new ResourceLocation("furnace_smelt"), Smelt.class);
		}

		public void serialize(JsonObject object, Smelt functionClazz, JsonSerializationContext serializationContext) {
		}

		public Smelt deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
			return new Smelt(conditionsIn);
		}
	}
}