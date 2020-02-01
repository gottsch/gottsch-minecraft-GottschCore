/**
 * 
 */
package com.someguyssoftware.gottschcore.world.gen.structure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.mod.IMod;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;
import com.someguyssoftware.gottschcore.property.PropertyHelper;
import com.someguyssoftware.gottschcore.random.RandomHelper;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * @author Mark Gottschling on Dec 8, 2019
 *
 */
public class DecayProcessor implements IDecayProcessor {
	private static int AIR = -9999;
	private static int UNKNOWN = 9999;

	private static String DEFAULT_DECAY_RULE_NAME = "default";
	private static int NO_DECAY_INDEX = -1;
	private static String NULL_BLOCK_NAME = "null";


	private List<DecayBlockInfo> decayBlockInfoList;
	/** map of blocks in the structure */
	private int[][][] layout;
	/** the decay rule set */
	private IDecayRuleSet ruleSet;
	private IMod mod;
	
	private boolean backFill = true;
	private IBlockState backFillBlockLayer1 = Blocks.DIRT.getDefaultState();
	private IBlockState backFillBlockLayer2 = Blocks.STONE.getDefaultState();
	
	private int decayStartY = 0;

	// TODO need to know what the null block is
	public DecayProcessor(IMod mod, IDecayRuleSet ruleSet) {
		decayBlockInfoList = new ArrayList<>();
		this.mod = mod;
		this.ruleSet = ruleSet;
	}

	@Override
	public void add(ICoords coords, GottschTemplate.BlockInfo blockInfo, IBlockState state) {
		getDecayBlockInfoList().add(new DecayBlockInfo(blockInfo, coords, state));
	}

