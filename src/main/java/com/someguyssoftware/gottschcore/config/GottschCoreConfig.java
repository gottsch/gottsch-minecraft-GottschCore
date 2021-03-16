/**
 * 
 */
package com.someguyssoftware.gottschcore.config;

import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.mod.IMod;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Reloading;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * @author Mark Gottschling on Nov 15, 2019
 *
 */
@EventBusSubscriber(modid = GottschCore.MODID, bus = EventBusSubscriber.Bus.MOD)
public class GottschCoreConfig extends AbstractConfig {
	protected static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec COMMON_CONFIG;

	static {
		MOD = new Mod(COMMON_BUILDER);
		LOGGING = new Logging(COMMON_BUILDER);
		COMMON_CONFIG = COMMON_BUILDER.build();
	}

	private static IMod mod;

	/**
	 * 
	 * @param mod
	 */
	public GottschCoreConfig(IMod mod) {
		GottschCoreConfig.mod = mod;
	}

	@SubscribeEvent
	public static void onLoad(final ModConfig.Loading configEvent) {
		GottschCoreConfig.loadConfig(GottschCoreConfig.COMMON_CONFIG,
				FMLPaths.CONFIGDIR.get().resolve(mod.getId() + "-common.toml"));
	}

	@SubscribeEvent
	public static void onReload(final Reloading configEvent) {
	}

	@Override
	public boolean isEnableVersionChecker() {
		return GottschCoreConfig.MOD.enableVersionChecker.get();
	}

	@Override
	public void setEnableVersionChecker(boolean enableVersionChecker) {
		GottschCoreConfig.MOD.enableVersionChecker.set(enableVersionChecker);
	}

	@Override
	public boolean isLatestVersionReminder() {
		return GottschCoreConfig.MOD.latestVersionReminder.get();
	}

	@Override
	public void setLatestVersionReminder(boolean latestVersionReminder) {
		GottschCoreConfig.MOD.latestVersionReminder.set(latestVersionReminder);
	}

	@Override
	public boolean isModEnabled() {
		return GottschCoreConfig.MOD.enabled.get();
	}

	@Override
	public void setModEnabled(boolean modEnabled) {
		GottschCoreConfig.MOD.enabled.set(modEnabled);
	}

	@Override
	public String getModsFolder() {
		return GottschCoreConfig.MOD.folder.get();
	}

	@Override
	public void setModsFolder(String modsFolder) {
		GottschCoreConfig.MOD.folder.set(modsFolder);
	}

	@Override
	public String getConfigFolder() {
		return GottschCoreConfig.MOD.configFolder.get();
	}

	@Override
	public void setConfigFolder(String configFolder) {
		GottschCoreConfig.MOD.configFolder.set(configFolder);
	}
}
