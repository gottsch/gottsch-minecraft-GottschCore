package com.someguyssoftware.gottschcore.block;

import net.minecraft.util.math.shapes.VoxelShape;

/**
 * 
 * @author Mark Gottschling on Jan 11, 2020
 *
 */
public interface INonStandardBlock extends IGottschCoreBlock {
  
  public void setShape(VoxelShape shape);
}
