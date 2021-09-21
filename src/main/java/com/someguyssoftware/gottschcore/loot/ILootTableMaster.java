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
package com.someguyssoftware.gottschcore.loot;

import java.util.List;

import com.someguyssoftware.gottschcore.mod.IMod;

import net.minecraft.server.level.ServerLevel;

public interface ILootTableMaster {

	/**
	 * Call in WorldEvent.Load event handler.
	 * @param world
	 */
	void init(ServerLevel world);

	void clear();

	/**
	 * CREATES CONFIG FOLDERS/RESOURCES - on mod/manager creation
	 * Creates all the necessary folder and resources before actual loading of loot tables.
	 * Call in your @Mod class in preInt() or int().
	 * 
	 * @param resourceRootPath
	 * @param modID
	 */
	void buildAndExpose(String resourceRootPath, String modID, List<String> locations);

	/**
	 * ONE STOP SHOP to tell where the resource is and to add to the maps and to register with minecraft
	 * 
	 * Call in WorldEvent.Load event handler.
	 * Overide this method if you have a different cache mechanism.
	 * @param location
	 */
	void register(ServerLevel world, String modID, List<String> locations);

	IMod getMod();

}