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
 * @author Mark Gottschling on Jan 15, 2020
 *
 */
public class BasedBlock extends ModBlock implements IBasedBlock {
	
	/**
	 * 
	 * @param modID
	 * @param name
	 * @param materialIn
	 */
	public BasedBlock(String modID, String name, Block.Properties properties) {
		super(modID, name, properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(BASE, Direction.NORTH));
	}
	
	/**
	 * 
	 */
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BASE);
	}

    
	/**
	 * 
	 */
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
    	return this.getDefaultState().with(BASE, context.getFace());
    }
    
	/**
	 * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
	 * fine.
	 */
	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.with(BASE, rotation.rotate(getBase(state)));
	}
	
	/**
	 * 
	 * @param state
	 * @return
	 */
	@Override
	public Direction getBase(BlockState state) {
		return state.get(BASE);
	}
}
