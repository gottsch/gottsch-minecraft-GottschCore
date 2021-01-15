package com.someguyssoftware.gottschcore.loot.functions;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.someguyssoftware.gottschcore.loot.LootContext;
import com.someguyssoftware.gottschcore.loot.LootFunction;
import com.someguyssoftware.gottschcore.loot.conditions.ILootCondition;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;

public class Smelt extends LootFunction {
   private static final Logger LOGGER = LogManager.getLogger();

   private Smelt(ILootCondition[] conditionsIn) {
      super(conditionsIn);
   }

   public ItemStack doApply(ItemStack stack, LootContext context) {
      if (stack.isEmpty()) {
         return stack;
      } else {
         Optional<FurnaceRecipe> optional = context.getWorld().getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(stack), context.getWorld());
         if (optional.isPresent()) {
            ItemStack itemstack = optional.get().getRecipeOutput();
            if (!itemstack.isEmpty()) {
               ItemStack itemstack1 = itemstack.copy();
               itemstack1.setCount(stack.getCount() * itemstack.getCount()); //Forge: Support smelting returning multiple
               return itemstack1;
            }
         }

         LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", (Object)stack);
         return stack;
      }
   }

   public static LootFunction.Builder<?> func_215953_b() {
      return builder(Smelt::new);
   }

   public static class Serializer extends LootFunction.Serializer<Smelt> {
      protected Serializer() {
         super(new ResourceLocation("furnace_smelt"), Smelt.class);
      }

      public Smelt deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
         return new Smelt(conditionsIn);
      }
   }
}