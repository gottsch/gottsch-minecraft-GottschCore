/**
 * 
 */
package com.someguyssoftware.gottschcore.biome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author Mark Gottschling on May 8, 2017
 *
 */
public class BiomeHelper {

	public static List<String> biomeNames;
	
	static {
		biomeNames = Arrays.asList(new String[] {
				"ocean","deep_ocean","frozen_ocean","deep_frozen_ocean","cold_ocean","deep_cold_ocean",
				"lukewarm_ocean","deep_lukewarm_ocean","warm_ocean","deep_warm_ocean",
				"river","frozen_river","beach","stone_shore","snowy_beach","forest","wooded_hills","flower_forest",
				"birch_forest","birch_forest_hills","tall_birch_forest","tall_birch_hills","dark_forest","dark_forest_hills",
				"jungle","jungle_hills","modified_jungle","jungle_edge","modified_jungle_edge","bamboo_jungle",
				"bamboo_jungle_hills","taiga","taiga_hills","taiga_mountains","snowy_taiga","snowy_taiga_hills",
				"snowy_taiga_mountains","giant_tree_taiga","giant_tree_taiga_hills","giant_spruce_taiga",
				"giant_spruce_taiga_hills","mushroom_fields","mushroom_field_shore","swamp","swamp_hills",
				"savanna","savanna_plateau","shattered_savanna","shattered_savanna_plateau","plains",
				"sunflower_plains","desert","desert_hills","desert_lakes","snowy_tundra","snowy_mountains",
				"ice_spikes","mountains","wooded_mountains","gravelly_mountains","modified_gravelly_mountains",
				"mountain_edge","badlands","badlands_plateau","modified_badlands_plateau","wooded_badlands_plateau",
				"modified_wooded_badlands_plateau","eroded_badlands","nether","the_end","small_end_islands",
				"end_midlands","end_highlands","end_barrens","the_void"
		});
	}
	
	public enum Result {
		OK,
		WHITE_LISTED,
		BLACK_LISTED
	};
	
	/**
	 * 
	 * @param biomes
	 * @return
	 */
	public static List<Biome> loadBiomesList(String[] biomes) {
		List<Biome> list = new ArrayList<>();
		if (biomes != null) {
			for (String biomeName : biomes) {
				Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(biomeName));
				if (!list.contains(biome)) {
					list.add(biome);
				}
			}
		}
		return list;
	}

	public static Result isBiomeAllowed(Biome biome, List<String> whiteList, List<String> blackList) {
        if (whiteList != null && whiteList.size() > 0) {
        	for (String biomeName : whiteList) {
	        	if (biomeName.equals(biome.getRegistryName().toString())) {
	        		return Result.WHITE_LISTED;
	        	}
	        }
        	// added in 1.15. If white list has values and biome is not in it, then by definition, it is black listed.
        	return Result.BLACK_LISTED;
        }
        
        if (blackList != null && blackList.size() > 0) {
        	for (String biomeName : blackList) {
        		if (biomeName.equals(biome.getRegistryName().toString())) {
        			return Result.BLACK_LISTED;
        		}
        	}
        }
        
    	// neither white list nor black list have values = all biomes are valid
    	return Result.OK;
	}
}
