/*
 * This file is part of  Treasure2.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
 *
 * Treasure2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Treasure2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Treasure2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.gottschcore.world;

import java.util.Random;

import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/**
 * 
 * @author Mark Gottschling Feb 10, 2023
 *
 */
public class WorldGenContext implements IWorldGenContext {

	private ServerLevelAccessor level;
	private ChunkGenerator chunkGenerator;
	private Random random;
	
	public WorldGenContext(ServerLevelAccessor level, ChunkGenerator chunkGenerator, Random random) {
		this.level = level;
		this.chunkGenerator = chunkGenerator;
		this.random = random;
	}
	
	public WorldGenContext(FeaturePlaceContext<?> context) {
		this(context.level(), context.chunkGenerator(), context.random());
	}
	
	@Override
	public ServerLevelAccessor level() {
		return level;
	}

	@Override
	public ChunkGenerator chunkGenerator() {
		return chunkGenerator;
	}

	@Override
	public Random random() {
		return random;
	}
}
