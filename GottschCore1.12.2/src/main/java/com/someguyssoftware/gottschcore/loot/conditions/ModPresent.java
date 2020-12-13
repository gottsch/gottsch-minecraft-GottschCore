package com.someguyssoftware.gottschcore.loot.conditions;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.someguyssoftware.gottschcore.loot.LootContext;

import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

/**
 * A condition that checks for the existance of a mod.
 * 
 * @author Mark Gottschling on Nov 7, 2018
 *
 */
@Deprecated
public class ModPresent implements LootCondition {
	private final String modID;

	public ModPresent(String modID) {
		this.modID = modID;
	}

	/**
	 * 
	 */
	public boolean testCondition(Random rand, LootContext context) {
		if (Loader.isModLoaded(this.modID))
			return true;
		return false;
	}

	/**
	 * 
	 * @author Mark Gottschling on Nov 7, 2018
	 *
	 */
	public static class Serializer extends LootCondition.Serializer<ModPresent> {
		public Serializer() {
			super(new ResourceLocation("gottschcore:mod_present"), ModPresent.class);
		}

		public void serialize(JsonObject json, ModPresent value, JsonSerializationContext context) {
			json.addProperty("modid", String.valueOf(value.modID));
		}

		public ModPresent deserialize(JsonObject json, JsonDeserializationContext context) {
			return new ModPresent(JsonUtils.getString(json, "modid"));
		}
	}

}