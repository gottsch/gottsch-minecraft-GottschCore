/*
 * This file is part of  GottschCore.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.gottschcore.util;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 
 * @author Mark Gottschling on Jul 7, 2023
 *
 */
public class GottschCoreUtil {
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static ResourceLocation asLocation(String defaultDomain, String name) {
		return hasDomain(name) ? new ResourceLocation(name) : new ResourceLocation(defaultDomain, name);
	}
	
	public static boolean hasDomain(String name) {
		return name.indexOf(":") >= 0;
	}
	
	public static ResourceLocation getName(Block block) {
		// don't bother checking optional - if it is empty, then the block isn't registered and this shouldn't run anyway.
		ResourceLocation name = ForgeRegistries.BLOCKS.getResourceKey(block).get().location();
		return name;
	}

	public static ResourceLocation getName(Item item) {
		// don't bother checking optional - if it is empty, then the block isn't registered and this shouldn't run anyway.
		ResourceLocation name = ForgeRegistries.ITEMS.getResourceKey(item).get().location();
		return name;
	}
	
	public static ResourceLocation getName(Holder<Biome> biome) {
		return biome.unwrapKey().get().location();	
	}
}
