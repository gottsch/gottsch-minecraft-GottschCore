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
import net.minecraft.util.ResourceLocation;

/**
 * @author Mark Gottschling on Nov 7, 2018
 *
 */
@Deprecated
public abstract class LootFunction {
    private final LootCondition[] conditions;

    protected LootFunction(LootCondition[] conditionsIn)
    {
        this.conditions = conditionsIn;
    }

    public abstract ItemStack apply(ItemStack stack, Random rand, LootContext context);

    public LootCondition[] getConditions()
    {
        return this.conditions;
    }

    public abstract static class Serializer<T extends LootFunction>
        {
            private final ResourceLocation lootTableLocation;
            private final Class<T> functionClass;

            protected Serializer(ResourceLocation location, Class<T> clazz)
            {
                this.lootTableLocation = location;
                this.functionClass = clazz;
            }

            public ResourceLocation getFunctionName()
            {
                return this.lootTableLocation;
            }

            public Class<T> getFunctionClass()
            {
                return this.functionClass;
            }

            public abstract void serialize(JsonObject object, T functionClazz, JsonSerializationContext serializationContext);

            public abstract T deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn);
        }
}
