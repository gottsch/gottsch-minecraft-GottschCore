package com.someguyssoftware.gottschcore.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

/**
 * A NonStandardBlock with the additional properties:
 * 1) contains a VoxelShape for each of the cardinal directions (NESW).
 * 2) contains state property (FACING) for the the cardinal direction the block is facing
 */
public class CardinalDirectionFacadeBlock {//extends NonStandardBlock implements IFacadeBlock {

//	public static final EnumProperty<Direction> FACING = DirectionProperty.create("facing", Direction.class);
//
//	protected VoxelShape SHAPE_NORTH = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
//	protected VoxelShape SHAPE_EAST = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
//	protected VoxelShape SHAPE_SOUTH = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
//	protected VoxelShape SHAPE_WEST = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
//
//	/**
//	 * 
//	 * @param modID
//	 * @param name
//	 * @param properties
//	 */
//	public CardinalDirectionFacadeBlock(String modID, String name, Block.Properties properties) {
//		super(modID, name, properties);
////		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
//		this.setDefaultState(stateContainer.getBaseState().with(BlockStateProperties.FACING, Direction.NORTH));
//		// TODO need a WATERLOGGED version of blocks
//	}
//
//	@Override
//	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
//		switch(state.get(BlockStateProperties.FACING)) {
//		case NORTH:
//		default:
//			return SHAPE_NORTH;
//		case SOUTH:
//			return SHAPE_SOUTH;
//		case WEST:
//			return SHAPE_WEST;
//		case EAST:
//			return SHAPE_EAST;
//		}
//	}
//
//	@Override
//	public BlockState getStateForPlacement(BlockItemUseContext context) {
//		Direction direction = context.getPlacementHorizontalFacing().getOpposite();
//		return this.getDefaultState().with(BlockStateProperties.FACING, direction);
//	}
//
//	/**
//	 * Called by ItemBlocks after a block is set in the world, to allow post-place logic
//	 */
//	@Override
//	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
//	}
//
//	@Override
//	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
//		super.onReplaced(state, worldIn, pos, newState, isMoving);
//	}
//
//	/**
//	 * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
//	 * blockstate.
//	 * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
//	 * fine.
//	 */
//	@Override
//	public BlockState rotate(BlockState state, Rotation rotation) {
//		return state.with(BlockStateProperties.FACING, rotation.rotate(state.get(BlockStateProperties.FACING)));
//	}
//
//	/**
//	 * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
//	 * blockstate.
//	 * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
//	 */
//	@Override
//	public BlockState mirror(BlockState state, Mirror mirrorIn) {
//		return state.rotate(mirrorIn.toRotation(state.get(BlockStateProperties.FACING)));
//	}
//
//	@Override
//	public VoxelShape[] getShapes() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/**
//	 *
//	 */
//	@Override
//	public void setShapes(VoxelShape[] shapes) {
//		// TODO replace integers with ENUMS or vanilla constants
//		this.SHAPE_NORTH = shapes[0];
//		this.SHAPE_EAST = shapes[1];
//		this.SHAPE_SOUTH = shapes[2];
//		this.SHAPE_WEST = shapes[3];
//	}
//
//	@Override
//	public Direction getFacing(BlockState state) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//  
//  /**
//   * Gets whether the provided {@link VoxelShape} is opaque
//   */
//  public static boolean isOpaque(VoxelShape shape) {
//     return false;
//  }

//  /**
//   * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
//   * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
//   */
//  public BlockRenderLayer getRenderLayer() {
//     return BlockRenderLayer.CUTOUT;
//  }
	
	//	@Override
	//	public Direction getFacing() {
	//		return this.stateContainer.getProperty("facing").(FACING);
	//	}

//	@Override
//	public void setFacing(Direction facing) {
//		this.stateContainer.getBaseState().with(BlockStateProperties.FACING, facing);		
//	}
}
