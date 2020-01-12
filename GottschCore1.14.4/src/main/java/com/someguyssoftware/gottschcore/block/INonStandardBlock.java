package com.someguyssoftware.gottschcore.block;

public interface INonStandardBlock extends IGottschCoreBlock {

  public VoxelShape getShape();
  
  public void setShape(VoxelShape shape);
}
