/*
 * This file is part of  GottschCore.
 * Copyright (c) 2022 Mark Gottschling (gottsch)
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
package com.someguyssoftware.gottschcore.spatial;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

/**
 * 
 * @author Mark Gottschling on Sep 19, 2022
 *
 */
public class Coords2D {
	private int x;
	private int y;
	
	/**
	 * 
	 */
	public Coords2D() {}
	
	/**
	 * 
	 * @param x
	 * @param y
	 */
	public Coords2D(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Coords2D(Coords2D coords) {
		this(coords.getX(), coords.getY());		
	}
	
	/**
	 * Copy constructor from BlockPos
	 * 
	 * @param pos
	 */
	public Coords2D(BlockPos pos) {
		this(pos.getX(), pos.getZ());
	}
	
	/**
	 * Copy constructor from Vec3i
	 * 
	 * @param vec
	 */
	public Coords2D(Vector3i vec) {
		this(MathHelper.floor(vec.getX()), MathHelper.floor(vec.getZ()));
	}
	
	/**
	 * Copy constructor from Vec3d
	 * @param vec
	 */
	public Coords2D(Vector3d vec) {
		this(MathHelper.floor(vec.x), MathHelper.floor(vec.z));
	}
	
	public double distance(Coords2D destination) {
	    double d0 = this.getX() - destination.getX();
	    double d1 = this.getY() - destination.getY();
	    return Math.sqrt(d0 * d0 + d1 * d1);
	}
	
    public void translate(int xDistance, int yDistance) {
        this.x += xDistance;
        this.y += yDistance;
    }
    
	public void setLocation(int x, int y) {
		setX(x);
		setY(y);
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "Coords2D [x=" + x + ", y=" + y + "]";
	}
}
