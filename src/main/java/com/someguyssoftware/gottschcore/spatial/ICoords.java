package com.someguyssoftware.gottschcore.spatial;

import com.mojang.math.Vector3d;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;

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

	public ICoords add(int x, int y, int z);

	public ICoords add(ICoords coords);

	public ICoords delta(ICoords coords);

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
	 * @param y
	 * @return
	 */
	ICoords resetY(int y);

	/**
	 * 
	 * @param z
	 * @return
	 */
	ICoords resetZ(int z);

	/**
	 * 
	 * @param x
	 * @return
	 */
	ICoords resetX(int x);

	/**
	 * 
	 * @param parentNBT
	 */
	public static ICoords readFromNBT(CompoundTag tag) {
		Integer x = null;
		Integer y = null;
		Integer z = null;
		ICoords coords = null;
		if (tag.contains("x")) {
			x = tag.getInt("x");
		}
		if (tag.contains("y")) {
			y = tag.getInt("y");
		}
		if (tag.contains("z")) {
			z = tag.getInt("z");
		}
		if (x != null && y != null && z != null) {
			coords = new Coords(x, y, z);
		}
		return coords;
	}

	/**
	 * 
	 * @param nbt
	 * @return
	 */
	CompoundTag writeToNBT(CompoundTag nbt);

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

	ICoords withY(ICoords coords);

	ICoords withY(int y);

	ICoords withX(ICoords coords);

	ICoords withX(int x);

	ICoords withZ(ICoords coords);

	ICoords withZ(int z);

	public Vector3d toVec3d();

	CompoundTag save(CompoundTag tag);

	/**
	 * Remember Coords are immutable so it returns a new Coords.
	 */
	ICoords load(CompoundTag tag);

	ICoords offset(ICoords coords);

	ICoords offset(int x, int y, int z);

}