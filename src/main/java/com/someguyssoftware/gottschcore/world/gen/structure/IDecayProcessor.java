/**
 * 
 */
package com.someguyssoftware.gottschcore.world.gen.structure;

import java.util.List;
import java.util.Random;

import com.someguyssoftware.gottschcore.spatial.ICoords;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.template.Template.BlockInfo;

/**
 * @author Mark Gottschling on Dec 8, 2019
 *
 */
public interface IDecayProcessor {

	void add(ICoords coords, BlockInfo blockInfo, BlockState state);

	List<DecayBlockInfo> process(World world, Random random, ICoords size, Block nullBlock);

	public IDecayRuleSet getRuleSet();

	boolean isBackFill();

	void setBackFill(boolean backFill);

	BlockState getBackFillBlockLayer1();

	void setBackFillBlockLayer1(BlockState backFillBlockLayer1);

	BlockState getBackFillBlockLayer2();

	void setBackFillBlockLayer2(BlockState backFillBlockLayer2);

	int getDecayStartY();

	void setDecayStartY(int decayStartY);
}
