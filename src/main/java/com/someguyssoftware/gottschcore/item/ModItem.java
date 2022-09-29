/**
 * 
 */
package com.someguyssoftware.gottschcore.item;

import net.minecraft.item.Item;

/**
 * @author Mark Gottschling on Jul 22, 2017
 *
 */
public class ModItem extends Item {

	public ModItem(Item.Properties properties) {
		super(properties);
	}
	
	/**
	 * 
	 */
	@Deprecated
	public ModItem(String modID, String name, Item.Properties properties) {
		super(properties);
		setItemName(modID, name);
	}
	
	/**
	 * Set the registry name of {@code this Item} to {@code name} and the unlocalised name to the full registry name.
	 * @param modID
	 * @param name
	 */
	@Deprecated
	public Item setItemName(String modID, String name) {
		setRegistryName(modID, name);
		return this;
	}
}
