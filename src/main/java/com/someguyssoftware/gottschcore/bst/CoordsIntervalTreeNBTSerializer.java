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
package com.someguyssoftware.gottschcore.bst;

import java.util.function.Supplier;

import com.someguyssoftware.gottschcore.spatial.ICoords;
import com.someguyssoftware.gottschcore.world.WorldInfo;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * 
 * @author Mark Gottschling on Sep 20, 2022
 *
 */
public class CoordsIntervalTreeNBTSerializer<D extends INBTSerializable<INBT>> {
	private static final String START_KEY = "coords1";
	private static final String END_KEY = "coords2";
	private static final String LEFT_KEY = "left";
	private static final String RIGHT_KEY = "right";
	private static final String MIN_KEY = "min";
	private static final String MAX_KEY = "max";
	private static final String DATA_KEY = "data";
	
	private Supplier<D> dataSupplier;
	
	public CoordsIntervalTreeNBTSerializer(Supplier<D> dataSupplier) {
		this.dataSupplier = dataSupplier;
	}
	
	// TODO save yourself into this nbt
	public void save(CoordsIntervalTree<D> tree, CompoundNBT nbt, String tagName) {
		if (tree.getRoot() == null) {
			return;
		}		
		CompoundNBT rootNbt = new CompoundNBT();
		save((CoordsInterval<D>)tree.getRoot(), rootNbt);
		nbt.put(tagName, rootNbt);
	}
	
	/**
	 * 
	 * @param interval
	 * @param nbt
	 */
	private void save(CoordsInterval<D> interval, CompoundNBT nbt) {	
		CompoundNBT coordsNbt1 = new CompoundNBT();
		CompoundNBT coordsNbt2 = new CompoundNBT();
		
		interval.getCoords1().save(coordsNbt1);
		interval.getCoords2().save(coordsNbt2);
		
		nbt.put(START_KEY, coordsNbt1);
		nbt.put(END_KEY, coordsNbt2);

		nbt.putInt(MIN_KEY, interval.getMin());
		nbt.putInt(MAX_KEY, interval.getMax());
		
		if (interval.getData() != null) {
			INBT dataNbt =interval.getData().serializeNBT();
			nbt.put(DATA_KEY, dataNbt);
		}
		
		if (interval.getLeft() != null) {
			CompoundNBT left = new CompoundNBT();
			save((CoordsInterval<D>)interval.getLeft(), left);
			nbt.put(LEFT_KEY, left);
		}

		if (interval.getRight() != null) {
			CompoundNBT right = new CompoundNBT();
			save((CoordsInterval<D>)interval.getRight(), right);
			nbt.put(RIGHT_KEY, right);
		}
	}
	
	/**
	 * 
	 * @param nbt
	 * @param tagName
	 * @return
	 */
	public synchronized CoordsIntervalTree<D> load(CompoundNBT nbt, String tagName) {
		CoordsIntervalTree<D> tree = new CoordsIntervalTree<>();
		
		CompoundNBT rootNbt = nbt.getCompound(tagName);
		CoordsInterval<D> root = load(rootNbt);
		if (root != null && !root.isEmpty()) {
			tree.setRoot(root);
		}
		return tree;
	}
	
	/**
	 * 
	 * @param nbt
	 * @return
	 */
	private synchronized CoordsInterval<D> load(CompoundNBT nbt) {
		CoordsInterval<D> interval = new CoordsInterval<D>();
		
		ICoords c1 = CoordsInterval.EMPTY.getCoords1();
		ICoords c2 = CoordsInterval.EMPTY.getCoords2();
		if (nbt.contains(START_KEY)) {
			c1 = WorldInfo.EMPTY_COORDS.load(nbt.getCompound(START_KEY));
		}
		if (nbt.contains(END_KEY)) {
			c2 = WorldInfo.EMPTY_COORDS.load(nbt.getCompound(END_KEY));
		}
		interval.setCoords1(c1);
		interval.setCoords2(c2);
		
		if (nbt.contains(MIN_KEY)) {
			interval.setMin(nbt.getInt(MIN_KEY));
		}
		if (nbt.contains(MAX_KEY)) {
			interval.setMax(nbt.getInt(MAX_KEY));
		}
		
		if (nbt.contains(DATA_KEY) && dataSupplier != null) {
			D data = getDataSupplier().get();
			data.deserializeNBT(nbt);
			interval.setData(data);
		}
		
		if (nbt.contains(LEFT_KEY)) {
			CoordsInterval<D> left = load((CompoundNBT) nbt.get(LEFT_KEY));
			if (!left.isEmpty()) {
				interval.setLeft(left);
			}
		}
		
		if (nbt.contains(RIGHT_KEY)) {
			CoordsInterval<D> right = load((CompoundNBT) nbt.get(RIGHT_KEY));
			if (!right.isEmpty()) {
				interval.setLeft(right);
			}
			else {
				interval.setRight(right);
			}			
		}		
		return interval;
	}

	public Supplier<D> getDataSupplier() {
		return dataSupplier;
	}

}
