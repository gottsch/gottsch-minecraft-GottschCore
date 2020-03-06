package com.someguyssoftware.gottschcore.config;

import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

/**
 * 
 * @author Mark Gottschling on Nov 14, 2019
 *
 */
public abstract class AbstractConfig implements IConfig {
	protected static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	protected static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

	public static Mod MOD;
	public static Logging LOGGING;

	static {
		MOD = new Mod(COMMON_BUILDER);
		LOGGING = new Logging(COMMON_BUILDER);
	}

	/**
	 * 
	 * @author Mark Gottschling on Nov 16, 2019
	 *
	 */
	public static class Mod {
		public ForgeConfigSpec.BooleanValue enabled;
		public ConfigValue<String> folder;
		public ConfigValue<String> configFolder;
		public ForgeConfigSpec.BooleanValue enableVersionChecker;
		public ForgeConfigSpec.BooleanValue latestVersionReminder;

		/**
		 * 
		 * @param builder
		 */
		public Mod(final ForgeConfigSpec.Builder builder) {
			builder.comment("General mod properties").push(MOD_CATEGORY);
			enabled = builder.comment("Enables/Disables mod.").define("enabled", true);
			folder = builder
					.comment("The directory where mods are located", "This is relative to the Minecraft install path.")
					.define("modFolder", DEFAULT_MODS_FOLDER);
			configFolder = builder
					.comment("The directory where configs are located.", "Resource files will be located here as well.",
							"This is relative to the Minecraft install path.")
					.define("configFolder", DEFAULT_CONFIG_FOLDER);
			enableVersionChecker = builder.comment("Enables/Disables version checking.").define("enableVersionChecker",
					true);

			latestVersionReminder = builder.comment("Remind the user of the latest version update.")
					.define("enableLatestVersionReminder", true);

			builder.pop();
		}
	}

	/**
	 * 
	 * @author Mark Gottschling on Nov 16, 2019
	 *
	 */
	public static class Logging {
		public ConfigValue<String> level;
		public ConfigValue<String> folder;
		public ConfigValue<String> size;
		public ConfigValue<String> filename;

		Logging(final ForgeConfigSpec.Builder builder) {
			builder.comment("Logging properties").push(LOGGING_CATEGORY);

			level = builder.comment("The logging level. Set to 'off' to disable logging.",
					"Values = [trace|debug|info|warn|error|off]").define("level", DEFAULT_LOGGER_LEVEL);
			size = builder.comment("The size a log file can be before rolling over to a new file.").define("size:",
					DEFAULT_LOGGER_SIZE);
			folder = builder.comment("The directory where the logs should be stored.",
					"This is relative to the Minecraft install path.").define("folder", DEFAULT_LOGGER_FOLDER);

			filename = builder.comment("The base filename of the  log file.").define("filename",
					DEFAULT_LOGGER_FILENAME);
			builder.pop();
		}
	}

	/**
	 * 
	 * @param spec
	 * @param path
	 */
	public static void loadConfig(ForgeConfigSpec spec, Path path) {
		final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave()
				.writingMode(WritingMode.REPLACE).build();

		configData.load();
		spec.setConfig(configData);
	}
}
