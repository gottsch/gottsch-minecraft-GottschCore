/*
 * This file is part of  GottschCore.
 * Copyright (c) 2020 Mark Gottschling (gottsch)
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

import javax.annotation.concurrent.Immutable;

import com.mojang.math.Vector3d;

import mod.gottsch.forge.gottschcore.GottschCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

/**
 * 
 * @author Mark Gottschling on Feb 26, 2020
 *
 */
@Immutable
public class Coords implements ICoords {
	private final int x;
	private final int y;
	private final int z;
	
	public static final Coords EMPTY = new Coords(0, -255, 0);

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Coords(final int x, final int y, final int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * 
	 * @param coords
	 */
	public Coords(ICoords coords) {
		this(coords.getX(), coords.getY(), coords.getZ());
	}

	/**
	 * Copy constructor from BlockPos
	 * 
	 * @param pos
	 */
	public Coords(BlockPos pos) {
		this(pos.getX(), pos.getY(), pos.getZ());
	}

	public Coords(Vec3 vec) {
		this(Mth.floor(vec.x), Mth.floor(vec.y), Mth.floor(vec.z));
	}
	
	/**
	 * Copy constructor from Vec3i
	 * 
	 * @param vec
	 */
	public Coords(Vec3i vec) {
		this(Mth.floor(vec.getX()), Mth.floor(vec.getY()), Mth.floor(vec.getZ()));
	}
	
	/**
	 * Copy constructor from Vec3d
	 * @param vec
	 */
	public Coords(Vector3d vec) {
		this(Mth.floor(vec.x), Mth.floor(vec.y), Mth.floor(vec.z));
	}

	
	/**
	 * Offset this Coords n blocks up
	 * 
	 * @param n the amount to offset by
	 */
	@Override
	public ICoords up(int n) {
		return new Coords(this.getX(), this.getY() + n, this.getZ());
	}

	/**
	 * Offset this Coords n blocks down
	 */
	@Override
	public ICoords down(int n) {
		return new Coords(this.getX(), this.getY() - n, this.getZ());
	}

	/**
	 * Offset this Coords n blocks north
	 */
	@Override
	public ICoords north(int n) {
		return new Coords(this.getX(), this.getY(), this.getZ() - n);
	}

	/**
	 * Offset this Coords n blocks south
	 */
	@Override
	public ICoords south(int n) {
		return new Coords(this.getX(), this.getY(), this.getZ() + n);
	}

	/**
	 * Offset this Coords n blocks east
	 */
	@Override
	public ICoords east(int n) {
		return new Coords(this.getX() + n, this.getY(), this.getZ());
	}

	/**
	 * Offset this Coords n blocks west
	 */
	@Override
	public ICoords west(int n) {
		return new Coords(this.getX() - n, this.getY(), this.getZ());
	}

	/**
	 * Offset this Coords 1 block in the given direction
	 */
	@Override
	public ICoords offset(Direction facing) {
		return switch (facing) {
		case NORTH -> this.north(1);
		case EAST -> this.east(1);
		case SOUTH -> this.south(1);
		case WEST -> this.west(1);
		case UP -> this.up(1);
		case DOWN -> this.down(1);
		default -> this.north(1);
		};
	}
	
	// TODO add offset(Heading)

	@Override
	public ICoords offset(ICoords coords) {
		return offset(coords.getX(), coords.getY(), coords.getZ());
	}
	
	@Override
	public ICoords offset(int x, int y, int z) {
		return new Coords(toPos().offset(x, y, z));
	}
	
	/**
	 * Calculate squared distance to the given coordinates
	 * 
	 * @param toX
	 * @param toY
	 * @param toZ
	 * @since 1.0
	 */
	@Override
	public double getDistanceSq(double toX, double toY, double toZ) {
		double d0 = this.getX() - toX;
		double d1 = this.getY() - toY;
		double d2 = this.getZ() - toZ;
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	/**
	 * 
	 * @param coords
	 * @return
	 * @since 1.0
	 */
	@Override
	public double getDistanceSq(ICoords coords) {
		return getDistanceSq(coords.getX(), coords.getY(), coords.getZ());
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 * @since 1.0
	 */
	@Override
	public double getDistance(double x, double y, double z) {
		double d0 = this.getX() - x;
		double d1 = this.getY() - y;
		double d2 = this.getZ() - z;
		return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
	}

	/**
	 * 
	 * @param coords
	 * @return
	 * @since 1.0
	 */
	@Override
	public double getDistance(ICoords coords) {
		return getDistance(coords.getX(), coords.getY(), coords.getZ());
	}

	/**
	 * 
	 * @param targetX
	 * @param targetZ
	 * @return
	 * @since 1.0
	 */
	@Override
	public double getXZAngle(double targetX, double targetZ) {
		double angle = Math.toDegrees(Math.atan2(targetZ - getZ(), targetX - getX()));
		if (angle < 0) {
			angle += 360;
		}
		return angle;
	}

	/**
	 * 
	 * @param coords
	 * @return
	 * @since 1.0
	 */
	@Override
	public double getXZAngle(ICoords coords) {
		return getXZAngle(coords.getX(), coords.getZ());
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return new instance
	 */
	@Override
	public ICoords add(int x, int y, int z) {
		ICoords coords = new Coords(this.x + x, this.y + y, this.z + z);
		return coords;
	}

	/**
	 * 
	 * @param coords
	 * @return new instance
	 */
	@Override
	public ICoords add(ICoords coords) {
		ICoords c = new Coords(this.x + coords.getX(), this.y + coords.getY(), this.z + coords.getZ());
		return c;
	}

	/**
	 * 
	 * @param heading
	 * @param n
	 * @return
	 */
	@Override
	public ICoords add(Heading heading, int n) {
		switch (heading) {
		case NORTH:
			return this.north(n);
		case EAST:
			return this.east(n);
		case SOUTH:
			return this.south(n);
		case WEST:
			return this.west(n);
		case UP:
			return this.up(n);
		case DOWN:
			return this.down(n);

		default:
			return this;
		}
	}
	
	/**
	 * 
	 * @param direction
	 * @param n
	 * @return
	 */
	@Override
	public ICoords add(Direction direction, int n) {
		return add(Heading.fromDirection(direction), n);
	}
	
	@Override
	public ICoords withX(ICoords coords) {
		return new Coords(coords.getX(), this.getY(), this.getZ());
	}
	
	@Override
	public ICoords withX(int x) {
		return new Coords(x, this.getY(), this.getZ());
	}

	@Override
	public ICoords withY(ICoords coords) {
		return new Coords(this.getX(), coords.getY(), this.getZ());
	}

	@Override
	public ICoords withY(int y) {
		return new Coords(this.getX(), y, this.getZ());
	}

	@Override
	public ICoords withZ(ICoords coords) {
		return new Coords(this.getX(), this.getY(), coords.getZ());
	}

	@Override
	public ICoords withZ(int z) {
		return new Coords(this.getX(), this.getY(), z);
	}

	/**
	 * Delta between this and input ie. this.[xyz] - input.[xyz]
	 * 
	 * @param coords
	 * @return new instance
	 */
	@Override
	public ICoords delta(ICoords coords) {
		ICoords c = new Coords(this.x - coords.getX(), this.y - coords.getY(), this.z - coords.getZ());
		return c;
	}
	
	@Override
	public ICoords delta(BlockPos pos) {
		return delta(new Coords(pos));
	}

	@Override
	public ICoords negate() {
		return new Coords(-this.getX(), -this.getY(), -this.getZ());
	}
	
	/**
	 * 
	 * @return new BlockPos instance
	 */
	@Override
	public BlockPos toPos() {
		return new BlockPos(getX(), getY(), getZ());
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public ChunkPos toChunkPos() {
		return new ChunkPos(toPos());
	}
	
	@Override
	public Vec3 toVec3() {
		return new Vec3(getX(), getY(), getZ());
	}
	
	@Override
	public Vector3d toVec3d() {
		return new Vector3d(getX(), getY(), getZ());
	}

	/**
	 * 
	 * @param xlen
	 * @param zlen
	 * @param degrees
	 * @return
	 */
	@Override
	public ICoords rotate(double xlen, double zlen, final double degrees) {
		// convert degrees to radian
		double s = Math.sin(Math.toRadians(degrees));
		double c = Math.cos(Math.toRadians(degrees));

		// rotate point
		double xnew = xlen * c - zlen * s;
		double znew = xlen * s + zlen * c;

		// translate point back:
		ICoords coords = this.add((int) xnew, 0, (int) znew);
		return coords;
	}

	/**
	 * Rotate 90 degrees.
	 * 
	 * @param width
	 * @return new instance
	 */
	@Override
	public ICoords rotate90(int width) {
		ICoords coords = new Coords(width - this.getZ() - 1, this.getY(), this.getX());
		return coords;
	}

	/**
	 * Rotate 180 degrees
	 * 
	 * @param depth
	 * @param width
	 * @return new instance
	 */
	@Override
	public ICoords rotate180(int depth, int width) {
		ICoords coords = new Coords(width - this.getX() - 1, this.getY(), depth - this.getZ() - 1);
		return coords;
	}

	/**
	 * 
	 * @param depth
	 * @return new instance
	 */
	@Override
	public ICoords rotate270(int depth) {
		ICoords coords = new Coords(this.z, this.y, depth - this.getX() - 1);
		return coords;
	}

	/**
	 * Convenience method
	 * 
	 * @param axis
	 * @return
	 * @since 1.0
	 */
	@Override
	public int get(int axis) {
		switch (axis) {
		case 0:
			return this.x;
		case 1:
			return this.y;
		case 2:
			return this.z;
		default:
			return this.x;
		}
	}

	/**
	 * Convenience method
	 * 
	 * @param axis
	 * @return
	 * @since 1.0
	 */
	@Override
	public int get(char axis) {
		switch (axis) {
		case 'x':
		case 'X':
			return this.x;
		case 'y':
		case 'Y':
			return this.y;
		case 'z':
		case 'Z':
			return this.z;
		default:
			return this.x;
		}
	}
	
	@Override
	public CompoundTag save(CompoundTag tag) {
		try {
			tag.putInt("x", getX());
			tag.putInt("y", getY());
			tag.putInt("z", getZ());
		} catch (Exception e) {
			GottschCore.LOGGER.error("Unable to write state to NBT:", e);
		}
		return tag;
	}

	// TODO since Coords is immutable, should we have a CoordsHelper external class that does the loading?
	/**
	 * Remember Coords are immutable so it returns a new Coords.
	 */
	@Override
	public ICoords load(CompoundTag tag) {
		Integer x = null;
		Integer y = null;
		Integer z = null;
		if (tag.contains("x")) {
			x = tag.getInt("x");
		}
		if (tag.contains("y")) {
			y = tag.getInt("y");
		}
		if (tag.contains("z")) {
			z = tag.getInt("z");
		}
		ICoords coords = new Coords(x, y, z);		
		return coords;
	}
	
	@Override
	public int getX() {
		return x;
	}


	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getZ() {
		return z;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coords other = (Coords) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Coords [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

	@Override
	public String toShortString() {
		return x + " " + y + " " + z;
	}

	/**
	 * @param pos
	 * @return
	 */
	public static String toShortString(BlockPos pos) {
		return pos.getX() + " " + pos.getY() + " " + pos.getZ();
	}
}
