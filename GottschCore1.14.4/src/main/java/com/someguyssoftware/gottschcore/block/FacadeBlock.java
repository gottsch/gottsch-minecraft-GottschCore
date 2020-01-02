package com.someguyssoftware.gottschcore.block;

/**
 * 
 * Facade Block is:
 * 1) Not a full-sized block - all sides need to be rendered.
 * 2) Has FACING property that has values in the cardinal directions only.
 * 3) Faces NORTH by default
 * 4) Faces the player on placement.
 * 5) Its bounding box touches the NORTH face. The other sides may vary.
 * @author Mark Gottschling on Jan 2, 2020
 *
 */
public class FacadeBlock extends ModBlock {
  // TODO add FACING property
  // TODO add AxisAlignedBB for NESW
  
    /**
     * 
     * @param n
     * @param e
     * @param s
     * @param w
     */
    public void setBoundingBox(AxisAlignedBB north, AxisAlignedBB east, AxisAlignedBB south, AxisAlignedBB west) {
    	this.NORTH_AABB = north;
    	this.EAST_AABB = east;
    	this.SOUTH_AABB = south;
    	this.WEST_AABB = west;
    }
}
