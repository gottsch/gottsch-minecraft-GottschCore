package com.someguyssoftware.gottschcore.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.someguyssoftware.gottschcore.loot.IParameterized;
import com.someguyssoftware.gottschcore.loot.LootContext;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface ILootFunction extends IParameterized, BiFunction<ItemStack, LootContext, ItemStack> {
   static Consumer<ItemStack> func_215858_a(BiFunction<ItemStack, LootContext, ItemStack> p_215858_0_, Consumer<ItemStack> p_215858_1_, LootContext p_215858_2_) {
      return (p_215857_3_) -> {
         p_215858_1_.accept(p_215858_0_.apply(p_215857_3_, p_215858_2_));
      };
   }

   public interface IBuilder {
      ILootFunction build();
   }

   public abstract static class Serializer<T extends ILootFunction> {
      private final ResourceLocation lootTableLocation;
      private final Class<T> functionClass;

      protected Serializer(ResourceLocation location, Class<T> clazz) {
         this.lootTableLocation = location;
         this.functionClass = clazz;
      }

      public ResourceLocation getFunctionName() {
         return this.lootTableLocation;
      }

      public Class<T> getFunctionClass() {
         return this.functionClass;
      }

      public abstract void serialize(JsonObject object, T functionClazz, JsonSerializationContext serializationContext);

      public abstract T deserialize(JsonObject p_212870_1_, JsonDeserializationContext p_212870_2_);
   }
}