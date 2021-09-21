/*
 * This file is part of  GottschCore.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
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
package com.someguyssoftware.gottschcore.loot;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;

/**
 * @author Mark Gottschling on Dec 1, 2020
 *
 */
public class LootTableShell {
	// currently, this class is not serialized by other serializers so transient is safe to use for Gson
	private transient ResourceLocation resourceLocation;
	private String version;
	private String category;
	private List<String> categories;
	private String rarity;
	private List<LootPoolShell> pools;

	public LootTableShell() {}
	
	public List<LootPoolShell> getPools() {
		if (pools == null) {
			pools = new ArrayList<>();
		}
		return pools;
	}

	public void setPools(List<LootPoolShell> pools) {
		this.pools = pools;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<String> getCategories() {
		if (categories == null) {
			categories = new ArrayList<String>();
			if (category != null && !category.isEmpty()) {
				categories.add(category);
			}
		}
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	@Override
	public String toString() {
		return "LootTableShell [resourceLocation=" + resourceLocation + ", version=" + version + ", category="
				+ category + ", categories=" + categories + ", rarity=" + rarity + "]";
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public ResourceLocation getResourceLocation() {
		return resourceLocation;
	}

	public void setResourceLocation(ResourceLocation resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

	public String getRarity() {
		return rarity;
	}

	public void setRarity(String rarity) {
		this.rarity = rarity;
	}
}
