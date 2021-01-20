package com.someguyssoftware.gottschcore.loot.conditions;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.someguyssoftware.gottschcore.loot.LootContext;
import com.someguyssoftware.gottschcore.loot.LootParameter;
import com.someguyssoftware.gottschcore.loot.LootParameters;

import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;


public class RandomChanceWithLooting implements ILootCondition {
   private final float chance;
   private final float lootingMultiplier;

   private RandomChanceWithLooting(float chanceIn, float lootingMultiplierIn) {
      this.chance = chanceIn;
      this.lootingMultiplier = lootingMultiplierIn;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.KILLER_ENTITY);
   }

   public boolean test(LootContext p_test_1_) {
      int i = p_test_1_.getLootingModifier();
      return p_test_1_.getRandom().nextFloat() < this.chance + (float)i * this.lootingMultiplier;
   }

   public static ILootCondition.IBuilder builder(float chanceIn, float lootingMultiplierIn) {
      return () -> {
         return new RandomChanceWithLooting(chanceIn, lootingMultiplierIn);
      };
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<RandomChanceWithLooting> {
      protected Serializer() {
         super(new ResourceLocation("random_chance_with_looting"), RandomChanceWithLooting.class);
      }

      public void serialize(JsonObject json, RandomChanceWithLooting value, JsonSerializationContext context) {
         json.addProperty("chance", value.chance);
         json.addProperty("looting_multiplier", value.lootingMultiplier);
      }

      public RandomChanceWithLooting deserialize(JsonObject json, JsonDeserializationContext context) {
         return new RandomChanceWithLooting(JSONUtils.getFloat(json, "chance"), JSONUtils.getFloat(json, "looting_multiplier"));
      }
   }
}