/**
 * 
 */
package com.someguyssoftware.gottschcore.random;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

/**
 * Similar to RandomProbabilityCollection, but removes the requirement
 *  that collection items implement the IRandomProbabilityItem interface.
 * @author Mark Gottschling on Jan 21, 2018
 *
 */
public class WeightedCollection<W extends Number, T> {
	private final NavigableMap<Double, T> map = new TreeMap<Double, T>();
	private Random random;
	private double total = 0;
	
	/**
	 * 
	 */
	public WeightedCollection() {
		setRandom(new Random());
	}
	
	/**
	 * 
	 * @param random
	 */
	public WeightedCollection(Random random) {
		setRandom(random);
	}
	
	/**
	 * Add an item with a given weight.
	 * @param weight
	 * @param item
	 */
	public WeightedCollection<W, T> add(W weight, T item) {
		if (weight.doubleValue() > 0) {
			total += weight.doubleValue();
			map.put(total, item);
		}
		return this;
	}
	
	/**
	 * Get the next random value.
	 * @return
	 */
	public T next() {
		if (map.isEmpty() || total < 1) return null;
		double value = getRandom().nextDouble(total);
		return map.ceilingEntry(value).getValue();
	}
	
	/**
	 * 
	 */
	public void clear() {
		map.clear();
		setTotal(0);
	}
	
	/**
	 * 
	 * @return
	 */
	public int size() {
		if (map == null) return 0;
		return map.size();
	}
	
	/**
	 * @return the random
	 */
	public Random getRandom() {
		return random;
	}

	/**
	 * @param random the random to set
	 */
	public void setRandom(Random random) {
		this.random = random;
	}
	
	/**
	 * 
	 * @param total
	 */
	private void setTotal(int total) {
		this.total = total;
	}
	
	public Map<Double, T> getMap() {
		return map;
	}
}
