package mod.gottsch.forge.gottschcore.world.gen.structure;

import java.util.Map;

import com.someguyssoftware.gottschcore.measurement.Quantity;

public interface IDecayRuleSet {

	Quantity getVerticalDecayRate();

	void setVerticalDecayRate(Quantity verticalDecayRate);

	double getInitialBlockStrength();

	void setInitialBlockStrength(double initialBlockStrength);

	double getBlockStrengthThreshold();

	void setBlockStrengthThreshold(double blockStrengthThreshold);

	Quantity getBlockStrengthDecayRate();

	void setBlockStrengthDecayRate(Quantity blockStrengthDecayRate);

	Quantity getBlockStrengthDistanceDecayRate();

	void setBlockStrengthDistanceDecayRate(Quantity blockStrengthDistanceDecayRate);

	Map<String, DecayRule> getDecayRules();

	void setDecayRules(Map<String, DecayRule> decayRules);

	String getName();

	void setName(String name);

}