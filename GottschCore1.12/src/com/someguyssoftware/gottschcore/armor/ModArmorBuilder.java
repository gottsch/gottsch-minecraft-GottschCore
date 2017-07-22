/**
 * 
 */
package com.someguyssoftware.gottschcore.armor;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;

/**
 * A Builder to construct ModArmor.
 * @author Mark Gottschling on Jul 21, 2017
 *
 */
public class ModArmorBuilder {
	private String modID;
	private String name;
	private ArmorMaterial material;
	private int renderIndex;
	private EntityEquipmentSlot slot;
	private String texture;
	private Item repairItem;
	private CreativeTabs tab;
	
	/**
	 * 
	 */
	public ModArmorBuilder() {
		
	}
	
	/**
	 * 
	 * @return
	 */
	public ModArmor build() {
		// validate all the properties have been set.
		
		// create a new ModArmor object
		ModArmor armor = new ModArmor(getModID(), getName(),
				getMaterial(), getRenderIndex(), getSlot(),
				getTexture(), getRepairItem());

		armor.setCreativeTab(getCreativeTab());
		
		return armor;
	}
	
	/**
	 * clear all the properties of the builder
	 */
	public void clear() {
		withModID(null).withName(null).withMaterial(null)
		.withRenderIndex(0)
		.withRepairItem(null)
		.withSlot(null)
		.withTexture(null);
	}

	public CreativeTabs getCreativeTab() {
		return this.tab;
	}
	
	public ModArmorBuilder withCreativeTab(CreativeTabs tab) {		
		this.tab = tab;
		return this;
	}
	
	public String getModID() {
		return modID;
	}

	public ModArmorBuilder withModID(String modID) {
		this.modID = modID;
		return this;
	}

	public String getName() {
		return name;
	}

	public ModArmorBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public ArmorMaterial getMaterial() {
		return material;
	}

	public ModArmorBuilder withMaterial(ArmorMaterial material) {
		this.material = material;
		return this;
	}

	public int getRenderIndex() {
		return renderIndex;
	}

	public ModArmorBuilder withRenderIndex(int renderIndex) {
		this.renderIndex = renderIndex;
		return this;
	}

	public EntityEquipmentSlot getSlot() {
		return slot;
	}

	public ModArmorBuilder withSlot(EntityEquipmentSlot slot) {
		this.slot = slot;
		return this;
	}

	public String getTexture() {
		return texture;
	}

	public ModArmorBuilder withTexture(String texture) {
		this.texture = texture;
		return this;
	}

	public Item getRepairItem() {
		return repairItem;
	}

	public ModArmorBuilder withRepairItem(Item repairItem) {
		this.repairItem = repairItem;
		return this;
	}
}
