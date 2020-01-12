/**
 * 
 */
package com.someguyssoftware.gottschcore.world.gen.structure;

import java.util.List;
import java.util.Random;

import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.Template.BlockInfo;

/**
 * @author Mark Gottschling on Dec 8, 2019
 *
 */
public interface IDecayProcessor {

	void add(ICoords coords, BlockInfo blockInfo, IBlockState state);

	List<DecayBlockInfo> process(World world, Random random, ICoords size, Block nullBlock);

	public IDecayRuleSet getRuleSet();
}
