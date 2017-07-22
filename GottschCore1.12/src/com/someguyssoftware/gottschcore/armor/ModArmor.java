/**
 * 
 */
package com.someguyssoftware.gottschcore.armor;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Mark Gottschling on Jul 21, 2017
 *
 */
public class ModArmor extends ItemArmor {
	private String texture;
	private String repairUnlocalizedName;
	
	/**
	 * 
	 * @param modID The Mod ID
	 * @param name The name of the armor
	 * @param material The material of the armor
	 * @param renderIndex Used on RenderPlayer to select the correspondent armor to be rendered on the player: 0 is cloth, 1 is chain, 2 is iron, 3 is diamond and 4 is gold.
	 * @param armourType Stores the armor type: 0 is helmet, 1 is plate, 2 is legs and 3 is boots
	 * @param texture Relative path to the texture file used to render the Item
	 * @param repairUnlocalizedName The unlocalized name of the Item that is used to repair the Armor
	 */
	public ModArmor(
			String modID,
			String name,
			ArmorMaterial material,
			int renderIndex,
			EntityEquipmentSlot armourType,
			String texture,
			Item repairItem) {
		
		// call the super construtor
		super(material, renderIndex, armourType);
		
		setArmorName(modID, name);
		setTexture(modID + ":" + texture);
		setRepairUnlocalizedName(repairItem.getUnlocalizedName());
	}

	/**
	 * Set the registry name of {@code this ItemArmor} to {@code name} and the unlocalised name to the full registry name.
	 * @param modID
	 * @param name
	 */
	public void setArmorName(String modID, String name) {
		setRegistryName(modID, name);
		setUnlocalizedName(getRegistryName().toString());
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public String getArmorTexture(ItemStack itemStack, Entity entity, EntityEquipmentSlot slot, String type) {
		return this.texture;
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
	
	public String getTexture() {
		return texture;
	}

	private void setTexture(String texture) {
		this.texture = texture;
	}

	public String getRepairUnlocalizedName() {
		return repairUnlocalizedName;
	}

	private void setRepairUnlocalizedName(String repairUnlocalizedName) {
		this.repairUnlocalizedName = repairUnlocalizedName;
	}
}
