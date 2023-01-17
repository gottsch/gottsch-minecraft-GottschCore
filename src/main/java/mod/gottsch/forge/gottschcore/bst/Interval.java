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
package mod.gottsch.forge.gottschcore.bst;

import mod.gottsch.forge.gottschcore.GottschCore;

/**
 * 
 * @author Mark Gottschling on Jul 26, 2022
 *
 * @param <D>
 */
public class Interval<D> implements IInterval<D> {
	
	public static final Interval<?> EMPTY = new Interval<>(null, null, null);
	
	private Integer start;
	private Integer end;
	private Integer min;
	private Integer max;
	private IInterval<D> left;
	private IInterval<D> right;

	// extra mod specific data
	private D data;

	/**
	 * Empty constructor
	 */
	public Interval() {
		start = EMPTY.getStart();
		end = EMPTY.getEnd();
	}
	
	/**
	 * 
	 * @param start
	 * @param end
	 */
	public Interval(Integer start, Integer end) {
		this.start = start;
		this.end = end;
		this.min = start < end ? start : end;
		this.max = start > end ? start : end;
	}

	/**
	 * 
	 * @param coords1
	 * @param coords2
	 * @param data
	 */
	public Interval(Integer start, Integer end, D data) {
		this(start, end);
		this.data = data;
	}

	
	@Override
	public int compareTo(IInterval<D> interval) {
		if (getStart() < interval.getStart()) {
			return -1;
		} else if (getStart() == interval.getStart()) {
			if (getEnd() == interval.getEnd()) {
				return 0;
			}
			GottschCore.LOGGER.debug("this.end -> {}, interval.end -> {}", this.getEnd(), interval.getEnd());
			return this.getEnd() < interval.getEnd() ? -1 : 1;
		} else {
			GottschCore.LOGGER.debug("this.end -> {}, interval.end -> {}", this.getEnd(), interval.getEnd());
			return 1;
		}
	}

	@Override
	public int getStart() {
		return start;
	}

	@Override
	public int getEnd() {
		return end;
	}

	@Override
	public Integer getMin() {
		return min;
	}

	@Override
	public void setMin(Integer min) {
		this.min = min;
	}
	
	@Override
	public Integer getMax() {
		return max;
	}

	@Override
	public void setMax(Integer max) {
		this.max = max;
	}
	@Override
	public IInterval<D> getLeft() {
		return left;
	}
	@Override
	public void setLeft(IInterval<D> left) {
		this.left = left;
	}
	@Override
	public IInterval<D> getRight() {
		return right;
	}
	@Override
	public void setRight(IInterval<D> right) {
		this.right = right;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Interval<?> other = (Interval<?>) obj;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		return true;
	}

	@Override
	public D getData() {
		return data;
	}
	@Override
	public void setData(D data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Interval [start=" + start + ", end=" + end + ", min=" + min + ", max=" + max + ", left=" + left
				+ ", right=" + right + ", data=" + data + "]";
	}
	
}
