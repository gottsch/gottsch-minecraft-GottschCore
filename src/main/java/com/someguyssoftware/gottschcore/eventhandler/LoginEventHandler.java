/**
 * 
 */
package com.someguyssoftware.gottschcore.eventhandler;

import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.mod.IMod;
import com.someguyssoftware.gottschcore.version.BuildVersion;
import com.someguyssoftware.gottschcore.version.VersionChecker;

import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * This class uses non-static subscribed methods and therefor the concrete class
 * can not use @Mod.EventBusSubscriber, but rather must be registered manually
 * in the Mod class.
 * 
 * @author Mark Gottschling on Apr 29, 2017
 *
 */
public class LoginEventHandler {
	// reference to the mod.
	private IMod mod;

	/**
	 * 
	 * @param mod
	 */
	public LoginEventHandler(IMod mod) {
		this.mod = mod;
	}

	/**
	 * Check current mod's build version against the published version when the
	 * player logs into a world.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public void checkVersionOnLogIn(PlayerEvent.PlayerLoggedInEvent event) {
		// proceed only if the latest version is not empty and enabled in the config
		if (mod.getConfig() == null || !mod.getConfig().isEnableVersionChecker()
		/* || mod.getModLatestVersion().isEmpty() */) {
			return;
		}

		// get the latest version from the website
		BuildVersion updateJsonVersion = null;
		// check if updateURL is set. if so, use the forge versioning to get the latest
		// version
		if (mod.getUpdateURL() != null && !mod.getUpdateURL().equals("")) {
			updateJsonVersion = VersionChecker.getVersionUsingForge(mod);
		} else {
			return;
		}

		// get the latest version recorded in the config
//		BuildVersion configVersion = new BuildVersion(mod.getConfig().getLatestVersion());
		BuildVersion modVersion = new BuildVersion(mod.getVersion());
//		boolean isConfigCurrent = VersionChecker.checkVersionUsingForge(modVersion, updateJsonVersion);
//		boolean isReminderOn = mod.getConfig().isLatestVersionReminder();
		boolean isCurrent;
		try {
			isCurrent = VersionChecker.checkVersionUsingForge(modVersion, updateJsonVersion);
		} catch (Exception e) {
			GottschCore.LOGGER.error("Unable to determine version using Forge:", e);
			return;
		}

		// update the config to the latest client version if it is not set already
//		if (!isConfigCurrent) {
//			// update config
//			mod.getConfig().setProperty(IConfig.MOD_CATEGORY, "latestVersion", mod.getVersion().toString());
//			// turn the reminder back on for the latest version
//			mod.getConfig().setProperty(IConfig.MOD_CATEGORY, "latestVersionReminder", true);
//		}

//		if (mod.getUpdateURL() != null && !mod.getUpdateURL().equals("")) {
//			// use Forge update method
//			try {
//				isCurrent = VersionChecker.checkVersionUsingForge(modVersion,
//						new BuildVersion(mod.getClass().getAnnotation(Mod.class).version()));
//			} catch (Exception e) {
//				GottschCore.logger.error("Unable to determine version using Forge:", e);
//				isCurrent = VersionChecker.checkVersionUsingForge(mod.getVersion(),
//						new BuildVersion(mod.getClass().getAnnotation(Mod.class).version()));
//			}
//		} else {
//			isCurrent = VersionChecker.checkVersionUsingForge(mod.getVersion(),
//					new BuildVersion(mod.getClass().getAnnotation(Mod.class).version()));
//		}

		if (!isCurrent/* && isReminderOn */) {
			StringBuilder builder = new StringBuilder();
			builder.append(TextFormatting.WHITE).append("A new ").append(TextFormatting.GOLD)
					.append(mod.getName() + " ").append(TextFormatting.WHITE).append("version is available: ")
					.append(TextFormatting.GOLD).append(mod.getVersion().toString());

			event.getPlayer().sendMessage(new StringTextComponent(builder.toString()));
		}
	}

	/**
	 * 
	 * @return
	 */
	public IMod getMod() {
		return mod;
	}

	/**
	 * 
	 * @param mod
	 */
	public void setMod(IMod mod) {
		this.mod = mod;
	}
}
