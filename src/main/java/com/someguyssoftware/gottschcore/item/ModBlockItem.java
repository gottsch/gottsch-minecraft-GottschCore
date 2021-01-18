/**
 * 
 */
package com.someguyssoftware.gottschcore.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

/**
 * @author Mark Gottschling on Jul 22, 2017
 *
 */
public class ModBlockItem extends BlockItem {

	/**
	 * 
	 */
	public ModBlockItem(String modID, String name, Block block, Item.Properties properties) {
		super(block, properties);
		setItemName(modID, name);
	}
	
	/**
	 * Set the registry name of {@code this Item} to {@code name} and the unlocalised name to the full registry name.
	 * @param modID
	 * @param name
	 */
	public Item setItemName(String modID, String name) {
		setRegistryName(modID, name);
		return this;
	}
}
