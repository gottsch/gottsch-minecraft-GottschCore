package com.someguyssoftware.gottschcore.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.someguyssoftware.gottschcore.loot.IRandomRange;
import com.someguyssoftware.gottschcore.loot.LootContext;
import com.someguyssoftware.gottschcore.loot.LootFunction;
import com.someguyssoftware.gottschcore.loot.RandomRanges;
import com.someguyssoftware.gottschcore.loot.conditions.ILootCondition;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SetCount extends LootFunction {
   private final IRandomRange countRange;

   private SetCount(ILootCondition[] p_i51222_1_, IRandomRange p_i51222_2_) {
      super(p_i51222_1_);
      this.countRange = p_i51222_2_;
   }

   public ItemStack doApply(ItemStack stack, LootContext context) {
      stack.setCount(this.countRange.generateInt(context.getRandom()));
      return stack;
   }

   public static LootFunction.Builder<?> func_215932_a(IRandomRange p_215932_0_) {
      return builder((p_215934_1_) -> {
         return new SetCount(p_215934_1_, p_215932_0_);
      });
   }

   public static class Serializer extends LootFunction.Serializer<SetCount> {
      protected Serializer() {
         super(new ResourceLocation("set_count"), SetCount.class);
      }

      public void serialize(JsonObject object, SetCount functionClazz, JsonSerializationContext serializationContext) {
         super.serialize(object, functionClazz, serializationContext);
         object.add("count", RandomRanges.serialize(functionClazz.countRange, serializationContext));
      }

      public SetCount deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
         IRandomRange irandomrange = RandomRanges.deserialize(object.get("count"), deserializationContext);
         return new SetCount(conditionsIn, irandomrange);
      }
   }
}