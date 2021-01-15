package com.someguyssoftware.gottschcore.resource;

import java.util.List;

import com.someguyssoftware.gottschcore.mod.IMod;

public interface IResourceManager {

	/**
	 * 
	 * @param jarResourceRootPath the base path in the mod .jar file to the resources (before the mod ID)
	 * @param modID
	 * @param locations list of subpaths in the mod .jar file to the resources (after the mod ID)
	 */
	void buildAndExpose(String resourceRootPath, String modID, String resourceBasePath, List<String> locations);

	IMod getMod();

	void setMod(IMod mod);

	String getBaseResourceFolder();

	void setBaseResourceFolder(String resourceFolder);

}