package com.someguyssoftware.gottschcore.loot.functions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.someguyssoftware.gottschcore.loot.LootContext;
import com.someguyssoftware.gottschcore.loot.LootFunction;
import com.someguyssoftware.gottschcore.loot.RandomValueRange;
import com.someguyssoftware.gottschcore.loot.conditions.ILootCondition;

import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class SetDamage extends LootFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RandomValueRange damageRange;

   private SetDamage(ILootCondition[] conditionsIn, RandomValueRange damageRangeIn) {
      super(conditionsIn);
      this.damageRange = damageRangeIn;
   }

   public ItemStack doApply(ItemStack stack, LootContext context) {
      if (stack.isDamageable()) {
         float f = 1.0F - this.damageRange.generateFloat(context.getRandom());
         stack.setDamage(MathHelper.floor(f * (float)stack.getMaxDamage()));
      } else {
         LOGGER.warn("Couldn't set damage of loot item {}", (Object)stack);
      }

      return stack;
   }

   public static LootFunction.Builder<?> func_215931_a(RandomValueRange p_215931_0_) {
      return builder((p_215930_1_) -> {
         return new SetDamage(p_215930_1_, p_215931_0_);
      });
   }

   public static class Serializer extends LootFunction.Serializer<SetDamage> {
      protected Serializer() {
         super(new ResourceLocation("set_damage"), SetDamage.class);
      }

      public void serialize(JsonObject object, SetDamage functionClazz, JsonSerializationContext serializationContext) {
         super.serialize(object, functionClazz, serializationContext);
         object.add("damage", serializationContext.serialize(functionClazz.damageRange));
      }

      public SetDamage deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
         return new SetDamage(conditionsIn, JSONUtils.deserializeClass(object, "damage", deserializationContext, RandomValueRange.class));
      }
   }
}