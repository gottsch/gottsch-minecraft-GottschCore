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
package com.someguyssoftware.gottschcore.world.gen.structure;

import java.util.List;
import java.util.Random;

import com.someguyssoftware.gottschcore.spatial.ICoords;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author Mark Gottschling on Dec 8, 2019
 *
 */
public interface IDecayProcessor {

	void add(ICoords coords, GottschTemplate.BlockInfo blockInfo, BlockState state);

	List<DecayBlockInfo> process(Level world, Random random, ICoords size, Block nullBlock);

	public IDecayRuleSet getRuleSet();

	boolean isBackFill();

	void setBackFill(boolean backFill);

	BlockState getBackFillBlockLayer1();

	void setBackFillBlockLayer1(BlockState backFillBlockLayer1);

	BlockState getBackFillBlockLayer2();

	void setBackFillBlockLayer2(BlockState backFillBlockLayer2);

	int getDecayStartY();

	void setDecayStartY(int decayStartY);
}
