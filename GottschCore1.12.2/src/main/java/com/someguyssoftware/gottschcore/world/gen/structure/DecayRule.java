/**
 * 
 */
package com.someguyssoftware.gottschcore.world.gen.structure;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mark Gottschling on Dec 2, 2019
 *
 */
public class DecayRule {
	private String name;
	private int decayIterations;
	private double decayProbability;
	private List<String> decayBlocks;

	public DecayRule() {}

	public int getDecayIterations() {
		return decayIterations;
	}

	public void setDecayIterations(int decayIterations) {
		this.decayIterations = decayIterations;
	}

	public double getDecayProbability() {
		return decayProbability;
	}

	public void setDecayProbability(double decayProbability) {
		this.decayProbability = decayProbability;
	}

	public List<String> getDecayBlocks() {
		if (decayBlocks == null) {
			decayBlocks = new ArrayList<>();
		}
		return decayBlocks;
	}

	public void setDecayBlocks(List<String> decayBlocks) {
		this.decayBlocks = decayBlocks;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
