/*
 * This file is part of  GottschCore.
 * Copyright (c) 2021 Mark Gottschling (gottsch)
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


import java.util.Objects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;

/**
 *  GottschCore replacement for AABB.
 * @author Mark Gottschling on Oct 30, 2021
 *
 */
public class Box {
	// TODO determine what invalid y is (important when moving to mc1.17)
	public static final Box EMPTY = new Box(new Coords(0, -255, 0), new Coords(0, -255, 0));
	private static final String MIN_COORDS = "min";
	private static final String MAX_COORDS = "max";
	private ICoords minCoords;
	private ICoords maxCoords;
	
	/**
	 * 
	 */	
	public Box(final ICoords c1, final ICoords c2) {
		int minX = Math.min(c1.getX(), c2.getX());
		int minY = Math.min(c1.getY(), c2.getY());
		int minZ = Math.min(c1.getZ(), c2.getZ());
		int maxX = Math.max(c1.getX(), c2.getX());
		int maxY = Math.max(c1.getY(), c2.getY());
		int maxZ = Math.max(c1.getZ(), c2.getZ());
		
		setMinCoords(new Coords(minX, minY, minZ));
		setMaxCoords(new Coords(maxX, maxY, maxZ));
	}

	/**
	 * 
	 * @param c
	 */
	public Box(final ICoords c) {
		setMinCoords(new Coords(c));
		setMaxCoords(c.add(1, 1, 1));
	}
	
	/*
	 * Constructor from AxisAlignedBB.
	 */
	public Box(final AABB aabb) {
		setMinCoords(new Coords((int)aabb.minX, (int)aabb.minY, (int)aabb.minZ));
		setMaxCoords(new Coords((int)aabb.maxX, (int)aabb.maxY, (int)aabb.maxZ));
	}

	public AABB toAABB(Box box) {
		return new AABB(box.getMinCoords().toPos(), box.getMaxCoords().toPos());
	}

	/**
	 * 
	 * @return
	 */
	public ICoords getSize() {
		return getMaxCoords().delta(getMinCoords());
	}

	// TODO make non-static.
	// TODO remove box1 -> use this.
	public static boolean touching(Box box1, Box box2) {
		return box1.getMinCoords().getX() <= box2.getMaxCoords().getX()
				&& box1.getMaxCoords().getX() >= box2.getMinCoords().getX()
				&& box1.getMinCoords().getY() <= box2.getMaxCoords().getY()
				&& box1.getMaxCoords().getY() >= box2.getMinCoords().getY()
				&& box1.getMinCoords().getZ() <= box2.getMaxCoords().getZ()
				&& box1.getMaxCoords().getZ() >= box2.getMinCoords().getZ();
	}

	public static boolean contains(Box box1, Box box2) {
		return contains(box1, box2.getMinCoords())
				&& contains(box1, box2.getMaxCoords());
	}

	public static boolean contains(Box box, ICoords coords) {
		return coords.getX() >= box.getMinCoords().getX() && coords.getX() <= box.getMaxCoords().getX()
				&& coords.getY() >= box.getMinCoords().getY() && coords.getY() <= box.getMaxCoords().getY()
				&& coords.getZ() >= box.getMinCoords().getZ() && coords.getZ() <= box.getMaxCoords().getZ();
	}

	/**
	 * 
	 * @param nbt
	 */
	public void save(CompoundTag nbt) {
		CompoundTag coordsNbt1 = new CompoundTag();
		CompoundTag coordsNbt2 = new CompoundTag();

		getMinCoords().save(coordsNbt1);
		getMaxCoords().save(coordsNbt2);

		nbt.put(MIN_COORDS, coordsNbt1);
		nbt.put(MAX_COORDS, coordsNbt2);
	}

	/**
	 * 
	 * @param nbt
	 * @return
	 */
	public static Box load(CompoundTag nbt) {
		Box box;
		ICoords c1;
		ICoords c2;
		if (nbt.contains(MIN_COORDS)) {
			c1 = Coords.EMPTY.load(nbt.getCompound(MIN_COORDS));
		}
		else {
			return Box.EMPTY;
		}
		
		if (nbt.contains(MAX_COORDS)) {
			c2 = Coords.EMPTY.load(nbt.getCompound(MAX_COORDS));
		}
		else {
			return Box.EMPTY;
		}		
		box = new Box(c1, c2);

		return box;
	}
	
	/**
	 * @return the minCoords
	 */
	public ICoords getMinCoords() {
		return minCoords;
	}

	/**
	 * @param minCoords the minCoords to set
	 */
	public void setMinCoords(ICoords minCoords) {
		this.minCoords = minCoords;
	}

	/**
	 * @return the maxCoords
	 */
	public ICoords getMaxCoords() {
		return maxCoords;
	}

	/**
	 * @param maxCoords the maxCoords to set
	 */
	public void setMaxCoords(ICoords maxCoords) {
		this.maxCoords = maxCoords;
	}

	@Override
	public String toString() {
		return "Box [minCoords=" + minCoords.toShortString() + ", maxCoords=" + maxCoords.toShortString() + "]";
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(maxCoords, minCoords);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Box other = (Box) obj;
		return Objects.equals(maxCoords, other.maxCoords) && Objects.equals(minCoords, other.minCoords);
	}
}