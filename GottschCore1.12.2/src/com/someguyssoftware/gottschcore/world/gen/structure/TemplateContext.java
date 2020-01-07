/**
 * 
 */
package com.someguyssoftware.gottschcore.world.gen.structure;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.gen.structure.template.PlacementSettings;

/**
 * @author Mark Gottschling on Jan 7, 2020
 *
 */
public class TemplateContext {
	private PlacementSettings placement;
	private Block nullBlock;
	private Map<IBlockState, IBlockState> replacementMap;
	
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
	
	public Map<IBlockState, IBlockState> getReplacementMap() {
		return replacementMap;
	}
	public TemplateContext setReplacementMap(Map<IBlockState, IBlockState> replacementMap) {
		this.replacementMap = replacementMap;
		return this;
	}
}
