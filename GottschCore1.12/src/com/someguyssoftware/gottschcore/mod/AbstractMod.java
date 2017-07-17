/**
 * 
 */
package com.someguyssoftware.gottschcore.mod;

import com.someguyssoftware.gottschcore.eventhandler.LoginEventHandler;
import com.someguyssoftware.gottschcore.version.BuildVersion;
import com.someguyssoftware.gottschcore.version.VersionChecker;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * 
 * @author Mark Gottschling on Jul 13, 2017
 *
 */
public abstract class AbstractMod implements IMod {

	// latest version
	private static BuildVersion latestVersion;

	/**
	 * @param event
	 */
	@EventHandler
	public void preInt(FMLPreInitializationEvent event) {
		// register events
		// TODO add registration
		MinecraftForge.EVENT_BUS.register(new LoginEventHandler(this));
	}
	
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		// does nothing currently
	}
	
	/**
	 * 
	 * @param event
	 */
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		// check config if version check is enabled
		if (getConfig().isEnableVersionChecker())	{
			// get the latest version from the website
			setModLatestVersion(VersionChecker.getVersion(getVerisionURL(), getMinecraftVersion()));
		}
	}
	
    /**
     * Prepend the name with the mod ID, suitable for ResourceLocations such as textures.
     * @param name
     * @return eg "treasure:myblockname"
     */
    public String prependModID(String name) {return this.getId() + ":" + name;}
    
	/*
	 *  TODO this is wrong. getInstance() is a static method for singleton's to get the instance of the class.
	 *  But how else can you get the mod in the api when you don't know what the given mod class is.
	 */
	/* (non-Javadoc)
	 * @see com.someguyssoftware.mod.IMod#getInstance()
	 */
	@Override
	abstract public IMod getInstance();

	@Override
	abstract public String getVersion();
	
	/* (non-Javadoc)
	 * @see com.someguyssoftware.mod.IMod#getModLatestVersion()
	 */
	@Override
	public BuildVersion getModLatestVersion() {
		return AbstractMod.latestVersion;
	}
    
	/* (non-Javadoc)
	 * @see com.someguyssoftware.mod.IMod#setModLatestVersion()
	 */
	@Override
	public void setModLatestVersion(BuildVersion latestVersion) {
		AbstractMod.latestVersion = latestVersion;
	}

}
