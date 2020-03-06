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
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * @author Mark Gottschling on Nov 15, 2019
 *
 */
@EventBusSubscriber
public class GottschCoreConfig extends AbstractConfig {
	public static ForgeConfigSpec COMMON_CONFIG;
//	public static ForgeConfigSpec CLIENT_CONFIG;

	static {
		COMMON_CONFIG = COMMON_BUILDER.build();
//		CLIENT_CONFIG = CLIENT_BUILDER.build();
	}

	/**
	 * 
	 * @param mod
	 */
	public GottschCoreConfig(IMod mod) {
	}

	@SubscribeEvent
	public static void onLoad(final ModConfig.Loading configEvent) {
		GottschCoreConfig.loadConfig(GottschCoreConfig.COMMON_CONFIG,
				FMLPaths.CONFIGDIR.get().resolve(GottschCore.MODID + "-common.toml"));
	}

	@SubscribeEvent
	public static void onReload(final ModConfig.ConfigReloading configEvent) {
	}
}
