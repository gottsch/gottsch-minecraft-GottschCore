/**
 * 
 */
package com.someguyssoftware.gottschcore.world.gen.structure;

import java.util.Map;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;


/**
 * @author Mark Gottschling on Jan 7, 2020
 *
 */
public class TemplateContext {
	private PlacementSettings placement;
	private Block nullBlock;
	private Map<BlockState, BlockState> replacementMap;
	
	public TemplateContext() {
		
	}
	
	public PlacementSettings getPlacement() {
		return placement;
	}
	public TemplateContext setPlacement(PlacementSettings placement) {
		this.placement = placement;
		return this;
	}
	
	public Block getNullBlock() {
		return nullBlock;
	}
	public TemplateContext setNullBlock(Block nullBlock) {
		this.nullBlock = nullBlock;
		return this;
	}
	
	public Map<BlockState, BlockState> getReplacementMap() {
		return replacementMap;
	}
	public TemplateContext setReplacementMap(Map<BlockState, BlockState> replacementMap) {
		this.replacementMap = replacementMap;
		return this;
	}
}
