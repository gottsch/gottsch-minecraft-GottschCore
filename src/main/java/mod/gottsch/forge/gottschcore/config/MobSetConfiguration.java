/*
 * This file is part of  Treasure2.
 * Copyright (c) 2024 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.gottschcore.config;

import com.google.common.reflect.ClassPath;
import mod.gottsch.forge.gottschcore.size.IntegerRange;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Mark Gottschling on Aug 16, 2024
 *
 */
public class MobSetConfiguration {
	List<MobSet> mobSets;
	
	/*
	 * 
	 */
	public static class MobSet {
		private ResourceLocation name;
		private String category;
		private List<ResourceLocation> biomeWhitelist;
		private List<ResourceLocation> biomeBlacklist;
		private IntegerRange count;
		private List<WeightedMob> mobs;

		public ResourceLocation getName() {
			return name;
		}
		public void setName(ResourceLocation name) {
			this.name = name;
		}

		public List<ResourceLocation> getBiomeWhitelist() {
			if (biomeWhitelist == null) {
				biomeWhitelist = new ArrayList<>(1);
			}
			return biomeWhitelist;
		}
		public void setBiomeWhitelist(List<ResourceLocation> biomeWhitelist) {
			this.biomeWhitelist = biomeWhitelist;
		}
		public List<ResourceLocation> getBiomeBlacklist() {
			if (biomeBlacklist == null) {
				biomeBlacklist = new ArrayList<>(1);
			}
			return biomeBlacklist;
		}
		public void setBiomeBlacklist(List<ResourceLocation> biomeBlacklist) {
			this.biomeBlacklist = biomeBlacklist;
		}
		public IntegerRange getCount() {
			if (count == null) {
				count = new IntegerRange();
			}
			return count;
		}
		public void setCount(IntegerRange count) {
			this.count = count;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public List<WeightedMob> getMobs() {
			return mobs;
		}

		public void setMobs(List<WeightedMob> mobs) {
			this.mobs = mobs;
		}

		@Override
		public String toString() {
			return "MobSet{" +
					"name='" + name + '\'' +
					", category='" + category + '\'' +
					", biomeWhitelist=" + biomeWhitelist +
					", biomeBlacklist=" + biomeBlacklist +
					", count=" + count +
					", mobs=" + mobs +
					'}';
		}
	}

	public static class WeightedMob {
		private ResourceLocation name;
		private Integer weight;

		public WeightedMob() {
		}

		public ResourceLocation getName() {
			return name;
		}

		public void setName(ResourceLocation name) {
			this.name = name;
		}

		public Integer getWeight() {
			return weight;
		}

		public void setWeight(Integer weight) {
			this.weight = weight;
		}

		@Override
		public String toString() {
			return "WeightedMob{" +
					"name='" + name + '\'' +
					", weight=" + weight +
					'}';
		}
	}

	public List<MobSet> getMobSets() {
		return mobSets;
	}

	public void setMobSets(List<MobSet> mobSets) {
		this.mobSets = mobSets;
	}
}
