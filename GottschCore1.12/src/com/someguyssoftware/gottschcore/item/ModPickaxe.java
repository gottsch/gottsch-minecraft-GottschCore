/**
 * 
 */
package com.someguyssoftware.gottschcore.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;

/**
 * @author Mark Gottschling on Jul 22, 2017
 *
 */
public class ModPickaxe extends ItemPickaxe {
	private String repairUnlocalizedName;
	
	/**
	 * 
	 * @param material
	 */
	public ModPickaxe(Item.ToolMaterial material, Item repairItem) {
		super(material);
		setRepairUnlocalizedName(repairItem.getUnlocalizedName());
	}
	
	/**
	 * 
	 */
	public ModPickaxe(String modID, String name, Item.ToolMaterial material, Item repairItem)  {
		super(material);
		setItemName(modID, name);
		setRepairUnlocalizedName(repairItem.getUnlocalizedName());
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
	
    /**
     * Return whether this item is repairable in an anvil.
     */
	@Override
	public boolean getIsRepairable(ItemStack itemToRepair, ItemStack resourceItem) {
		if (itemToRepair.isItemDamaged()) {
			if (resourceItem.getItem().getUnlocalizedName().equals(this.repairUnlocalizedName)) {
				return true;
			}       
		}
		return false;
    }

	public String getRepairUnlocalizedName() {
		return repairUnlocalizedName;
	}

	public void setRepairUnlocalizedName(String repairUnlocalizedName) {
		this.repairUnlocalizedName = repairUnlocalizedName;
	}
}
