/**
 * 
 */
package com.someguyssoftware.gottschcore.mod;

import com.someguyssoftware.gottschcore.annotation.ModInfo;
import com.someguyssoftware.gottschcore.config.IConfig;

/**
 * 
 * @author Mark Gottschling on Nov 14, 2019
 *
 */
@Deprecated
public interface IMod {

	/**
	 * 
	 * @return
	 */
	public IConfig getConfig();

	/**
	 * Get the instance of the mod
	 * 
	 * @return
	 */
	public IMod getInstance();

	/**
	 * Get the name of the mod.
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * Get the name of the mod.
	 * 
	 * @return
	 */
	public default String getName() {
		ModInfo modInfo = getClass().getAnnotation(ModInfo.class);
		return modInfo.name();
	}

	/**
	 * The the current mod version.
	 * 
	 * @return
	 */
	public default String getVersion() {
		ModInfo modInfo = getClass().getAnnotation(ModInfo.class);
		return modInfo.version();
	}

	/**
	 * The the minecraft version.
	 * 
	 * @return
	 */
	public default String getMincraftVersion() {
		ModInfo modInfo = getClass().getAnnotation(ModInfo.class);
		return modInfo.minecraftVersion();
	}

	/**
	 * The the forge version.
	 * 
	 * @return
	 */
	public default String getForgeVersion() {
		ModInfo modInfo = getClass().getAnnotation(ModInfo.class);
		return modInfo.forgeVersion();
	}

	/**
	 * The Forge update URL of the mod.
	 */
	public default String getUpdateURL() {
		ModInfo modInfo = getClass().getAnnotation(ModInfo.class);
		return modInfo.updateJsonUrl();
	}
}
