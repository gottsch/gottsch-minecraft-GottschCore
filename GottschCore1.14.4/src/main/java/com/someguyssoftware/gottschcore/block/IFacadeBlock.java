package com.someguyssoftware.gottschcore.block;

import net.minecraft.util.math.shapes.VoxelShape;

/**
 * 
 * @author Mark Gottschling on Jan 11, 2020
 *
 */
public interface IFacadeBlock extends INonStandardBlock, IFacingBlock {

  public VoxelShape[] getShapes();
  public void setShapes(VoxelShape[] shapes);
}
