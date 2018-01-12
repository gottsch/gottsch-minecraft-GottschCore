/**
 * 
 */
package com.someguyssoftware.gottschcore.mod;

import com.someguyssoftware.gottschcore.config.IConfig;
import com.someguyssoftware.gottschcore.version.BuildVersion;

/**
 * @author Mark Gottschling on Jan 5, 2016
 *
 */
public interface IMod {
	/**
	 * 
	 * @return
	 */
	public IConfig getConfig();
	
	/**
	 * Returns the latest published version of the mod.
	 * @return
	 */
	public BuildVersion getModLatestVersion();
	
	/**
	 * Set the latest published verison of the mod.
	 * @param version
	 */
	public void setModLatestVersion(BuildVersion version);
	
	/**
	 * 
	 */
	public BuildVersion getMinecraftVersion();
	
	/**
	 * By default calls deprecated getVerisionURL() to maintain backwards-compatibility.
	 * @return
	 */
	public default String getVersionURL() {
		return getVerisionURL();
	}
	
	/**
	 * @deprecated use getVersionURL() instead.
	 * @return
	 */
	@Deprecated()
	public default String getVerisionURL() {
		return null;
	}
	
	/**
	 * Get the instance of the mod
	 * @return
	 */
	public IMod getInstance();
	
	/**
	 * Get the name of the mod.
	 * @return
	 */
	public String getName();
	
	/**
	 * Get the name of the mod.
	 * @return
	 */
	public String getId();
	
	/**
	 * The the current mod version.
	 * @return
	 */
	public String getVersion();
	
	
	/**
	 * The Forge update URL of the mod.
	 */
	public default String getUpdateURL() {
		return null;
	}
}
