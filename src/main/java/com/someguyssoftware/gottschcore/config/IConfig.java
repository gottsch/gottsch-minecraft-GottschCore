/**
 * 
 */
package com.someguyssoftware.gottschcore.config;

/**
 * @author Mark Gottschling on Apr 30, 2017
 *
 */
public interface IConfig {
	public static final String MOD_CATEGORY = "01-mod";
	public static final String LOGGING_CATEGORY = "02-logging";
	public static final String CATEGORY_DIV = "##############################";
	
	public static final String DEFAULT_MODS_FOLDER = "mods";
	public static final String DEFAULT_CONFIG_FOLDER = "config";
	public static final String DEFAULT_LOGGER_LEVEL = "info";
	public static final String DEFAULT_LOGGER_FOLDER = "logs";
	public static final String DEFAULT_LOGGER_FILENAME = "log";
	public static final String DEFAULT_LOGGER_SIZE = "1000K";

	/**
	 * @return
	 */
	public boolean isEnableVersionChecker();

	/**
	 * @param enableVersionChecker
	 */
	void setEnableVersionChecker(boolean enableVersionChecker);

	/**
	 * @return
	 */
	boolean isLatestVersionReminder();

	/**
	 * @param latestVersionReminder
	 */
	void setLatestVersionReminder(boolean latestVersionReminder);

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
	
	/**
	 * 
	 * @return
	 */
	default public String getLogsFolder() {
		return DEFAULT_LOGGER_FOLDER;
	}
	
	default public String getLogSize() {
		return DEFAULT_LOGGER_SIZE;
	}
	
	default public String getLoggingLevel() {
		return DEFAULT_LOGGER_SIZE;
	}
}
