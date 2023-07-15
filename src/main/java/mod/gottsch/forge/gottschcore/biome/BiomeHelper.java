/*
 * This file is part of  GottschCore.
 * Copyright (c) 2021 Mark Gottschling (gottsch)
 * 
 * All rights reserved.
 *
 * GottschCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GottschCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GottschCore.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.gottschcore.biome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.BiomeSources;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author Mark Gottschling on May 8, 2017
 *
 */
public class BiomeHelper {

	public enum Result {
		OK,
		WHITE_LISTED,
		BLACK_LISTED
	};

	/**
	 * 
	 * @param biome
	 * @param whiteList
	 * @param blackList
	 * @return
	 */
	public static Result isBiomeAllowed(Biome biome, List<? extends String> whiteList, List<? extends String> blackList) {
		return isBiomeAllowed(ForgeRegistries.BIOMES.getKey(biome), whiteList, blackList);
	}
	
	/**
	 * 
	 * @param biome
	 * @param whiteList
	 * @param blackList
	 * @return
	 */
	public static Result isBiomeAllowed(ResourceLocation biome, List<? extends String> whiteList, List<? extends String> blackList) {
        if (whiteList != null && whiteList.size() > 0) {
        	for (String biomeName : whiteList) {
	        	if (biomeName.equals(biome.toString())) {
	        		return Result.WHITE_LISTED;
	        	}
	        }
        	// added in 1.15. If white list has values and biome is not in it, then by definition, it is black listed.
        	return Result.BLACK_LISTED;
        }
        
        if (blackList != null && blackList.size() > 0) {
        	for (String biomeName : blackList) {
        		if (biomeName.equals(biome.toString())) {
        			return Result.BLACK_LISTED;
        		}
        	}
        }
        
    	// neither white list nor black list have values = all biomes are valid
    	return Result.OK;
	}
	
	/**
	 * 
	 * @param biome
	 * @param category
	 * @param biomeWhiteList
	 * @param biomeBlackList
	 * @param categoryWhiteList
	 * @param categoryBlackList
	 * @return
	 */
//	public static Result isBiomeAllowed(ResourceLocation biome, BiomeCategory category, 
//			List<? extends String> biomeWhiteList, List<? extends String> biomeBlackList,
//			List<? extends String> categoryWhiteList, List<? extends String> categoryBlackList) {
//		
//		Result result = isBiomeAllowed(biome, biomeWhiteList, biomeBlackList);
//		if (result == Result.OK) {
//	        if (categoryWhiteList != null && !categoryWhiteList.isEmpty()) {
//	        	if (categoryWhiteList.contains(category.getName())) {
//	        		return Result.WHITE_LISTED;
//	        	}
//	        	return Result.BLACK_LISTED;
//	        }
//	        if (categoryBlackList != null && !categoryBlackList.isEmpty()) {
//	        	if (categoryBlackList.contains(category.getName())) {
//	        		return Result.BLACK_LISTED;
//	        	}
//	        }
//		}
//		else {
//			return result;
//		}
//		
//    	// neither white list nor black list have values = all biomes are valid
//    	return Result.OK;
//	}
}
