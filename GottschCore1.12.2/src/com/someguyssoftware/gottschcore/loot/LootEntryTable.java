/**
 * 
 */
package com.someguyssoftware.gottschcore.loot;

import java.util.Collection;
import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.someguyssoftware.gottschcore.loot.conditions.LootCondition;

import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

/**
 * @author Mark Gottschling on Nov 6, 2018
 *
 */
public class LootEntryTable extends LootEntry {

    protected final ResourceLocation table;

    /**
     * 
     * @param tableIn
     * @param weightIn
     * @param qualityIn
     * @param conditionsIn
     * @param entryName
     */
    public LootEntryTable(ResourceLocation tableIn, int weightIn, int qualityIn, LootCondition[] conditionsIn, String entryName) {
        super(weightIn, qualityIn, conditionsIn, entryName);
        this.table = tableIn;
    }
    
    /**
     * TODO have to replace LootContext
     */
    @Override
    public void addLoot(Collection<ItemStack> stacks, Random rand, LootContext context) {
    	LootTable loottable = context.getLootTableManager().getLootTableFromLocation(this.table);
        Collection<ItemStack> collection = loottable.generateLootFromPools(rand, context);
        stacks.addAll(collection);
    }

    /**
     * 
     */
    @Override
    protected void serialize(JsonObject json, JsonSerializationContext context) {
        json.addProperty("name", this.table.toString());
    }

    /**
     * 
     * @param object
     * @param deserializationContext
     * @param weightIn
     * @param qualityIn
     * @param conditionsIn
     * @return
     */
    public static LootEntryTable deserialize(JsonObject object, JsonDeserializationContext deserializationContext, int weightIn, int qualityIn, LootCondition[] conditionsIn) {
    	String name = LootTableManager.readLootEntryName(object, "loot_table");
        ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(object, "name"));
        return new LootEntryTable(resourcelocation, weightIn, qualityIn, conditionsIn, name);
    }
}
