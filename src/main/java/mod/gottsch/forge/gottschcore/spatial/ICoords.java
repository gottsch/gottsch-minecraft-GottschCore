/*
 * This file is part of  GottschCore.
 * Copyright (c) 2017 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.gottschcore.spatial;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

/**
 * This class is a wrapper for Minecraft positional classes and calculations.
 * Ex. BlockPos in 1.8+, or for (x, y, z) in &lt;1.8
 * 
 * @author Mark Gottschling on May 5, 2017
 *
 */
public interface ICoords {

	/**
	 * Convert to Minecraft BlockPos object
	 * 
	 * @return
	 */
	public BlockPos toPos();

	/**
	 * Convert to Minecraft ChunkPos object
	 * 
	 * @return
	 */
	public ChunkPos toChunkPos();

	/**
	 * Convenience method
	 * 
	 * @param axis
	 * @return
	 */
	public int get(int axis);

	/**
	 * Convenience method
	 * 
	 * @param axis
	 * @return
	 */
	public int get(char axis);

	public int getX();

	public int getY();

	public int getZ();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString();
	
	public ICoords withY(ICoords coords);

	public ICoords withY(int y);

	public ICoords withX(ICoords coords);

	public ICoords withX(int x);

	public ICoords withZ(ICoords coords);

	public ICoords withZ(int z);

	public ICoords add(int x, int y, int z);

	public ICoords add(ICoords coords);

	public ICoords delta(ICoords coords);
	public ICoords delta(BlockPos pos);
	
	public ICoords rotate90(int width);

	public ICoords rotate180(int depth, int width);

	public ICoords rotate270(int depth);

	public String toShortString();

	/**
	 * @param toX
	 * @param toY
	 * @param toZ
	 * @return
	 */
	double getDistanceSq(double toX, double toY, double toZ);

	/**
	 * @param coords
	 * @return
	 */
	double getDistanceSq(ICoords coords);

	/**
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	double getDistance(double x, double y, double z);

	/**
	 * @param coords
	 * @return
	 */
	double getDistance(ICoords coords);

	/**
	 * @param targetX
	 * @param targetZ
	 * @return
	 */
	double getXZAngle(double targetX, double targetZ);

	/**
	 * @param coords
	 * @return
	 */
	double getXZAngle(ICoords coords);

	/**
	 * @param n
	 * @return
	 */
	ICoords up(int n);

	/**
	 * 
	 * @param n
	 * @return
	 */
	ICoords down(int n);

	/**
	 * 
	 * @param n
	 * @return
	 */
	ICoords north(int n);

	/**
	 * 
	 * @param n
	 * @return
	 */
	ICoords south(int n);

	/**
	 * 
	 * @param n
	 * @return
	 */
	ICoords east(int n);

	/**
	 * 
	 * @param n
	 * @return
	 */
	ICoords west(int n);

	/**
	 * 
	 * @param heading
	 * @param n
	 * @return
	 */
	ICoords add(Heading heading, int n);
	
	/**
	 * 
	 * @param direction
	 * @param n
	 * @return
	 */
	ICoords add(Direction direction, int n);

	ICoords rotate(double xlen, double zlen, double degrees);

	ICoords offset(Direction facing);

//	ICoords withY(ICoords coords);
//
//	ICoords withY(int y);
//
//	ICoords withX(ICoords coords);
//
//	ICoords withX(int x);
//
//	ICoords withZ(ICoords coords);
//
//	ICoords withZ(int z);

	public Vec3 toVec3d();

	CompoundTag save(CompoundTag tag);

	/**
	 * Remember Coords are immutable so it returns a new Coords.
	 */
	ICoords load(CompoundTag tag);

	ICoords offset(ICoords coords);

	ICoords offset(int x, int y, int z);

	Vec3 toVec3();

	ICoords negate();

}