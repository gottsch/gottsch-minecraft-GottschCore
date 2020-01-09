package com.someguyssoftware.gottschcore.block;

import net.minecraft.block.Block;

/**
 * A NonStandardBlock with the additional properties:
 * 1) contains a VoxelShape for each of the cardinal directions (NESW).
 * 2) contains state property (FACING) for the the cardinal direction the block is facing
 */
public class CardinalDirectionNonStandardBlock extends NonStandardBlock {

	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

	protected VoxelShape SHAPE_NORTH = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected VoxelShape SHAPE_EAST = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected VoxelShape SHAPE_SOUTH = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected VoxelShape SHAPE_WEST = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
 
	public CardinalDirectionNonStandardBlock(String modID, String name, Block.Properties properties) {
		super(modID, name, properties);
	}
    
   /**
    * 
    * @param n
    * @param e
    * @param s
    * @param w
    */
   public void setShape(VoxelShape north, VoxelShape east, VoxelShape south, VoxelShape west) {
    this.SHAPE_NORTH = north;
     this.SHAPE_EAST = east;
     this.SHAPE_SOUTH = south;
     this.SHAPE_WEST = west;
   }
   
   /**
    *
    */
   public void setShape(VoxelShape[] shapes) {
    // TODO replace integers with ENUMS or vanilla constants
    this.SHAPE_NORTH = shapes[0];
    this.SHAPE_EAST = shapes[1];
    this.SHAPE_SOUTH = shapes[2];
    this.SHAPE_WEST = shapes[3];
   }
    
}
