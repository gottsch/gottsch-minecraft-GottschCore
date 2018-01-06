/**
 * 
 */
package com.someguyssoftware.gottschcore.eventhandler;

import static net.minecraftforge.common.ForgeVersion.Status.AHEAD;
import static net.minecraftforge.common.ForgeVersion.Status.BETA;
import static net.minecraftforge.common.ForgeVersion.Status.BETA_OUTDATED;
import static net.minecraftforge.common.ForgeVersion.Status.OUTDATED;
import static net.minecraftforge.common.ForgeVersion.Status.PENDING;
import static net.minecraftforge.common.ForgeVersion.Status.UP_TO_DATE;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.config.IConfig;
import com.someguyssoftware.gottschcore.mod.IMod;
import com.someguyssoftware.gottschcore.version.BuildVersion;
import com.someguyssoftware.gottschcore.version.VersionChecker;

import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.versioning.ComparableVersion;

/**
 * This class uses non-static subscribed methods and therefor the concrete class can not
 * use @Mod.EventBusSubscriber, but rather must be registered manually in the Mod class.
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

	/**
	 * Check current mod's build version against the published version when the player logs into a world.
	 * @param event
	 */
	@SubscribeEvent
	public void checkVersionOnLogIn(PlayerEvent.PlayerLoggedInEvent event) {
		// proceed only if the latest version is not empty and enabled in the config
		if (mod.getConfig() == null || !mod.getConfig().isEnableVersionChecker() || mod.getModLatestVersion().isEmpty()) {
			return;
		}
		
		// get the latest version recorded in the config
		BuildVersion configVersion = new BuildVersion(mod.getConfig().getLatestVersion());
		boolean isConfigCurrent = VersionChecker.checkVersion(mod.getModLatestVersion(), configVersion);
		boolean isReminderOn = mod.getConfig().isLatestVersionReminder();
		boolean isCurrent = false;
		
		// update the config to the latest client version if it is not set already
		if (!isConfigCurrent) {
			// update config
			mod.getConfig().setProperty(IConfig.MOD_CATEGORY, "latestVersion", mod.getModLatestVersion().toString());
			// turn the reminder back on for the latest version
			mod.getConfig().setProperty(IConfig.MOD_CATEGORY, "latestVersionReminder", true);
		}

        if (mod.getUpdateURL() != null && !mod.getUpdateURL().equals("")) {
        	// use Forge update method
        	try {
        		isCurrent = VersionChecker.checkVersionUsingForge(mod);
        	}
        	catch(Exception e) {
        		GottschCore.logger.error("Unable to determine version using Forge:", e);
            	isCurrent = VersionChecker.checkVersion(mod.getModLatestVersion(), new BuildVersion(mod.getClass().getAnnotation(Mod.class).version()));
        	}
        }
        else {
        	isCurrent = VersionChecker.checkVersion(mod.getModLatestVersion(), new BuildVersion(mod.getClass().getAnnotation(Mod.class).version()));
        }
		
		if (!isCurrent && isReminderOn) {
			StringBuilder builder = new StringBuilder();
			builder
				.append(TextFormatting.WHITE)
				.append("A new ")
				.append(TextFormatting.GOLD)
				.append(mod.getName() + " ")
				.append(TextFormatting.WHITE)
				.append("version is available: ")
				.append(TextFormatting.GOLD)
				.append(mod.getModLatestVersion().toString());

			event.player.sendMessage(new TextComponentString(builder.toString()));
			
			// TODO spin a new thread and have it sleep for 8 seconds before displaying messageepi
			// TODO present the user with an interface to toggle version reminder instead of text message
			// TODO or present a command usage to stop reminder for this version.
			// TODO update config
		}
	}
}
