package com.someguyssoftware.gottschcore.block;

public interface IDirectionalBlock extends IGottschCoreBlock {
  
  public PropertyEnum getFacing();
  public void setFacing(PropertyEnum facing);
}
