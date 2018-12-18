/**
 * 
 */
package com.someguyssoftware.gottschcore.positional;

import javax.annotation.concurrent.Immutable;

import net.minecraft.util.math.AxisAlignedBB;

/**
 * Wrapper for AxisAlignedBB
 * @author Mark Gottschling on Dec 14, 2018
 *
 */
@Immutable
public class BBox {

	private ICoords minCoords;
	private ICoords maxCoords;
	
	/**
	 * 
	 */	
	public BBox(final ICoords c1, final ICoords c2) {
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
	public BBox(final ICoords c) {
		setMinCoords(new Coords(c));
		setMaxCoords(c.add(1, 1, 1));
	}
	
	public AxisAlignedBB toAABB() {
		return new AxisAlignedBB(this.minCoords.toPos(), this.maxCoords.toPos());
	}

	/**
	 * @return the minCoords
	 */
	protected ICoords getMinCoords() {
		return minCoords;
	}

	/**
	 * @param minCoords the minCoords to set
	 */
	protected void setMinCoords(ICoords minCoords) {
		this.minCoords = minCoords;
	}

	/**
	 * @return the maxCoords
	 */
	protected ICoords getMaxCoords() {
		return maxCoords;
	}

	/**
	 * @param maxCoords the maxCoords to set
	 */
	protected void setMaxCoords(ICoords maxCoords) {
		this.maxCoords = maxCoords;
	}
}
