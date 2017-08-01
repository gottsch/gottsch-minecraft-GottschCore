/**
 * 
 */
package com.someguyssoftware.gottschcore.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * TODO Dec 25, 2016 - anything to do with updating the stack size can be reworked as this is taken
 * care of already natively.
 * @author Mark Gottschling on Jul 28, 2015
 *
 */
public class InventoryUtil {
	
	/**
	 * Adds the item to the inventory. Will automatically update the available slots list.
	 * @param inventory
	 * @param stack
	 * @param random
	 * @param availableSlots
	 */
	public static void addItemToInventory(IInventory inventory, ItemStack stack, Random random, List<Integer> availableSlots) {
		int slot = 0;
		
		// get the stack size
		int stackSize = stack.getCount();

		try {
		while (stack.getMaxStackSize() < stackSize) {
			// check if slots are available
			if (availableSlots != null && availableSlots.size() > 0) {
				// reduce the size of the stack to it's maximum allowed limit
				stack.setCount(stack.getMaxStackSize());
				// decrement the size
				stackSize -= stack.getMaxStackSize();				
				// get the next available slot
				int index = random.nextInt(availableSlots.size());			
				slot = availableSlots.get(index);
				// remove the availabe slot
				availableSlots.remove(index);				
				// add to inventory
				inventory.setInventorySlotContents(slot, stack);	
			}
			else {
				return;
			}
		}
		
		if (stackSize > 0) {
			if (availableSlots != null && availableSlots.size() > 0) {
				// get the next available slot
				int index = random.nextInt(availableSlots.size());	
				slot = availableSlots.get(index);
				// remove the availabe slot
				availableSlots.remove(index);		
				inventory.setInventorySlotContents(slot, stack);	
			}
		}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param inventory
	 * @param stack
	 * @param random
	 * @return
	 */
	public static List<Integer> addItemToInventory(IInventory inventory, ItemStack stack, Random random) {
		List<Integer> availableSlots = getAvailableSlots(inventory);		
		addItemToInventory(inventory, stack, random, availableSlots);
		return availableSlots;
	}
	
	/**
	 * 
	 * @param inventory
	 * @param stack
	 * @param slot
	 */
	public static void addItemToInventory(IInventory inventory, ItemStack stack, int slot) {
		// ensure stack has items
		if (stack.getCount() > 0) {
			if (slot < inventory.getSizeInventory() && inventory.getStackInSlot(slot) == null) {
				// ensure the stack has the correct size
				if (stack.getCount() > stack.getMaxStackSize()) {
					stack.setCount(stack.getMaxDamage());
				}
				// add stack to chest
				inventory.setInventorySlotContents(slot, stack);	
			}
		}
	}
	
	/**
	 * Updated to check against AIR items in the slots as it seems in MC1.11 all slots are filled with AIR by default.
	 * @param inventory
	 * @return
	 */
	public static List<Integer> getAvailableSlots(IInventory inventory) {
		List<Integer> availableSlots = new ArrayList<>();
		Item airItem = Item.getItemFromBlock(Blocks.AIR);
		for (int i =0; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i) == null || inventory.getStackInSlot(i).getItem() == airItem) {
				availableSlots.add(new Integer(i));
			}
		}
		return availableSlots;
	}
}
