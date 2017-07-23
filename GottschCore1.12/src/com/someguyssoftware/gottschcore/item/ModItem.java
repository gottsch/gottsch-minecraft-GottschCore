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

	/**
	 * 
	 */
	public ModItem() {
		super();
	}
	
	/**
	 * Set the registry name of {@code this ItemArmor} to {@code name} and the unlocalised name to the full registry name.
	 * @param modID
	 * @param name
	 */
	public void setItemName(String modID, String name) {
		setRegistryName(modID, name);
		setUnlocalizedName(getRegistryName().toString());
	}
}
