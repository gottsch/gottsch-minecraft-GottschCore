package com.someguyssoftware.gottschcore.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

/**
 * 
 * NonStandard Block is:
 * 1) Not a full-sized block - all sides need to be rendered.
 * @author Mark Gottschling on Jan 2, 2020
 *
 */
public class NonStandardBlock extends ModBlock implements INonStandardBlock {

	protected VoxelShape SHAPE_DEFAULT = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
 
	public NonStandardBlock(String modID, String name, Block.Properties properties) {
      super(modID, name, properties);
    }
  
    /**
     * Gets whether the provided {@link VoxelShape} is opaque
     */
//    public static boolean isOpaque(VoxelShape shape) {
//       return false;
//    }
    
    /**
     * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
     * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
     */
    @Override
    public BlockRenderLayer getRenderLayer() {
//       return BlockRenderLayer.CUTOUT;
    	return BlockRenderLayer.SOLID;
    }
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		   return SHAPE_DEFAULT;
	}
	
    /**
     * 
     * @param n
     * @param e
     * @param s
     * @param w
     */
	@Override
    public void setShape(VoxelShape shape) {
    	this.SHAPE_DEFAULT = shape;
    }
}
