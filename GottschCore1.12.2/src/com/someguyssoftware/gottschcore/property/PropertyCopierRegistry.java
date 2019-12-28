/**
 * 
 */
package com.someguyssoftware.gottschcore.property;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Mark Gottschling on Dec 23, 2019
 *
 */
public class PropertyCopierRegistry {

	private Map<String, IPropertyCopier> registry;
	private static PropertyCopierRegistry instance = new PropertyCopierRegistry();
	
	/**
	 * 
	 */
	private PropertyCopierRegistry() {
		registry = new HashMap<>();
	}
	
	/**
	 * 
	 * @return
	 */
	public static PropertyCopierRegistry getInstance() {
		return instance;
	}
	
	/**
	 * 
	 * @param clazz
	 * @param copier
	 */
	void register(Class<?> clazz, IPropertyCopier copier) {
		if (!registry.containsKey(clazz.getName())) {
//			logger.debug("Registering Rotator for "  + clazz.getName());
			registry.put(clazz.getName(), copier);
		}
	}
	
	/**
	 * 
	 * @param clazz
	 */
	private void unregister(Class<?> clazz) {
		if (registry.containsKey(clazz.getName())) {
			registry.remove(clazz.getName());
		}
	}
	
	/**
	 * 
	 * @param clazz
	 * @return
	 */
	public IPropertyCopier get(Class<?> clazz) {
		if (registry.containsKey(clazz.getName())) {
			return registry.get(clazz.getName());
		}
		return null;
	}
	
	public boolean has(Class<?> clazz) {
		return registry.containsKey(clazz.getName());
	}
}
