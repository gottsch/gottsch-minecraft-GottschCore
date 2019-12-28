/**
 * 
 */
package com.someguyssoftware.gottschcore.property;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockVine;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;


/**
 * @author Mark Gottschling on Dec 23, 2019
 *
 */
public class PropertyHelper {
	private static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	// copiers
	public static IPropertyCopier stairsPropertyCopier = new StairsPropertyCopier();
	public static IPropertyCopier logPropertyCopier = new LogPropertyCopier();
	public static IPropertyCopier oldLogPropertyCopier = new OldLogPropertyCopier();
	public static IPropertyCopier vinePropertyCopier = new VinePropertyCopier();
	// default copier
	public static IPropertyCopier facingPropertyCopier = new FacingPropertyCopier();
	
	static {
		// register all the blocks with the rotators in the RotatorRegistry
		PropertyHelper.registerBlocks();
	}
	
	/**
	 * 
	 */
	private PropertyHelper() {	}
	
	private static void registerBlocks() {
		PropertyCopierRegistry registry = PropertyCopierRegistry.getInstance();
		registry.register(BlockStairs.class, stairsPropertyCopier);
		registry.register(BlockLog.class, logPropertyCopier);
		registry.register(BlockOldLog.class, oldLogPropertyCopier);
		registry.register(BlockVine.class, vinePropertyCopier);
	}
	
	public static IBlockState copyProperties(IBlockState sourceState, IBlockState destState) {
		Block block = destState.getBlock();

		// check against the list of blocks to ignore
		if (block == Blocks.AIR
				|| block instanceof BlockSlab) return destState;
		
		// determine which rotator implementation to use
		IPropertyCopier copier = null;
		if (PropertyCopierRegistry.getInstance().has(block.getClass())) {
			copier = PropertyCopierRegistry.getInstance().get(block.getClass());
		}
		// most common property/rotator
		else if (destState.getProperties().containsKey(FACING)) {
			copier = facingPropertyCopier;
		}
		
		if (copier == null) {
			return destState;
		}		
		return copier.copy(sourceState, destState);
	}
}
