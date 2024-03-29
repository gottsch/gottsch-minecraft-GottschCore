/*
 * This file is part of  GottschCore.
 * Copyright (c) 2019 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.gottschcore.block.entity;

import mod.gottsch.forge.gottschcore.spatial.ICoords;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * 
 * @author Mark Gottschling on Feb 1, 2019
 *
 */
public interface IProximityBlockEntity {

	void execute(Level level, RandomSource random, ICoords blockCoords, ICoords playerCoords);

	double getProximity();

	void setProximity(double proximity);

}
