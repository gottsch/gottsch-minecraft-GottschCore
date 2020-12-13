package com.someguyssoftware.gottschcore.loot.conditions;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.someguyssoftware.gottschcore.loot.LootContext;

import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

/**
 * 
 * @author Mark Gottschling on Nov 7, 2018
 *
 */
@Deprecated
public class RandomChanceWithLooting implements LootCondition
{
    private final float chance;
    private final float lootingMultiplier;

    public RandomChanceWithLooting(float chanceIn, float lootingMultiplierIn)
    {
        this.chance = chanceIn;
        this.lootingMultiplier = lootingMultiplierIn;
    }

    public boolean testCondition(Random rand, LootContext context)
    {
        int i = context.getLootingModifier();

        return rand.nextFloat() < this.chance + (float)i * this.lootingMultiplier;
    }

    public static class Serializer extends LootCondition.Serializer<RandomChanceWithLooting>
        {
            protected Serializer()
            {
                super(new ResourceLocation("random_chance_with_looting"), RandomChanceWithLooting.class);
            }

            public void serialize(JsonObject json, RandomChanceWithLooting value, JsonSerializationContext context)
            {
                json.addProperty("chance", Float.valueOf(value.chance));
                json.addProperty("looting_multiplier", Float.valueOf(value.lootingMultiplier));
            }

            public RandomChanceWithLooting deserialize(JsonObject json, JsonDeserializationContext context)
            {
                return new RandomChanceWithLooting(JsonUtils.getFloat(json, "chance"), JsonUtils.getFloat(json, "looting_multiplier"));
            }
        }
}