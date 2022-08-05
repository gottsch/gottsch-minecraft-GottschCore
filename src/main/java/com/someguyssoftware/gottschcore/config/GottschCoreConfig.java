/*
 * This file is part of  GottschCore.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
 * 
 * All rights reserved.
 *
 * GottschCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GottschCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GottschCore.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package com.someguyssoftware.gottschcore.config;

import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.mod.IMod;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * @author Mark Gottschling on Nov 15, 2019
 *
 */
@EventBusSubscriber(modid = GottschCore.MODID, bus = EventBusSubscriber.Bus.MOD)
public class GottschCoreConfig extends AbstractConfig {
	protected static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec COMMON_CONFIG;

	public static final Mod MOD;
	public static final Logging LOGGING;
	
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
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		GottschCoreConfig.loadConfig(GottschCoreConfig.COMMON_CONFIG,
				FMLPaths.CONFIGDIR.get().resolve(mod.getId() + "-common.toml"));
	}

	@SubscribeEvent
	public static void onReload(final ModConfigEvent.Reloading configEvent) {
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
	
	@Override
	public String getLogsFolder() {
		return GottschCoreConfig.LOGGING.folder.get();
	}
	
	@Override
	public String getLogSize() {
		return GottschCoreConfig.LOGGING.size.get();
	}
	
	@Override
	public String getLoggingLevel() {
		return GottschCoreConfig.LOGGING.level.get();
	}
}
