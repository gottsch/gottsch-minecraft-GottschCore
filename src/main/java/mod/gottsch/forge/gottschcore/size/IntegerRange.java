/*
 * This file is part of  Treasure2.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
 *
 * Treasure2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Treasure2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Treasure2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.gottschcore.size;

import java.util.Objects;

/**
 * A class to hold a minimum and maximum value for a value range.
 * 
 * @author Mark Gottschling on May 14, 2025
 *
 */
public class IntegerRange {
	private int min;
	private int max;

	/**
	 *
	 */
	public IntegerRange() {

	}

	/**
	 *
	 * @param q
	 */
	public IntegerRange(IntegerRange q) {
		setMin(q.getMin());
		setMax(q.getMax());
	}

	/**
	 * @param min
	 * @param max
	 */
	public IntegerRange(int min, int max) {
		super();
		this.min = min;
		this.max = max;
	}

	/**
	 * 
	 * @return
	 */
	public IntegerRange copy() {
		return new IntegerRange(this);
	}

	/**
	 * 
	 * @return
	 */
	public int getMinInt() {
		return (int) getMin();
	}

	/**
	 * 
	 * @return
	 */
	public int getMaxInt() {
		return (int) getMax();
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		IntegerRange that = (IntegerRange) o;
		return min == that.min && max == that.max;
	}

	@Override
	public int hashCode() {
		return Objects.hash(min, max);
	}

	@Override
	public String toString() {
		return "IntegerRange {" +
				"min=" + min +
				", max=" + max +
				'}';
	}
}
