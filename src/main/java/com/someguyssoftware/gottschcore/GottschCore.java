package com.someguyssoftware.gottschcore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.someguyssoftware.gottschcore.annotation.Credits;
import com.someguyssoftware.gottschcore.annotation.ModInfo;
import com.someguyssoftware.gottschcore.config.GottschCoreConfig;
import com.someguyssoftware.gottschcore.config.IConfig;
import com.someguyssoftware.gottschcore.config.IModSetup;
import com.someguyssoftware.gottschcore.mod.IMod;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * 
 * @author Mark Gottschling on Feb 26, 2020
 *
 */
@Mod(value = GottschCore.MODID)
@ModInfo(modid = GottschCore.MODID, name = GottschCore.NAME, version = GottschCore.VERSION, minecraftVersion = "1.18.2", forgeVersion = "40.1.0", updateJsonUrl = "https://raw.githubusercontent.com/gottsch/gottsch-minecraft-GottschCore/1.18.2-master/update.json")
@Credits(values = { "GottschCore for Minecraft 1.12+ was first developed by Mark Gottschling on Jul 13, 2017." })
public class GottschCore implements IMod {
	// logger
	public static final Logger LOGGER = LogManager.getLogger(GottschCore.class.getSimpleName());

	// constants
	public static final String MODID = "gottschcore";
	protected static final String NAME = "GottschCore";
	protected static final String VERSION = "1.9.1";
	
	public static GottschCore instance;
	private static GottschCoreConfig config;

	public GottschCore() {
		GottschCore.instance = this;
		GottschCore.config = new GottschCoreConfig(this);

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, GottschCoreConfig.COMMON_CONFIG);

		// Register the setup method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

		GottschCoreConfig.loadConfig(GottschCoreConfig.COMMON_CONFIG,
				FMLPaths.CONFIGDIR.get().resolve("gottschcore-common.toml"));
	}

	/**
	 * ie. preint
	 * 
	 * @param event
	 */
	private void setup(final FMLCommonSetupEvent event) {
		IModSetup.addRollingFileAppender(this);
	}

	@Override
	public IMod getInstance() {
		return GottschCore.instance;
	}

	@Override
	public String getId() {
		return GottschCore.MODID;
	}

	@Override
	public IConfig getConfig() {
		return GottschCore.config;
	}	
}
