/**
 * 
 */
package com.someguyssoftware.gottschcore.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * @author Mark Gottschling on Apr 30, 2017
 *
 */
public interface IConfig {
	public static final String MOD_CATEGORY = "03-mod";
	
	/**
	 * Loads the Forge mod Configuration file.
	 * @param file
	 * @return the loaded Forge mod Configuration;
	 */
	default public Configuration load(File file) {
        Configuration config = new Configuration(file);
        config.load();
		return config;
	}

	/**
	 * @return
	 */
	public boolean isEnableVersionChecker();

	/**
	 * @return
	 */
	String getLatestVersion();

	/**
	 * @param enableVersionChecker
	 */
	void setEnableVersionChecker(boolean enableVersionChecker);

	/**
	 * @param latestVersion
	 */
	void setLatestVersion(String latestVersion);

	/**
	 * @return
	 */
	boolean isLatestVersionReminder();

	/**
	 * @param latestVersionReminder
	 */
	void setLatestVersionReminder(boolean latestVersionReminder);

	/**
	 * @param forgeConfiguration
	 */
	void setForgeConfiguration(Configuration forgeConfiguration);

	/**
	 * @return
	 */
	Configuration getForgeConfiguration();

	/**
	 * @param category
	 * @param key
	 * @param value
	 */
	void setProperty(String category, String key, boolean value);

	/**
	 * @param category
	 * @param key
	 * @param value
	 */
	void setProperty(String category, String key, String value);

	/**
	 * 
	 * @param property
	 * @param value
	 */
	public void setProperty(Property property, String value);

	/**
	 * @return
	 */
	boolean isModEnabled();

	/**
	 * @param modEnabled
	 */
	void setModEnabled(boolean modEnabled);
	
	/**
	 * 
	 * @return
	 */
	public String getModsFolder();
	
	/**
	 * 
	 * @param modFolder
	 */
	public void setModsFolder(String modsFolder);

	/**
	 * 
	 * @return
	 */
	public String getConfigFolder();

	/**
	 * 
	 * @param configFolder
	 */
	void setConfigFolder(String configFolder);

}
