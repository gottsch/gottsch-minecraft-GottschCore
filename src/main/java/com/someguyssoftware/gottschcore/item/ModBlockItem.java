/*
 * This file is part of  GottschCore.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
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
package com.someguyssoftware.gottschcore.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * @author Mark Gottschling on Jul 22, 2017
 *
 */
public class ModBlockItem extends BlockItem {

	/**
	 * 
	 */
	public ModBlockItem(String modID, String name, Block block, Item.Properties properties) {
		super(block, properties);
		setItemName(modID, name);
	}
	
	/**
	 * Set the registry name of {@code this Item} to {@code name} and the unlocalised name to the full registry name.
	 * @param modID
	 * @param name
	 */
	public Item setItemName(String modID, String name) {
		setRegistryName(modID, name);
		return this;
	}
}
