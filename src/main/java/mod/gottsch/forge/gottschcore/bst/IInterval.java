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

/**
 * 
 * @author Mark Gottschling on Sept 20, 2022
 *
 * @param <D>
 */
public interface IInterval<D> extends Comparable<IInterval<D>> {

	int getStart();
	int getEnd();

	Integer getMin();
	void setMin(Integer min);

	Integer getMax();
	void setMax(Integer max);

	IInterval<D> getLeft();
	void setLeft(IInterval<D> left);

	IInterval<D> getRight();
	void setRight(IInterval<D> right);

	D getData();
	void setData(D data);

	abstract int compareTo(IInterval<D> o);
}
