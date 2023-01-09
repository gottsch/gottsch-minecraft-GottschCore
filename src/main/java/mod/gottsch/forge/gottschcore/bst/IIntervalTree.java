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

import java.util.List;

/**
 * 
 * @author Mark Gottschling on Sep 20, 2022
 *
 * @param <D>
 */
public interface IIntervalTree<D> {

	IInterval<D> insert(IInterval<D> interval);

	void clear();

	IInterval<D> delete(IInterval<D> target);

	List<IInterval<D>> getOverlapping(IInterval<D> interval, IInterval<D> testInterval, boolean findFast,
			boolean includeBorder);

}
