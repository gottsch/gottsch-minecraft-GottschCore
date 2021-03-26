/**
 * 
 */
package com.someguyssoftware.gottschcore.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;

/**
 * 
 * @author Mark Gottschling on Jan 14, 2020
 *
 */
public class FacingBlock extends ModBlock implements IFacingBlock {
	
	/**
	 * 
	 * @param modID
	 * @param name
	 * @param materialIn
	 */
	public FacingBlock(String modID, String name, Block.Properties properties) {
		super(modID, name, properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}
	
	/**
	 * 
	 */
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

    
	/**
	 * 
	 */
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
    	return this.defaultBlockState().setValue(FACING, context.getPlayer().getDirection().getOpposite());
    }
    
	/**
	 * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
	 * fine.
	 */
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(getFacing(state)));
	}
	
	/**
	 * 
	 * @param state
	 * @return
	 */
	@Override
	public Direction getFacing(BlockState state) {
		return state.getValue(FACING);
	}
}