	@Override
	public List<DecayBlockInfo> process(final World world, final Random random, ICoords size, final Block NULL_BLOCK) {
		Comparator<DecayBlockInfo> compareByCoords = Comparator	
				.comparing(DecayBlockInfo::getY)
				.thenComparing(DecayBlockInfo::getZ)
				.thenComparing(DecayBlockInfo::getX);

		Collections.sort(decayBlockInfoList, compareByCoords);

		// intialize the array with the size of the template
		layout = new int[size.getY()][size.getZ()][ size.getX()];

		// determine the offsets
		ICoords offsetCoords = decayBlockInfoList.get(0).getCoords();

		// set the initial strength
		double initialStrength = getRuleSet().getInitialBlockStrength();

		// initialize
		int minDistance = 0;
		int distance1 = 0;
		int distance2 = 0;

		// pass 1
		int decayBlockInfoListIndex = 0;
		for(DecayBlockInfo decay : decayBlockInfoList) {		
			// load array from list
			int x = decay.getX() - offsetCoords.getX();
			int y = decay.getY() - offsetCoords.getY();
			int z = decay.getZ() - offsetCoords.getZ();

			// update the layout matrix with the index to the decy blockinfo list
			layout[y][z][x] = decayBlockInfoListIndex;
			decayBlockInfoListIndex++;

			if (decay.getState().getBlock() == NULL_BLOCK) {
				continue;
			}

			
			IBlockState supportState = null;
			if (y == 0) {
				// determine if back filling needs to occur
				if (isBackFill()) {
					ICoords fillCoords = decay.getCoords().down(1);
					IBlockState fillState = world.getBlockState(fillCoords.toPos());
					int depth = 1;
					while (!fillState.getMaterial().isSolid()) {
						// fill the space with a block
						world.setBlockState(fillCoords.toPos(), (depth > 3) ? Blocks.STONE.getDefaultState() : Blocks.DIRT.getDefaultState());
						// move down
						fillCoords = fillCoords.down(1);
						fillState = world.getBlockState(fillCoords.toPos());
						depth++;
					}
				}
				// get the "support" blockstate
				supportState = world.getBlockState(decay.getCoords().toPos().down());
			}
			else {
				supportState = decayBlockInfoList.get(layout[y-1][z][x]).getState();
			}

			// get the rule for the current block
			String blockRegistryName = null;
			String ruleKey = null;
			DecayRule decayRule = null;
			
			if (decay.getState().getBlock() != Blocks.AIR) {
				ruleKey = decay.getState().getBlock().getRegistryName().toString();
				
				// check if the block is a *special* block that is registered in the DecayRuleKeyRegistry
				if (DecayRuleKeyRegistry.getInstance().has(ruleKey)) {
					ruleKey = DecayRuleKeyRegistry.getInstance().get(ruleKey, String.valueOf(decay.getState().getBlock().getMetaFromState(decay.getState())));
//					GottschCore.logger.debug("block -> {} has a registry key -> {} in the DecayRuleKeyRegistry.", decay.getState().getBlock().getRegistryName().toString(), ruleKey);
				}
//				GottschCore.logger.debug("ruleKey -> {}", ruleKey);	
				decayRule = ruleSet.getDecayRules().get(ruleKey);
				if (decayRule == null) {
					decayRule = ruleSet.getDecayRules().get(DEFAULT_DECAY_RULE_NAME);
				}

				if (decayRule != null) {
					int decayIndex = getDecayIndex(random, decayRule);
					// decay the block before any other tests (because it might turn to air)
					IBlockState decayState = applyDecay(decay.getState(), decayRule, decayIndex, NULL_BLOCK);
					decay.setState(decayState);
					decay.setDecayIndex(decayIndex);
				}
			}
			
			// determine if block is a wall/supported from below
			boolean isWall = decay.getY() >=0 &&
					decay.getState().getMaterial().isSolid() &&
					supportState.getMaterial().isSolid();
			decay.setWall(isWall);	

			// determine distance (from N and W)
			minDistance = 0;
			if (isWall) {
				decay.setDistance(0);
			}
			else if (decay.getState().getBlock() == Blocks.AIR){
				minDistance = AIR;
			}
			else {
				if (x == 0 && z == 0) {
					minDistance = UNKNOWN;
				}
				else if (z ==0) {
					minDistance = decayBlockInfoList.get(layout[y][z][x-1]).getDistance();
					if (minDistance == AIR) minDistance = UNKNOWN;
				}
				else if (x == 0) {
					minDistance = decayBlockInfoList.get(layout[y][z-1][x]).getDistance();
					if (minDistance == AIR) minDistance = UNKNOWN;
				}
				else {
					distance1 = decayBlockInfoList.get(layout[y][z][x-1]).getDistance();
					if (distance1 == AIR) distance1 = UNKNOWN;
					distance2 = decayBlockInfoList.get(layout[y][z-1][x]).getDistance();
					if (distance2 == AIR) distance2 = UNKNOWN;
					minDistance = Math.min(distance1, distance2) + 1;		

				}
				decayBlockInfoList.get(layout[y][z][x]).setDistance(minDistance);
			}
		}

		if (GottschCore.logger.isDebugEnabled()) {
			dump("-pass1", size);
		}

		// pass 2
		double threshold = getRuleSet().getBlockStrengthThreshold();
		double yStrength = initialStrength;
		DecayBlockInfo decay = null;
		for (int y = 0; y < layout.length; y++) { // bottom up
			for (int z = layout[y].length-1; z >=0; z--) { // backwards z
				for (int x = layout[y][z].length-1; x >= 0; x--) { // backwards x
					minDistance = 0;
					distance1 = 0;
					distance2 = 0;

					// get the decay block
					decay = decayBlockInfoList.get(layout[y][z][x]);

					if (decay.getState().getBlock() == NULL_BLOCK) {
						continue;
					}

					// update the strength with vertical decay
					if (y >= getDecayStartY()) {
						yStrength = initialStrength - ((y - getDecayStartY()) * 
								RandomHelper.randomDouble(random, 
										getRuleSet().getVerticalDecayRate().getMin(), 
										getRuleSet().getVerticalDecayRate().getMax()));
						
						if (y > 0) {
							yStrength = Math.min(yStrength, decayBlockInfoList.get(layout[y-1][z][x]).getStrength());
						}
						decay.setStrength(yStrength);
					}
					
					// perform tests
					if (decay.isWall()) {
						decay.setDistance(0);
					}
					else if (decay.getDistance() == AIR) {
						continue;
					}
					else {
						// calculate the distance
						if (x == layout[y][z].length-1 && z == layout[y].length-1) {} // skip
						else if (z == layout[y].length-1) {
							minDistance = decayBlockInfoList.get(layout[y][z][x+1]).getDistance();
							if (minDistance == AIR) minDistance = UNKNOWN;
						}
						else if (x == layout[y][z].length-1) {
							minDistance = decayBlockInfoList.get(layout[y][z+1][x]).getDistance();
							if (minDistance == AIR) minDistance = UNKNOWN;
						}
						else {
							distance1 = decayBlockInfoList.get(layout[y][z][x+1]).getDistance();
							if (distance1 == AIR) distance1 = UNKNOWN;
							distance2 = decayBlockInfoList.get(layout[y][z+1][x]).getDistance();
							if (distance2 == AIR) distance2 = UNKNOWN;
						}
						minDistance = Math.min(decay.getDistance(), Math.min(distance1, distance2) + 1);
						decay.setDistance(minDistance);

						double strength = yStrength - (decay.getDistance() * 
								RandomHelper.randomDouble(random, 
										getRuleSet().getBlockStrengthDistanceDecayRate().getMin(), 
										getRuleSet().getBlockStrengthDistanceDecayRate().getMax()));
						if (decay.getDecayIndex() > NO_DECAY_INDEX) {
							double weakenAmount = decay.getDecayIndex() * 
									RandomHelper.randomDouble(random, 
											getRuleSet().getBlockStrengthDecayRate().getMin(),
											getRuleSet().getBlockStrengthDecayRate().getMax());

							// reduce 
							strength = strength - weakenAmount;
							// update the strength of the blocks around this block
							updateNeighbors(y, z, x, weakenAmount);
						}
						decay.setStrength(strength);
					}
					// test the strength
					if (decay.getStrength() <= threshold) {
						decay.setState(Blocks.AIR.getDefaultState());
					}

					// test behind (x-1 and z-1) for strength <= threshold
					if (z + 1 < layout[y].length && decayBlockInfoList.get(layout[y][z+1][x]).getState().getBlock() != Blocks.AIR
							&& decayBlockInfoList.get(layout[y][z+1][x]).getState().getBlock() != NULL_BLOCK) {
						if (decayBlockInfoList.get(layout[y][z+1][x]).getStrength() <= threshold) {
							decayBlockInfoList.get(layout[y][z+1][x]).setState(Blocks.AIR.getDefaultState());
						}
					}
					if (x + 1 < layout[y][z].length && decayBlockInfoList.get(layout[y][z][x+1]).getState().getBlock() != Blocks.AIR
							&& decayBlockInfoList.get(layout[y][z][x+1]).getState().getBlock() != NULL_BLOCK) {
						if (decayBlockInfoList.get(layout[y][z][x+1]).getStrength() <= threshold) {
							decayBlockInfoList.get(layout[y][z][x+1]).setState(Blocks.AIR.getDefaultState());
						}
					}
				}
			}
		}
		
		if (GottschCore.logger.isDebugEnabled()) {
			dump("-pass2", size);
		}
		
		// pass3: go forward again and check for neighbors
		for (int y = 0; y < layout.length; y++) { // bottom up
			for (int z = 0; z < layout[y].length; z++) {
				for (int x = 0; x < layout[y][z].length; x++) {
					// get the decay block
					decay = decayBlockInfoList.get(layout[y][z][x]);

					if (decay.getState().getBlock() == Blocks.AIR) {
						continue;
					}
					if (decay.getState().getBlock() == NULL_BLOCK) {
						continue;
					}
					if (!checkForNeighbors(world, y, z, x)) {
						decay.setState(Blocks.AIR.getDefaultState());
					}	
				}
			}
		}

		return decayBlockInfoList;
	}

