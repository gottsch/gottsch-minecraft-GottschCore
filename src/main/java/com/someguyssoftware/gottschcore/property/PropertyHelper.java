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
package com.someguyssoftware.gottschcore.property;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * @author Mark Gottschling on Dec 23, 2019
 *
 */
public class PropertyHelper {
	public static final EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class);
	// copiers
//	public static IPropertyCopier stairsPropertyCopier = new StairsPropertyCopier();
//	public static IPropertyCopier logPropertyCopier = new LogPropertyCopier();
//	public static IPropertyCopier oldLogPropertyCopier = new OldLogPropertyCopier();
//	public static IPropertyCopier vinePropertyCopier = new VinePropertyCopier();
//	public static IPropertyCopier wallPropertyCopier = new WallPropertyCopier();
//	public static IPropertyCopier fenceGatePropertyCopier = new FenceGatePropertyCopier();
//	public static IPropertyCopier planksPropertyCopier = new PlanksPropertyCopier();
	
	// default copier
	public static IPropertyCopier facingPropertyCopier = new FacingPropertyCopier();
	
	static {
		// register all the blocks with the rotators in the RotatorRegistry
		PropertyHelper.registerBlocks();
	}
	
	/**
	 * 
	 */
	private PropertyHelper() {	}
	
	private static void registerBlocks() {
		PropertyCopierRegistry registry = PropertyCopierRegistry.getInstance();
//		registry.register(StairsBlock.class, stairsPropertyCopier);
//		registry.register(LogBlock.class, logPropertyCopier);
//		registry.register(OldLogBlock.class, oldLogPropertyCopier);
//		registry.register(VineBlock.class, vinePropertyCopier);
//		registry.register(FenceBlock.class, vinePropertyCopier);
//		registry.register(FenceGateBlock.class, fenceGatePropertyCopier);
//		registry.register(WallBlock.class, wallPropertyCopier);
//		registry.register(PlanksBlock.class, planksPropertyCopier);
	}
	
	public static BlockState copyProperties(BlockState sourceState, BlockState destState) {
		Block block = destState.getBlock();

		// check against the list of blocks to ignore
		if (block == Blocks.AIR
				|| block instanceof SlabBlock) return destState;
		
		// determine which rotator implementation to use
		IPropertyCopier copier = null;
		if (PropertyCopierRegistry.getInstance().has(block.getClass())) {
			copier = PropertyCopierRegistry.getInstance().get(block.getClass());
		}
		// most common property/rotator
		else if (destState.getProperties().contains(FACING)) {
			copier = facingPropertyCopier;
		}
		
		if (copier == null) {
			return destState;
		}		
		return copier.copy(sourceState, destState);
	}
}
