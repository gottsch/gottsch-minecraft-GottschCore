/**
 * 
 */
package com.someguyssoftware.gottschcore.item;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

/**
 * @author Mark Gottschling on Jul 22, 2017
 *
 */
public class ModAxe extends ItemTool {
	private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(new Block[] { Blocks.PLANKS, Blocks.BOOKSHELF,
			Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LIT_PUMPKIN, Blocks.MELON_BLOCK,
			Blocks.LADDER, Blocks.WOODEN_BUTTON, Blocks.WOODEN_PRESSURE_PLATE});
	
	private String repairUnlocalizedName;
	
	/**
	 * 
	 * @param material
	 */
	public ModAxe(Item.ToolMaterial material, Item repairItem) {
		super(material, EFFECTIVE_ON);
		setRepairUnlocalizedName(repairItem.getUnlocalizedName());
	}
	
	/**
	 * 
	 */
	public ModAxe(String modID, String name, Item.ToolMaterial material, Item repairItem)  {
		super(material, EFFECTIVE_ON);
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
	 * 
	 */
//	public float getStrVsBlock(ItemStack stack, IBlockState state) {
//		Material material = state.getMaterial();
//		return material != Material.WOOD && material != Material.PLANTS && material != Material.VINE
//				? super.getStrVsBlock(stack, state) : this.efficiencyOnProperMaterial;
//	}
	
	/**
	 * 
	 */
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        Material material = state.getMaterial();
        return material != Material.WOOD && material != Material.PLANTS && material != Material.VINE ? super.getDestroySpeed(stack, state) : this.efficiency;
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
