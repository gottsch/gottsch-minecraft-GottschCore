/**
 * 
 */
package com.someguyssoftware.gottschcore.world.gen.structure;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.init.Blocks;


/**
 * @author Mark Gottschling on Dec 23, 2019
 *
 */
public class DecayRuleKeyRegistry {

	private static Map<String, Map<String, String>> registry = new HashMap<>();
	private static DecayRuleKeyRegistry instance = new DecayRuleKeyRegistry();
	
	static {
		Map<String, String> stoneMapping = new HashMap<>();
		stoneMapping.put("0", "minecraft:stone");
		stoneMapping.put("1", "minecraft:granite");
		stoneMapping.put("2", "minecraft:smooth_granite");
		stoneMapping.put("3", "minecraft:diorite");
		stoneMapping.put("4", "minecraft:smooth_diorite");
		stoneMapping.put("5", "minecraft:andesite");
		stoneMapping.put("6", "minecraft:smooth_andesite");
		
		Map<String, String> sandstoneMapping = new HashMap<>();
		
		registry.put(Blocks.STONE.getRegistryName().toString(), stoneMapping);
		registry.put(Blocks.SANDSTONE.getRegistryName().toString(), sandstoneMapping);
	}
	
	/**
	 * 
	 */
	private DecayRuleKeyRegistry() {
	}
	
	/**
	 * 
	 * @return
	 */
	public static DecayRuleKeyRegistry getInstance() {
		return instance;
	}
	
	/**
	 * 
	 * @param clazz
	 * @param copier
	 */
	void register(String name, Map<String, String> map) {
		if (!registry.containsKey(name)) {
			registry.put(name, map);
		}
	}
	
	/**
	 * 
	 * @param clazz
	 */
	private void unregister(String name) {
		if (registry.containsKey(name)) {
			registry.remove(name);
		}
	}
	
	/**
	 * 
	 * @param clazz
	 * @return
	 */
	public Map<String, String> get(Class<?> clazz) {
		if (registry.containsKey(clazz.getName())) {
			return registry.get(clazz.getName());
		}
		return null;
	}
	
	public String get(String name, String metaKey) {
		if (registry.containsKey(name)) {
			Map<String, String> mapping = registry.get(name);
			return mapping.get(metaKey);
		}
		return null;
	}
	
	public boolean has(String name) {
		return registry.containsKey(name);
	}
}
