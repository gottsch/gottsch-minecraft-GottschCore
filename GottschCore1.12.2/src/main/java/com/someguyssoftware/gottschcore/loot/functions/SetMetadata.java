package com.someguyssoftware.gottschcore.loot.functions;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.loot.LootContext;
import com.someguyssoftware.gottschcore.loot.conditions.LootCondition;

import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.RandomValueRange;

/**
 * 
 * @author Mark Gottschling on Nov 7, 2018
 *
 */
@Deprecated
public class SetMetadata extends LootFunction {
	private final RandomValueRange metaRange;

	public SetMetadata(LootCondition[] conditionsIn, RandomValueRange metaRangeIn) {
		super(conditionsIn);
		this.metaRange = metaRangeIn;
	}

	public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
		if (stack.isItemStackDamageable()) {
			GottschCore.logger.warn("Couldn't set data of loot item {}", (Object) stack);
		} else {
			stack.setItemDamage(this.metaRange.generateInt(rand));
		}

		return stack;
	}

	public static class Serializer extends LootFunction.Serializer<SetMetadata> {
		protected Serializer() {
			super(new ResourceLocation("set_data"), SetMetadata.class);
		}

		public void serialize(JsonObject object, SetMetadata functionClazz, JsonSerializationContext serializationContext) {
			object.add("data", serializationContext.serialize(functionClazz.metaRange));
		}

		public SetMetadata deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
			return new SetMetadata(conditionsIn, (RandomValueRange) JsonUtils.deserializeClass(object, "data", deserializationContext, RandomValueRange.class));
		}
	}
}