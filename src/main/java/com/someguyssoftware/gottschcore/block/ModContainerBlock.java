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
package com.someguyssoftware.gottschcore.block;

import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;

/**
 * This class replaces BlockContainer so that it can extend ModBlock
 * 
 * @author Mark Gottschling onJan 2, 2018
 *
 */
public abstract class ModContainerBlock extends BaseEntityBlock /*ModBlock /*implements ITileEntityProvider*/ {

	/**
	 * 
	 * @param modID
	 * @param name
	 * @param properties
	 */
	public ModContainerBlock(String modID, String name, Block.Properties properties) {
		super(properties);
		setBlockName(modID, name);
	}
	
	/**
	 * 
	 * @param modID
	 * @param name
	 */
	public void setBlockName(String modID, String name) {
		setRegistryName(modID, name);
	}
}
