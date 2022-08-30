/**
 * 
 */
package com.someguyssoftware.gottschcore.block;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/**
 * 
 * @author Mark Gottschling on Jan 15, 2020
 *
 */
public class BasedBlock extends ModBlock implements IBasedBlock {

	public BasedBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(BASE, Direction.UP));
	}
	
	/**
	 * 
	 * @param modID
	 * @param name
	 * @param materialIn
	 */
	@Deprecated
	public BasedBlock(String modID, String name, Block.Properties properties) {
		super(modID, name, properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(BASE, Direction.UP));
	}

	/**
	 * 
	 */
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BASE);
	}

	/**
	 * 
	 */
	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(BASE, context.getClickedFace());
	}

	/**
	 * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
	 * fine.
	 */
	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(BASE, rotation.rotate(getBase(state)));
	}

	/**
	 * 
	 * @param state
	 * @return
	 */
	@Override
	public Direction getBase(BlockState state) {
		return state.getValue(BASE);
	}
}
