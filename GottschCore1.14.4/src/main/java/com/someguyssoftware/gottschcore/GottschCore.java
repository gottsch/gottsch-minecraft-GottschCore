package com.someguyssoftware.gottschcore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.someguyssoftware.gottschcore.annotation.Credits;
import com.someguyssoftware.gottschcore.annotation.ModInfo;
import com.someguyssoftware.gottschcore.config.GottschCoreConfig;
import com.someguyssoftware.gottschcore.config.IConfig;
import com.someguyssoftware.gottschcore.mod.IMod;

import net.minecraft.block.Blocks;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * 
 * @author Mark Gottschling on Nov 13, 2019
 *
 */
@Mod(value=GottschCore.MODID)
@ModInfo(
		modid=GottschCore.MODID,
		name="GottschCore",
		version="1.9.0",
		minecraftVersion="1.14.4",
		forgeVersion="28.1.0",
		updateJsonUrl="https://raw.githubusercontent.com/gottsch/gottsch-minecraft-GottschCore/master/GottschCore1.12.2/update.json")
@Credits(values={"GottschCore for Minecraft 1.12+ was first developed by Mark Gottschling on Jul 13, 2017."})
public class GottschCore implements IMod {
	// logger
	public static final Logger LOGGER = LogManager.getLogger(GottschCore.class.getSimpleName());

	// constants
	public static final String MODID = "gottschcore";
	//	protected static final String NAME = "GottschCore";
	//	protected static final String VERSION = "1.9.0";
	//	public static final String UPDATE_JSON_URL = "https://raw.githubusercontent.com/gottsch/gottsch-minecraft-GottschCore/master/GottschCore1.12.2/update.json";

	// the version of Minecraft that this mod is developed for
	//	private static final BuildVersion MINECRAFT_VERSION = new BuildVersion(1, 14, 4);

	public static GottschCore instance;

	public GottschCore() {
		GottschCore.instance = this;
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, GottschCoreConfig.CLIENT_CONFIG);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, GottschCoreConfig.COMMON_CONFIG);

		// Register the setup method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

		GottschCoreConfig.loadConfig(GottschCoreConfig.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve("gottschcore-client.toml"));
		GottschCoreConfig.loadConfig(GottschCoreConfig.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("gottschcore-common.toml"));

		// test accessing the logs
		GottschCoreConfig.LOGGING.filename.get();
	}

	/**
	 * ie. preint
	 * @param event
	 */
	private void setup(final FMLCommonSetupEvent event) {   	

		// some preinit code
		LOGGER.info("HELLO FROM PREINIT");
		LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
	}

	@Override
	public IMod getInstance() {
		return this.instance;
	}


	@Override
	public String getId() {
		return GottschCore.MODID;
	}
}
