package com.someguyssoftware.gottschcore.block;

public interface INonStandardBlock {

  public VoxelShape getShape();
  
  public void setShape(VoxelShape shape);
}