	/**
	 * 
	 * @param y
	 * @param i
	 * @param x
	 * @return
	 */
	private boolean checkForNeighbors(World world, int y, int z, int x) {
		IBlockState supportState = null;
		if (y == 0) supportState = world.getBlockState(decayBlockInfoList.get(layout[y][z][x]).getCoords().toPos().down());
		else supportState = decayBlockInfoList.get(layout[y-1][z][x]).getState();

		if (supportState.getMaterial().isSolid()) return true;
		if (z -1 > 0 && decayBlockInfoList.get(layout[y][z-1][x]).getState().getMaterial().isSolid()) return true;
		if (z + 1 < layout[y].length && decayBlockInfoList.get(layout[y][z+1][x]).getState().getMaterial().isSolid()) return true;
		if (x -1 > 0 && decayBlockInfoList.get(layout[y][z][x-1]).getState().getMaterial().isSolid()) return true;
		if (x + 1 < layout[y][z].length && decayBlockInfoList.get(layout[y][z][x+1]).getState().getMaterial().isSolid()) return true;		
		return false;
	}

	/**
	 * 
	 * @param y
	 * @param z
	 * @param x
	 * @param weakenAmount
	 */
	private void updateNeighbors(int y, int z, int x, double weakenAmount) {
		if (z -1 > 0 && !decayBlockInfoList.get(layout[y][z-1][x]).isWall()) {
			weakenStrength(decayBlockInfoList.get(layout[y][z-1][x]), weakenAmount);
		}
		if (z + 1 < layout[y].length && !decayBlockInfoList.get(layout[y][z+1][x]).isWall()) {
			weakenStrength(decayBlockInfoList.get(layout[y][z+1][x]), weakenAmount);
		}
		if (x -1 > 0 && !decayBlockInfoList.get(layout[y][z][x-1]).isWall()) {
			weakenStrength(decayBlockInfoList.get(layout[y][z][x-1]), weakenAmount);
		}
		if (x + 1 < layout[y][z].length && !decayBlockInfoList.get(layout[y][z][x+1]).isWall()) {
			weakenStrength(decayBlockInfoList.get(layout[y][z][x+1]), weakenAmount);
		}		
	}

