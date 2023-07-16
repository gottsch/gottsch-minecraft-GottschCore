package mod.gottsch.forge.gottschcore.world.gen.structure;

import java.util.Map;

import mod.gottsch.forge.gottschcore.size.DoubleRange;

public interface IDecayRuleSet {

	DoubleRange getVerticalDecayRate();

	void setVerticalDecayRate(DoubleRange verticalDecayRate);

	double getInitialBlockStrength();

	void setInitialBlockStrength(double initialBlockStrength);

	double getBlockStrengthThreshold();

	void setBlockStrengthThreshold(double blockStrengthThreshold);

	DoubleRange getBlockStrengthDecayRate();

	void setBlockStrengthDecayRate(DoubleRange blockStrengthDecayRate);

	DoubleRange getBlockStrengthDistanceDecayRate();

	void setBlockStrengthDistanceDecayRate(DoubleRange blockStrengthDistanceDecayRate);

	Map<String, DecayRule> getDecayRules();

	void setDecayRules(Map<String, DecayRule> decayRules);

	String getName();

	void setName(String name);

}