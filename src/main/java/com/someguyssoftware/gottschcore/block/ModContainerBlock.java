/**
 * 
 */
package com.someguyssoftware.gottschcore.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

/**
 * This class replaces BlockContainer so that it can extend ModBlock
 * 
 * @author Mark Gottschling onJan 2, 2018
 *
 */
public abstract class ModContainerBlock extends ModBlock implements ITileEntityProvider {

	/**
	 * 
	 * @param modID
	 * @param name
	 * @param properties
	 */
	public ModContainerBlock(String modID, String name, Block.Properties properties) {
		super(modID, name, properties);
	}

	/**
	 * The type of render function called. MODEL for mixed tesr and static model,
	 * MODELBLOCK_ANIMATED for TESR-only, LIQUID for vanilla liquids, INVISIBLE to
	 * skip all rendering. Since this class is abstract, default to no rendering.
	 */
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	/**
	 * Called on server when World#addBlockEvent is called. If server returns true, then also called on the client. On
	 * the Server, this may perform additional changes to the world, like pistons replacing the block with an extended
	 * base. On the client, the update may involve replacing tile entities or effects such as sounds or particles
	 * @deprecated call via {@link IBlockState#onBlockEventReceived(World,BlockPos,int,int)} whenever possible.
	 * Implementing/overriding is fine.
	 */
	@Override
	public boolean triggerEvent(BlockState state, World world, BlockPos pos, int id, int param) {
		super.triggerEvent(state, world, pos, id, param);
		TileEntity tileEntity = world.getBlockEntity(pos);
		return tileEntity == null ? false : tileEntity.triggerEvent(id, param);
	}

	@Nullable
	public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
		TileEntity tileentity = worldIn.getBlockEntity(pos);
		return tileentity instanceof INamedContainerProvider ? (INamedContainerProvider)tileentity : null;
	}
}
