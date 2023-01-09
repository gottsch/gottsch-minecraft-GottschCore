/*
 * This file is part of  GottschCore.
 * Copyright (c) 2022 Mark Gottschling (gottsch)
 * 
 * All rights reserved.
 *
 * GottschCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GottschCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GottschCore.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.gottschcore.random;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

/**
 * 
 * @author Mark Gottschling
 *
 * @param <W>
 * @param <T>
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
