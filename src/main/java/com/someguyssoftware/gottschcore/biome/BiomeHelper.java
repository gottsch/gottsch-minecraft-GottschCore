/**
 * 
 */
package com.someguyssoftware.gottschcore.biome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author Mark Gottschling on May 8, 2017
 *
 */
public class BiomeHelper {

	public static List<String> biomeNames;
	
	static {
		biomeNames = Arrays.asList(new String[] {
				"minecraft:ocean",
				"minecraft:plains",
				"minecraft:desert",
				"minecraft:mountains",
				"minecraft:forest",
				"minecraft:taiga",
				"minecraft:swamp",
				"minecraft:river",
				"minecraft:nether_wastes",
				"minecraft:the_end",
				"minecraft:frozen_ocean",
				"minecraft:frozen_river",
				"minecraft:snowy_tundra",
				"minecraft:snowy_mountains",
				"minecraft:mushroom_fields",
				"minecraft:mushroom_field_shore",
				"minecraft:beach",
				"minecraft:desert_hills",
				"minecraft:wooded_hills",
				"minecraft:taiga_hills",
				"minecraft:mountain_edge",
				"minecraft:jungle",
				"minecraft:jungle_hills",
				"minecraft:jungle_edge",				
				"minecraft:deep_ocean",
				"minecraft:stone_shore",
				"minecraft:snowy_beach",
				"minecraft:birch_forest",
				"minecraft:birch_forest_hills",
				"minecraft:dark_forest",
				"minecraft:snowy_taiga",
				"minecraft:snowy_taiga_hills",
				"minecraft:giant_tree_taiga",
				"minecraft:giant_tree_taiga_hills",
				"minecraft:wooded_mountains",
				"minecraft:savanna",
				"minecraft:savanna_plateau",
				"minecraft:badlands",
				"minecraft:badlands_plateau",
				"minecraft:wooded_badlands_plateau",
				"minecraft:small_end_islands",
				"minecraft:end_midlands",
				"minecraft:end_highlands",
				"minecraft:end_barrens",
				"minecraft:warm_ocean",
				"minecraft:lukewarm_ocean",
				"minecraft:cold_ocean",
				"minecraft:deep_warm_ocean",
				"minecraft:deep_lukewarm_ocean",
				"minecraft:deep_cold_ocean",
				"minecraft:deep_frozen_ocean",
				"minecraft:the_void",
				"minecraft:sunflower_plains",
				"minecraft:desert_lakes",
				"minecraft:gravelly_mountains",				
				"minecraft:flower_forest",
				"minecraft:taiga_mountains",
				"minecraft:swamp_hills",
				"minecraft:ice_spikes",
				"minecraft:modified_jungle",
				"minecraft:modified_jungle_edge",				
				"minecraft:tall_birch_forest",
				"minecraft:tall_birch_hills",
				"minecraft:dark_forest_hills",
				"minecraft:snowy_taiga_mountains",
				"minecraft:giant_spruce_taiga",
				"minecraft:giant_spruce_taiga_hills",
				"minecraft:modified_gravelly_mountains",
				"minecraft:shattered_savanna",
				"minecraft:shattered_savanna_plateau",
				"minecraft:eroded_badlands",
				"minecraft:modified_wooded_badlands_plateau",
				"minecraft:modified_badlands_plateau",				
				"minecraft:bamboo_jungle",
				"minecraft:bamboo_jungle_hills",
				"minecraft:soul_sand_valley",
				 "minecraft:crimson_forest",
				"minecraft:warped_forest",
				"minecraft:basalt_deltas",
				"minecraft:dripstone_caves",
				"minecraft:lush_caves",
				"minecraft:meadow",
				"minecraft:grove",
				"minecraft:snowy_slopes",
				"minecraft:snowcapped_peaks",
				"minecraft:lofty_peaks",
				"minecraft:stony_peaks"
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

	public static Result isBiomeAllowed(Biome biome, List<? extends String> whiteList, List<? extends String> blackList) {
		return isBiomeAllowed(biome.getRegistryName(), whiteList, blackList);
	}
	
	public static Result isBiomeAllowed(ResourceLocation biome, List<? extends String> whiteList, List<? extends String> blackList) {
        if (whiteList != null && whiteList.size() > 0) {
        	for (String biomeName : whiteList) {
	        	if (biomeName.equals(biome)) {
	        		return Result.WHITE_LISTED;
	        	}
	        }
        	// added in 1.15. If white list has values and biome is not in it, then by definition, it is black listed.
        	return Result.BLACK_LISTED;
        }
        
        if (blackList != null && blackList.size() > 0) {
        	for (String biomeName : blackList) {
        		if (biomeName.equals(biome)) {
        			return Result.BLACK_LISTED;
        		}
        	}
        }
        
    	// neither white list nor black list have values = all biomes are valid
    	return Result.OK;
	}
	
	public static Result isBiomeAllowed(ResourceLocation biome, BiomeCategory category, 
			List<? extends String> biomeWhiteList, List<? extends String> biomeBlackList,
			List<? extends String> categoryWhiteList, List<? extends String> categoryBlackList) {
		
		Result result = isBiomeAllowed(biome, biomeWhiteList, biomeBlackList);
		if (result == Result.OK) {
	        if (categoryWhiteList != null && !categoryWhiteList.isEmpty()) {
	        	if (categoryWhiteList.contains(category.getName())) {
	        		return Result.WHITE_LISTED;
	        	}
	        	return Result.BLACK_LISTED;
	        }
	        if (categoryBlackList != null && !categoryBlackList.isEmpty()) {
	        	if (categoryBlackList.contains(category.getName())) {
	        		return Result.BLACK_LISTED;
	        	}
	        }
		}
		else {
			return result;
		}
		
    	// neither white list nor black list have values = all biomes are valid
    	return Result.OK;
	}
}
