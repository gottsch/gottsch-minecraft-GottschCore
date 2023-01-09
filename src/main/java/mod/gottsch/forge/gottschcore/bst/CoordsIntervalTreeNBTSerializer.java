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

import java.util.function.Supplier;

import mod.gottsch.forge.gottschcore.spatial.Coords;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * 
 * @author Mark Gottschling on Sep 20, 2022
 *
 */
public class CoordsIntervalTreeNBTSerializer<D extends INBTSerializable<Tag>> {
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
	
	public void save(CoordsIntervalTree<D> tree, CompoundTag tag, String tagName) {
		if (tree.getRoot() == null) {
			return;
		}		
		CompoundTag rootNbt = new CompoundTag();
		save((CoordsInterval<D>)tree.getRoot(), rootNbt);
		tag.put(tagName, rootNbt);
	}
	
	/**
	 * 
	 * @param interval
	 * @param tag
	 */
	private void save(CoordsInterval<D> interval, CompoundTag tag) {	
		CompoundTag coordsNbt1 = new CompoundTag();
		CompoundTag coordsNbt2 = new CompoundTag();
		
		interval.getCoords1().save(coordsNbt1);
		interval.getCoords2().save(coordsNbt2);
		
		tag.put(START_KEY, coordsNbt1);
		tag.put(END_KEY, coordsNbt2);

		tag.putInt(MIN_KEY, interval.getMin());
		tag.putInt(MAX_KEY, interval.getMax());
		
		if (interval.getData() != null) {
			Tag dataNbt =interval.getData().serializeNBT();
			tag.put(DATA_KEY, dataNbt);
		}
		
		if (interval.getLeft() != null) {
			CompoundTag left = new CompoundTag();
			save((CoordsInterval<D>)interval.getLeft(), left);
			tag.put(LEFT_KEY, left);
		}

		if (interval.getRight() != null) {
			CompoundTag right = new CompoundTag();
			save((CoordsInterval<D>)interval.getRight(), right);
			tag.put(RIGHT_KEY, right);
		}
	}
	
	/**
	 * 
	 * @param tag
	 * @param tagName
	 * @return
	 */
	public synchronized CoordsIntervalTree<D> load(CompoundTag tag, String tagName) {
		CoordsIntervalTree<D> tree = new CoordsIntervalTree<>();
		
		CompoundTag rootNbt = tag.getCompound(tagName);
		CoordsInterval<D> root = load(rootNbt);
		if (root != null && !root.isEmpty()) {
			tree.setRoot(root);
		}
		return tree;
	}
	
	/**
	 * 
	 * @param tag
	 * @return
	 */
	private synchronized CoordsInterval<D> load(CompoundTag tag) {
		CoordsInterval<D> interval = new CoordsInterval<D>();
		
		ICoords c1 = CoordsInterval.EMPTY.getCoords1();
		ICoords c2 = CoordsInterval.EMPTY.getCoords2();
		if (tag.contains(START_KEY)) {
			c1 = Coords.EMPTY.load(tag.getCompound(START_KEY));
		}
		if (tag.contains(END_KEY)) {
			c2 = Coords.EMPTY.load(tag.getCompound(END_KEY));
		}
		interval.setCoords1(c1);
		interval.setCoords2(c2);
		
		if (tag.contains(MIN_KEY)) {
			interval.setMin(tag.getInt(MIN_KEY));
		}
		if (tag.contains(MAX_KEY)) {
			interval.setMax(tag.getInt(MAX_KEY));
		}
		
		if (tag.contains(DATA_KEY) && dataSupplier != null) {
			D data = getDataSupplier().get();
			data.deserializeNBT(tag);
			interval.setData(data);
		}
		
		if (tag.contains(LEFT_KEY)) {
			CoordsInterval<D> left = load((CompoundTag) tag.get(LEFT_KEY));
			if (!left.isEmpty()) {
				interval.setLeft(left);
			}
		}
		
		if (tag.contains(RIGHT_KEY)) {
			CoordsInterval<D> right = load((CompoundTag) tag.get(RIGHT_KEY));
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
