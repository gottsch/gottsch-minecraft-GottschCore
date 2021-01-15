package com.someguyssoftware.gottschcore.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.someguyssoftware.gottschcore.loot.ConstantRange.Serializer;
import com.someguyssoftware.gottschcore.loot.conditions.ILootCondition;
import com.someguyssoftware.gottschcore.loot.conditions.LootConditionManager;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
@Deprecated
public class LootPredicateManager extends JsonReloadListener {
	   private static final Logger LOGGER = LogManager.getLogger();
	   private static final Gson GSON_INSTANCE = (new GsonBuilder()).registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer()).registerTypeAdapter(BinomialRange.class, new BinomialRange.Serializer()).registerTypeAdapter(ConstantRange.class, new ConstantRange.Serializer()).registerTypeHierarchyAdapter(ILootCondition.class, new LootConditionManager.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();
	   private Map<ResourceLocation, ILootCondition> resourceLocationToLootConditionMap = ImmutableMap.of();

	   public LootPredicateManager() {
	      super(GSON_INSTANCE, "predicates");
	   }

	   @Nullable
	   public ILootCondition getLootConditionFromLocation(ResourceLocation resourceLocation) {
	      return this.resourceLocationToLootConditionMap.get(resourceLocation);
	   }

	   protected void apply(Map<ResourceLocation, JsonObject> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
	      Builder<ResourceLocation, ILootCondition> builder = ImmutableMap.builder();
	      objectIn.forEach((p_227514_1_, p_227514_2_) -> {
	         try {
	            ILootCondition ilootcondition = GSON_INSTANCE.fromJson(p_227514_2_, ILootCondition.class);
	            builder.put(p_227514_1_, ilootcondition);
	         } catch (Exception exception) {
	            LOGGER.error("Couldn't parse loot table {}", p_227514_1_, exception);
	         }

	      });
	      Map<ResourceLocation, ILootCondition> map = builder.build();
	      ValidationTracker validationtracker = new ValidationTracker(LootParameterSets.GENERIC, map::get, (p_227518_0_) -> {
	         return null;
	      });
	      map.forEach((p_227515_1_, p_227515_2_) -> {
	         p_227515_2_.func_225580_a_(validationtracker.func_227535_b_("{" + p_227515_1_ + "}", p_227515_1_));
	      });
	      validationtracker.getProblems().forEach((p_227516_0_, p_227516_1_) -> {
	         LOGGER.warn("Found validation problem in " + p_227516_0_ + ": " + p_227516_1_);
	      });
	      this.resourceLocationToLootConditionMap = map;
	   }

	   public Set<ResourceLocation> func_227513_a_() {
	      return Collections.unmodifiableSet(this.resourceLocationToLootConditionMap.keySet());
	   }
	}