	/**
	 * 
	 * @param decay
	 * @param weakenAmount
	 */
	private void weakenStrength(DecayBlockInfo decay, double weakenAmount) {
		double strength = decay.getStrength();
		strength = strength - weakenAmount;
		decay.setStrength(strength);
	}

	/**
	 * 
	 * @param filenamePostfix
	 * @param size
	 */
	private void dump(String filenamePostfix, ICoords size) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmssZ");
		DecimalFormat df = new DecimalFormat("###.#");

		String filename = String.format("decay-prcoessor-%s-%s.txt", filenamePostfix, formatter.format(new Date()));

		Path path = Paths.get(mod.getConfig().getConfigFolder(), mod.getId(), "dumps").toAbsolutePath();
		try {
			Files.createDirectories(path);
		} catch (IOException e) {
			GottschCore.logger.error("Couldn't create directories for dump files:", e);
			return;
		}

		// setup a divider line
		char[] chars = new char[75];
		Arrays.fill(chars, '*');
		String div = new String(chars) + "\n";
		StringBuilder sb = new StringBuilder();

		String heading = "**  %-67s  **\n";

		sb.append(div).append(String.format("**  %-67s  **\n", "DECAY PROCESSOR")).append(div)
		.append(String.format(heading, "Size: [y][z][x]")).append(div)
		.append(String.format("[%s][%s][%s]\n", size.getY(), size.getZ(), size.getX())).append(div)

		// dump list
		.append(String.format(heading, "Decay BlockInfo List (Sorted): Map[y][z][x] -> DBL[y][z][x]")).append(div);
		for (int y =0 ; y < layout.length; y++) {
			sb.append(String.format(heading, "Level " + y)).append(div);
			for (int z = 0; z < layout[y].length; z++) {
				sb.append(String.format(heading, "Row " + z)).append(div);
				for (int x = 0; x < layout[y][z].length; x++) {
					sb.append(String.format("Map[%s][%s][%s] -> DBL[%s][%s][%s]\n", 
							decayBlockInfoList.get(layout[y][z][x]).getCoords().getY(),
							decayBlockInfoList.get(layout[y][z][x]).getCoords().getZ(),
							decayBlockInfoList.get(layout[y][z][x]).getCoords().getX(),
							y, z, x));
				}
				sb.append(div);
			}
			sb.append("\n").append(div);
		}

		sb.append("\n").append(div)		
		.append(String.format(heading, "Wall / Non-wall [1 / 0]")).append(div);
		// dump wall
		for (int y =0 ; y < layout.length; y++) {
			sb.append(String.format(heading, "Level " + y)).append(div);
			for (int z = 0; z < layout[y].length; z++) {
				for (int x = 0; x < layout[y][z].length; x++) {
					sb.append(String.format("[%s]", (decayBlockInfoList.get(layout[y][z][x]).isWall()?1:0)));
				}
				sb.append("\n");
			}
			sb.append(div);
		}		
		sb.append("\n").append(div);

		// dump by block [B] or air [A]
		sb.append(String.format(heading, "Block / Air [B / A]")).append(div);
		for (int y =0 ; y < layout.length; y++) {
			sb.append(String.format(heading, "Level " + y));
			for (int z = 0; z < layout[y].length; z++) {
				for (int x = 0; x < layout[y][z].length; x++) {
					sb.append(String.format("[%s]", (decayBlockInfoList.get(layout[y][z][x]).getState().getBlock() == Blocks.AIR ? "A" : "B")));
				}
				sb.append("\n");
			}
			sb.append(div);
		}		
		sb.append("\n");

		// dump distance
		sb.append(String.format(heading, "Distance")).append(div);		
		for (int y =0 ; y < layout.length; y++) {
			sb.append(String.format(heading, "Level " + y));
			for (int z = 0; z < layout[y].length; z++) {
				for (int x = 0; x < layout[y][z].length; x++) {
					sb.append(String.format("[%s]", decayBlockInfoList.get(layout[y][z][x]).getDistance()));
				}
				sb.append("\n");
			}
			sb.append(div);
		}
		sb.append("\n");

