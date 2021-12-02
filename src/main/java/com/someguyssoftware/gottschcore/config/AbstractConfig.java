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

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

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
			builder.comment(CATEGORY_DIV, " Mod properties", CATEGORY_DIV).push(MOD_CATEGORY);
			enabled = builder.comment("Enables/Disables mod.").define("enabled", true);
			folder = builder
					.comment(" The directory where mods are located", "This is relative to the Minecraft install path.")
					.define("modFolder", DEFAULT_MODS_FOLDER);
			configFolder = builder
					.comment(" The directory where configs are located.", "Resource files will be located here as well.",
							" This is relative to the Minecraft install path.")
					.define("configFolder", DEFAULT_CONFIG_FOLDER);
			enableVersionChecker = builder.comment("Enables/Disables version checking.").define("enableVersionChecker",
					true);

			latestVersionReminder = builder.comment(" Remind the user of the latest version update.")
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

		public Logging(final ForgeConfigSpec.Builder builder) {
			builder.comment(CATEGORY_DIV, " Logging properties", CATEGORY_DIV).push(LOGGING_CATEGORY);

			level = builder.comment(" The logging level. Set to 'off' to disable logging.",
					" Values = [trace|debug|info|warn|error|off]").define("level", DEFAULT_LOGGER_LEVEL);
			size = builder.comment(" The size a log file can be before rolling over to a new file.").define("size",
					DEFAULT_LOGGER_SIZE);
			folder = builder.comment(" The directory where the logs should be stored.",
					" This is relative to the Minecraft install path.").define("folder", DEFAULT_LOGGER_FOLDER);

//			filename = builder.comment(" The base filename of the  log file.").define("filename",
//					DEFAULT_LOGGER_FILENAME);
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
	
	/**
	 * @param modName
	 * @param object
	 */
	public void addRollingFileAppender(String modName, IConfig modConfig) {

		String appenderName = modName + "Appender";
		String loggerFolder = Paths.get(modConfig.getLogsFolder()).toString(); //"logs/gottschcore/";
		if (!loggerFolder.endsWith("/")) {
			loggerFolder += "/";
		}

		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		final Configuration config = ctx.getConfiguration();

		// create a sized-based trigger policy, using config setting for size.
		SizeBasedTriggeringPolicy policy = SizeBasedTriggeringPolicy.createPolicy(modConfig.getLoggerSize()/*"1000K"*/);
		// create the pattern for log statements
		PatternLayout layout = PatternLayout.newBuilder().withPattern("%d [%t] %p %c | %F:%L | %m%n")
				.withAlwaysWriteExceptions(true).build();

		// create a rolling file appender
		Appender appender = RollingFileAppender.newBuilder()
				.withFileName(Paths.get(loggerFolder, modName).toString() + /*modConfig.getLoggerFilename()"gottschcore"*/  ".log")
				.withFilePattern(Paths.get(loggerFolder, modName).toString() + /*modConfig.getLoggerFilename()"gottschcore" +*/ "-%d{yyyy-MM-dd-HH_mm_ss}.log")
				.withAppend(true).setName(appenderName).withBufferedIo(true).withImmediateFlush(true)
				.withPolicy(policy)
				.setLayout(layout)
				.setIgnoreExceptions(true).withAdvertise(false).setConfiguration(config).build();

		appender.start();
		config.addAppender(appender);
		
		// create a appender reference
		AppenderRef ref = AppenderRef.createAppenderRef("File", null, null);
		AppenderRef[] refs = new AppenderRef[] {ref};
		
		Level level = Level.getLevel(modConfig.getLoggerLevel().toUpperCase());
		LoggerConfig loggerConfig = LoggerConfig.createLogger(false, level, modName, "true", refs, null, config, null );
		loggerConfig.addAppender(appender, null, null);
		config.addLogger(modName, loggerConfig);
		
		// update logger with new appenders
		ctx.updateLoggers();
	}
}
