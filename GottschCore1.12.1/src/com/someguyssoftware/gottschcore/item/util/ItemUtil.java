/**
 * 
 */
package com.someguyssoftware.gottschcore.item.util;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;

/**
 * 
 * @author Mark Gottschling on Jul 28, 2015
 *
 */
public class ItemUtil {
	private static ResourceLocation enchantedBookName = (ResourceLocation) Item.REGISTRY.getNameForObject(Items.ENCHANTED_BOOK);
	private static ResourceLocation potionName = (ResourceLocation) Item.REGISTRY.getNameForObject(Items.POTIONITEM);
	private static int potions[] = new int[] {
		8193, 8257, 8225, 8194, 8258, 8226, 8195, 8259, 8197, 8229, 8198, 8262, 8201, 8265, 8233, 8203, 8235, 8205, 8269, 8206, 8270,
		8196, 8260, 8228, 8200, 8264, 8202, 8266, 8204, 8236, 8289, 8290, 8297, 8292
		};
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static Item getItemFromName(String name) {
		Item item = null;
		try {
			// attempt to get named item from the Items Registry first
			item = Item.getByNameOrId(name);

			// if unable to create
			if (item == null) {
				// the item may be a block, convert it to item and attempt to get it
				Block block = Block.getBlockFromName(name);
				/*
				 * NOTE getBlockFromName() may return null and getItemBlock(null) will return AIR
				 */
				if (block != null) {
					item = Item.getItemFromBlock(block);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return item;
	}
	
	/**
	 * 
	 * @param stack
	 * @return
	 */
	public static ItemStack addEnchantment(ItemStack stack) {
		Random random = new Random();
		int enchantmentIndex = 0;
		Item item = stack.getItem();
		if (item instanceof ItemArmor) {
			// generate the enchantment index
			enchantmentIndex = random.nextInt(10);
			switch(enchantmentIndex) {
			case 0:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("protection"), random.nextInt(4) + 1);
				break;		
			case 1:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("fire_protection"), random.nextInt(4) + 1);
				break;
			case 2:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("feather_falling"), random.nextInt(4) + 1);
				break;
			case 3:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("blast_protection"), random.nextInt(4) + 1);
				break;
			case 4:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("projectile_protection"), random.nextInt(4) + 1);
				break;	
			case 5:
				if (item.getUnlocalizedName().toLowerCase().contains("helmet")) {
					stack.addEnchantment(Enchantment.getEnchantmentByLocation("respiration"), random.nextInt(4) + 1);
				}
				break;
			case 6:
				if (item.getUnlocalizedName().toLowerCase().contains("helmet")) {
					stack.addEnchantment(Enchantment.getEnchantmentByLocation("aqua_affinity"), 1);
				}
				break;
			case 7:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("thorns"), random.nextInt(3) + 1);
				break;	
			case 8:
				// TODO should be depth strider
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("thorns"), random.nextInt(3) + 1);
				break;
			case 9:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("unbreaking"), random.nextInt(3) + 1);
				break;	
			default:
				break;
			}
		}
		else if (item instanceof ItemSword) {
			enchantmentIndex = random.nextInt(7);
			switch(enchantmentIndex) {
			case 0:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("sharpness"), random.nextInt(5) + 1);
				break;
			case 1:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("smite"), random.nextInt(5) + 1);
				break;
			case 2:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("bane_of_arthropods"), random.nextInt(5) + 1);
				break;
			case 3:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("knockback"), random.nextInt(2) + 1);
				break;
			case 4:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("fire_aspect"), random.nextInt(2) + 1);
				break;
			case 5:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("looting"), random.nextInt(3) + 1);
				break;
			case 6:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("unbreaking"), random.nextInt(3) + 1);
				break;
			}
		}
		else if (item instanceof ItemTool) {
			enchantmentIndex = random.nextInt(4);
			switch(enchantmentIndex) {
			case 0:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("efficiency"), random.nextInt(5) + 1);
				break;
			case 1:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("silk_touch"), 1);
				break;
			case 2:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("unbreaking"), random.nextInt(3) + 1);
				break;
			case 3:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("fortune"), random.nextInt(3) + 1);
				break;
			}
		}
		else if (item instanceof ItemBow) {
			enchantmentIndex = random.nextInt(4);
			switch(enchantmentIndex) {
			case 0:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("power"), random.nextInt(5) + 1);
				break;
			case 1:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("punch"), random.nextInt(2) + 1);
				break;
			case 2:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("flame"), 1);
				break;
			case 3:
				stack.addEnchantment(Enchantment.getEnchantmentByLocation("infinity"), 1);
				break;
			}
		}
		return stack;
	}

	/**
	 * @return the potions
	 */
	public static int[] getPotions() {
		return potions;
	}

	/**
	 * @return the enchantedBookName
	 */
	public static ResourceLocation getEnchantedBookName() {
		return enchantedBookName;
	}

	/**
	 * @return the potionName
	 */
	public static ResourceLocation getPotionName() {
		return potionName;
	}
}
