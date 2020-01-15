package com.someguyssoftware.gottschcore.block;

import net.minecraft.state.DirectionProperty;

/**
 * 
 * @author Mark Gottschling on Jan 11, 2020
 *
 */
public interface IRelativeDirectionalBlock extends IFacingBlock {

  public DirectionProperty getBase();
  public void setBase(DirectionProperty base);
  
}
