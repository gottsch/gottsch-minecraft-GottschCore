/**
 * 
 */
package com.someguyssoftware.gottschcore.world.gen.structure;

import java.util.HashMap;
import java.util.Map;

import com.someguyssoftware.gottschcore.measurement.Quantity;

/**
 * @author Mark Gottschling on Dec 19, 2019
 *
 */
public class DecayRuleSet implements IDecayRuleSet {
	private String name;
	private  Quantity verticalDecayRate;
	private double initialBlockStrength;
	private double blockStrengthThreshold;
	private Quantity blockStrengthDecayRate;
	private Quantity blockStrengthDistanceDecayRate;
	
	private Map<String, DecayRule> decayRules;
	
	public DecayRuleSet() {}

	@Override
	public Quantity getVerticalDecayRate() {
		return verticalDecayRate;
	}

	@Override
	public void setVerticalDecayRate(Quantity verticalDecayRate) {
		this.verticalDecayRate = verticalDecayRate;
	}

	@Override
	public double getInitialBlockStrength() {
		return initialBlockStrength;
	}

	@Override
	public void setInitialBlockStrength(double initialBlockStrength) {
		this.initialBlockStrength = initialBlockStrength;
	}

	@Override
	public double getBlockStrengthThreshold() {
		return blockStrengthThreshold;
	}

	@Override
	public void setBlockStrengthThreshold(double blockStrengthThreshold) {
		this.blockStrengthThreshold = blockStrengthThreshold;
	}

	@Override
	public Quantity getBlockStrengthDecayRate() {
		return blockStrengthDecayRate;
	}

	@Override
	public void setBlockStrengthDecayRate(Quantity blockStrengthDecayRate) {
		this.blockStrengthDecayRate = blockStrengthDecayRate;
	}

	@Override
	public Quantity getBlockStrengthDistanceDecayRate() {
		return blockStrengthDistanceDecayRate;
	}

	@Override
	public void setBlockStrengthDistanceDecayRate(Quantity blockStrengthDistanceDecayRate) {
		this.blockStrengthDistanceDecayRate = blockStrengthDistanceDecayRate;
	}

	@Override
	public Map<String, DecayRule> getDecayRules() {
		if (decayRules == null) {
			decayRules = new HashMap<>();
		}
		return decayRules;
	}

	@Override
	public void setDecayRules(Map<String, DecayRule> decayRules) {
		this.decayRules = decayRules;
	}

	@Override
	public String toString() {
		return "DecayRuleSet [name=" + name + ", verticalDecayRate=" + verticalDecayRate + ", initialBlockStrength="
				+ initialBlockStrength + ", blockStrengthThreshold=" + blockStrengthThreshold
				+ ", blockStrengthDecayRate=" + blockStrengthDecayRate + ", blockStrengthDistanceDecayRate="
				+ blockStrengthDistanceDecayRate + ", decayRules=" + decayRules + "]";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
}