		// dump strength
		sb.append(String.format(heading, "Strength")).append(div);		
		for (int y =0 ; y < layout.length; y++) {
			sb.append(String.format(heading, "Level " + y));
			for (int z = 0; z < layout[y].length; z++) {
				for (int x = 0; x < layout[y][z].length; x++) {
					sb.append(String.format("[%s]", df.format(decayBlockInfoList.get(layout[y][z][x]).getStrength())));
				}
				sb.append("\n");
			}
			sb.append(div);
		}

		try {
			GottschCore.logger.debug("dumping to path: {}", Paths.get(path.toString(), filename).toAbsolutePath());
			Files.write(Paths.get(path.toString(), filename), sb.toString().getBytes());
		} catch (IOException e) {
			GottschCore.logger.error("Error writing DecayProcessor to dump file", e);
		}
	}

	/**
	 * 
	 * @param arrangement
	 * @param style
	 * @param decayIndex
	 * @return
	 */
	public IBlockState applyDecay(IBlockState state, DecayRule decayRule, int decayIndex, final Block NULL_BLOCK) {
		String blockValue = "";
		IBlockState blockState = Blocks.AIR.getDefaultState();

		// ensure the decayIndex is right size for selected style
		decayIndex = (decayIndex < decayRule.getDecayBlocks().size()) ? decayIndex : decayRule.getDecayBlocks().size()-1;

		// get the block according to style and decay index
		blockValue = decayIndex > -1 ? decayRule.getDecayBlocks().get(decayIndex) : null;

		if (blockValue == null || blockValue == "") return state;

		// check for special case "null"
		if (blockValue.equals(NULL_BLOCK_NAME)) {
			return NULL_BLOCK.getDefaultState();
		}

		// get the block based on meta
		String[] blockAndMeta = blockValue.split("@");
		int meta = 0;
		// TODO could add additional checks here to ensure the string only contains 1 @ or that the value after @ is numeric
		if (blockAndMeta.length > 1) meta = Integer.valueOf(blockAndMeta[1]);
		try {
			blockState = Block.getBlockFromName(blockAndMeta[0]).getStateFromMeta(meta);
		}
		catch(Exception e) {
			blockState = null;
		}

		if (blockState == null) {
			GottschCore.logger.warn(String.format("Unable to retrieve blockState; returning original:\n" +
					"Decay Rule: %s\n" + 
					"Block: %s\n" +
					"blockAndMeta[0]: %s\n" +
					"Meta: %d", decayRule.getName(), blockValue, blockAndMeta[0], meta));
			return state;
		}

		blockState = PropertyHelper.copyProperties(state, blockState);
		return blockState;
	}

	/**
	 * @param decayMultiplier
	 * @param style
	 * @return
	 */
	private int getDecayIndex(Random random, DecayRule rule) {
		if (rule.getDecayIterations() <= 0) return -1;

		int decayIndex = -1;
		for (int i = 0; i < rule.getDecayIterations(); i++) {
			if (RandomHelper.checkProbability(random, rule.getDecayProbability())) {
				decayIndex++;
			}
		}
		return decayIndex;
	}


	public List<DecayBlockInfo> getDecayBlockInfoList() {
		return decayBlockInfoList;
	}

	protected void setDecayBlockInfoList(List<DecayBlockInfo> decayBlockInfoList) {
		this.decayBlockInfoList = decayBlockInfoList;
	}

	public int[][][] getLayout() {
		return layout;
	}

	protected void setLayout(int[][][] layout) {
		this.layout = layout;
	}

	public IDecayRuleSet getRuleSet() {
		return ruleSet;
	}

	public void setRuleSet(IDecayRuleSet ruleSet) {
		this.ruleSet = ruleSet;
	}

	@Override
	public boolean isBackFill() {
		return backFill;
	}

	@Override
	public void setBackFill(boolean backFill) {
		this.backFill = backFill;
	}

	@Override
	public IBlockState getBackFillBlockLayer1() {
		return backFillBlockLayer1;
	}

	@Override
	public void setBackFillBlockLayer1(IBlockState backFillBlockLayer1) {
		this.backFillBlockLayer1 = backFillBlockLayer1;
	}

	@Override
	public IBlockState getBackFillBlockLayer2() {
		return backFillBlockLayer2;
	}

	@Override
	public void setBackFillBlockLayer2(IBlockState backFillBlockLayer2) {
		this.backFillBlockLayer2 = backFillBlockLayer2;
	}

	@Override
	public int getDecayStartY() {
		return decayStartY;
	}

	@Override
	public void setDecayStartY(int decayStartY) {
		this.decayStartY = decayStartY;
	}
}
