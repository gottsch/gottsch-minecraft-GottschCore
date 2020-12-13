/**
 * 
 */
package com.someguyssoftware.gottschcore.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * @author Mark Gottschling on Jul 22, 2017
 *
 */
public class ModPickaxeBuilder {
	private String modID;
	private String name;
	private Item.ToolMaterial material;
	private Item repairItem;
	private CreativeTabs tab;
	
	/**
	 * 
	 */
	public ModPickaxeBuilder() {}
	
	/**
	 * 
	 * @return
	 */
	public ModPickaxe build() {
		ModPickaxe item = new ModPickaxe(material, repairItem);
		item.setItemName(modID, name);
		item.setCreativeTab(tab);
		return item;
	}

	/**
	 * clear all the properties of the builder
	 */
	public void clear() {
		withModID(null)
		.withName(null)
		.withRepairItem(null)
		.withCreativeTab(null);
	}
		
	public String getModID() {
		return modID;
	}

	public ModPickaxeBuilder withModID(String modID) {
		this.modID = modID;
		return this;
	}

	public String getName() {
		return name;
	}

	public ModPickaxeBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public CreativeTabs getCreativeTab() {
		return tab;
	}

	public ModPickaxeBuilder withCreativeTab(CreativeTabs tab) {
		this.tab = tab;
		return this;
	}

	public Item getRepairItem() {
		return repairItem;
	}

	public ModPickaxeBuilder withRepairItem(Item repairItem) {
		this.repairItem = repairItem;
		return this;
	}

	public Item.ToolMaterial getMaterial() {
		return material;
	}

	public ModPickaxeBuilder withMaterial(Item.ToolMaterial material) {
		this.material = material;
		return this;
	}
}
