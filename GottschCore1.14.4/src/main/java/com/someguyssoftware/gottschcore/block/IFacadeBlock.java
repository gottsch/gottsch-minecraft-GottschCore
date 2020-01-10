package com.someguyssoftware.gottschcore.block;

public interface IFacadeBlock extends INonStandardBlock, IDirectionalBlock {

  public VoxelShape[] getShapes();
  public void setShapes(VoxelShape[] shapes);
}
