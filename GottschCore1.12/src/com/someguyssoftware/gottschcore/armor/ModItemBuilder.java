/**
 * 
 */
package com.someguyssoftware.gottschcore.armor;

import net.minecraft.creativetab.CreativeTabs;

/**
 * @author Mark Gottschling on Jul 22, 2017
 *
 */
public class ModItemBuilder {
	private String modID;
	private String name;
	private int stackSize;
	private CreativeTabs tab;
	
	/**
	 * 
	 */
	public ModItemBuilder() {}
	
	/**
	 * 
	 * @return
	 */
	public ModItem build() {
		ModItem item = new ModItem();
		item.setMaxStackSize(stackSize)
		.setCreativeTab(tab);
		item.setItemName(modID, name);
		return item;
	}

	/**
	 * clear all the properties of the builder
	 */
	public void clear() {
		withModID(null)
		.withName(null)
		.withStackSize(0)
		.withTab(null);
	}
		
	public String getModID() {
		return modID;
	}

	public ModItemBuilder withModID(String modID) {
		this.modID = modID;
		return this;
	}

	public String getName() {
		return name;
	}

	public ModItemBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public int getStackSize() {
		return stackSize;
	}

	public ModItemBuilder withStackSize(int stackSize) {
		this.stackSize = stackSize;
		return this;
	}

	public CreativeTabs getTab() {
		return tab;
	}

	public ModItemBuilder withTab(CreativeTabs tab) {
		this.tab = tab;
		return this;
	}
}
