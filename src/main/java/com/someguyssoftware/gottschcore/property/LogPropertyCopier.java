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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * 
 * @author Mark Gottschling on Dec 26, 2019
 *
 */
public class LogPropertyCopier implements IPropertyCopier {
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    
    @Override
	public BlockState copy(BlockState source, BlockState dest) {
		if (dest.getProperties().contains(AXIS)) {
			dest = dest.setValue(AXIS, source.getValue(AXIS));
		}

		return dest;
	}

}
