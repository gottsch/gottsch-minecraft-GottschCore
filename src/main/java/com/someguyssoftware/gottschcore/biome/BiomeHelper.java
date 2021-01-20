/**
 * 
 */
package com.someguyssoftware.gottschcore.biome;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

/**
 * @author Mark Gottschling on May 8, 2017
 *
 */
public class BiomeHelper {

	/**
	 * 
	 * @param biomes
	 * @return
	 */
	public static List<Biome> loadBiomesList(String[] biomes) {
		List<Biome> list = new ArrayList<>();
		for (String biomeName : biomes) {
			Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(biomeName));
			if (!list.contains(biome)) {
				list.add(biome);
			}
		}
		return list;
	}

	/**
	 * 
	 * @param biomeTypeNames
	 * @param typeHolders
	 */
	public static void loadBiomeList(List<? extends String> biomeTypeNames, List<BiomeTypeHolder> typeHolders) {
		BiomeTypeHolder holder = null;
		Object t = null;

		for (String s : biomeTypeNames) {
			holder = null;
			// check against Forge BiomeTypeMap
			if ((t = BiomeTypeMap.getByName(s.trim().toUpperCase())) != null) {
				holder = new BiomeTypeHolder(0, t);
			}
			// check against all registered BiomeDictionaries
			else {
				for (IBiomeDictionary d : BiomeDictionaryManager.getInstance().getAll()) {
					t = d.getTypeByName(s.toUpperCase());
					holder = new BiomeTypeHolder(1, t);
					break;
				}
			}
			if (holder != null) {
				typeHolders.add(holder);
			}
		}
	}

	/**
	 * Load the Biome Type Holders with all the biome types.
	 * 
	 * @param biome
	 * @param whiteList
	 * @param blackList
	 * @return
	 */
	public static boolean isBiomeAllowed(Biome biome, List<BiomeTypeHolder> whiteList,
			List<BiomeTypeHolder> blackList) {
		/*
		 * check the white list first. if white list is not null && not empty but biome
		 * is NOT in the list, then return false
		 */
		if (whiteList != null && whiteList.size() > 0) {
			for (BiomeTypeHolder holder : whiteList) {
				// check which dictionary to use
				if (holder.getDictionaryId() == 0) {
					// TODO not sure if this will work
					if (BiomeDictionary.hasType(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, biome.getRegistryName()), (Type) holder.getBiomeType())) {
						return true;
					}
				} else {
					// check against all registered BiomeDictionaries
					for (IBiomeDictionary d : BiomeDictionaryManager.getInstance().getAll()) {
						if (d.isBiomeOfType(biome, (IBiomeType) holder.getBiomeType())) {
							return true;
						}
					}
				}
			}
			return false;
		} else if (blackList != null && blackList.size() > 0) {
			// check the black list
			for (BiomeTypeHolder holder : blackList) {
				// check which dictionary to use
				if (holder.getDictionaryId() == 0) {
					if (BiomeDictionary.hasType(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, biome.getRegistryName()), (Type) holder.getBiomeType())) {
						return false;
					}
				} else {
					// check against all registered BiomeDictionaries
					for (IBiomeDictionary d : BiomeDictionaryManager.getInstance().getAll()) {
						if (d.isBiomeOfType(biome, (IBiomeType) holder.getBiomeType())) {
							return false;
						}
					}
				}
			}
			return true;
		} else {
			// neither white list nor black list have values = all biomes are valid
			return true;
		}
	}
}
