/**
 * 
 */
package com.someguyssoftware.gottschcore.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A full-sized (meaning, the bounding box is full-sized) block that has direction and is always facing the player when placed, or north by default.
 * @author Mark Gottschling on Aug 31, 2016
 *
 */
public class CardinalDirectionBlock extends ModBlock {

	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	
	/**
	 * 
	 * @param modID
	 * @param name
	 * @param materialIn
	 */
	public CardinalDirectionBlock(String modID, String name, Block.Properties properties) {
		super(modID, name, properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}
    
    /**
     * Gets whether the provided {@link VoxelShape} is opaque
     */
    public static boolean isOpaque(VoxelShape shape) {
       return false;
    }

    /**
     * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
     * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
     */
    public BlockRenderLayer getRenderLayer() {
       return BlockRenderLayer.CUTOUT;
    }
    
    @OnlyIn(Dist.CLIENT)
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
    	return this.getDefaultState().with(FACING, context.getPlayer().getHorizontalFacing().getOpposite());
    }
}
