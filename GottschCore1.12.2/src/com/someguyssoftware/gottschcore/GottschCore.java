/**
 * 
 */
package com.someguyssoftware.gottschcore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.someguyssoftware.gottschcore.annotation.Credits;
import com.someguyssoftware.gottschcore.command.ShowVersionCommand;
import com.someguyssoftware.gottschcore.config.GottschCoreConfig;
import com.someguyssoftware.gottschcore.config.IConfig;
import com.someguyssoftware.gottschcore.mod.AbstractMod;
import com.someguyssoftware.gottschcore.mod.IMod;
import com.someguyssoftware.gottschcore.version.BuildVersion;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * 
 * @author Mark Gottschling on Jul 13, 2017
 *
 */
@Mod(modid = GottschCore.MODID, name = GottschCore.NAME, version = GottschCore.VERSION, dependencies = "required-after:forge@[14.23.5.2768,)", acceptedMinecraftVersions = "[1.12.2]", updateJSON = GottschCore.UPDATE_JSON_URL)
@Credits(values = { "GottschCore for Minecraft 1.12+ was first developed by Mark Gottschling on Jul 13, 2017." })
public class GottschCore extends AbstractMod {
	// constants
	public static final String MODID = "gottschcore";
	protected static final String NAME = "GottschCore";
	protected static final String VERSION = "1.13.1";
	public static final String UPDATE_JSON_URL = "https://raw.githubusercontent.com/gottsch/gottsch-minecraft-GottschCore/master/GottschCore1.12.2/update.json";

	// the version of Minecraft that this mod is developed for
	private static final BuildVersion MINECRAFT_VERSION = new BuildVersion(1, 12, 2);

	/*
	 * NOTE not used. Mods that used this library should define their own config
	 * path.
	 */
	private static final String GOTTSCHCORE_CONFIG_DIR = "gottschcore";
	private static GottschCoreConfig config;

	// latest version
	private static BuildVersion latestVersion;

	// logger
	public static Logger logger = LogManager.getLogger(GottschCore.NAME);

	/**
	 * Required for Forge
	 */
	@Instance(GottschCore.MODID)
	public static GottschCore instance;

	/**
	 * 
	 */
	public GottschCore() {
	}

	/**
	 * 
	 * @param event
	 */
	@Override
	@EventHandler
	public void preInt(final FMLPreInitializationEvent event) {
		super.preInt(event);
		// register additional events

		// create and load the config file
		config = new GottschCoreConfig(this, event.getModConfigurationDirectory(), GOTTSCHCORE_CONFIG_DIR,
				"general.cfg");
	}

	@EventHandler
	public void serverStarted(final FMLServerStartingEvent event) {
		event.registerServerCommand(new ShowVersionCommand(this));
	}

	@Override
	@EventHandler
	public void postInit(final FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	@Override
	public String getName() {
		return GottschCore.NAME;
	}

	@Override
	public String getId() {
		return GottschCore.MODID;
	}

	@Override
	public String getVersion() {
		return GottschCore.VERSION;
	}

	@Override
	public IMod getInstance() {
		return GottschCore.instance;
	}

	@Override
	public String getUpdateURL() {
		return GottschCore.UPDATE_JSON_URL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.someguyssoftware.gottschcore.IMod#getConfig()
	 */
	@Override
	public IConfig getConfig() {
		return GottschCore.config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.someguyssoftware.gottschcore.IMod#getMinecraftVersion()
	 */
	@Override
	public BuildVersion getMinecraftVersion() {
		return GottschCore.MINECRAFT_VERSION;
	}

	@Override
	public BuildVersion getModLatestVersion() {
		return latestVersion;
	}

	@Override
	public void setModLatestVersion(BuildVersion version) {
		GottschCore.latestVersion = version;
	}
}
