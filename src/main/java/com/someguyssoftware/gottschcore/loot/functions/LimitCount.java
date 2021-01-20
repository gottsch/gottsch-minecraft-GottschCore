package com.someguyssoftware.gottschcore.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.someguyssoftware.gottschcore.loot.IntClamper;
import com.someguyssoftware.gottschcore.loot.LootContext;
import com.someguyssoftware.gottschcore.loot.LootFunction;
import com.someguyssoftware.gottschcore.loot.conditions.ILootCondition;

import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class LimitCount extends LootFunction {
   private final IntClamper field_215914_a;

   private LimitCount(ILootCondition[] p_i51232_1_, IntClamper p_i51232_2_) {
      super(p_i51232_1_);
      this.field_215914_a = p_i51232_2_;
   }

   public ItemStack doApply(ItemStack stack, LootContext context) {
      int i = this.field_215914_a.applyAsInt(stack.getCount());
      stack.setCount(i);
      return stack;
   }

   public static LootFunction.Builder<?> func_215911_a(IntClamper p_215911_0_) {
      return builder((p_215912_1_) -> {
         return new LimitCount(p_215912_1_, p_215911_0_);
      });
   }

   public static class Serializer extends LootFunction.Serializer<LimitCount> {
      protected Serializer() {
         super(new ResourceLocation("limit_count"), LimitCount.class);
      }

      public void serialize(JsonObject object, LimitCount functionClazz, JsonSerializationContext serializationContext) {
         super.serialize(object, functionClazz, serializationContext);
         object.add("limit", serializationContext.serialize(functionClazz.field_215914_a));
      }

      public LimitCount deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
         IntClamper intclamper = JSONUtils.deserializeClass(object, "limit", deserializationContext, IntClamper.class);
         return new LimitCount(conditionsIn, intclamper);
      }
   }
}