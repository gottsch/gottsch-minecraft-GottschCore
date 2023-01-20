/*
 * This file is part of  GottschCore.
 * Copyright (c) 2022 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.gottschcore.config;

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

/**
 * 
 * @author Mark Gottschling on Oct 19, 2022
 *
 */
public interface IConfig {
	public static final String LOGGING_CATEGORY = "logging";
	public static final String CATEGORY_DIV = "##############################";

	public static final String DEFAULT_LOGGER_FOLDER = "logs";
	public static final String DEFAULT_LOGGER_LEVEL = "info";
	public static final String DEFAULT_LOGGER_SIZE = "1000K";
	
	/**
	 * @param mod
	 */
	default public void addRollingFileAppender(String modid) {

		String appenderName = modid + "Appender";
		String logsFolder = this.getLogsFolder();
		if (!logsFolder.endsWith("/")) {
			logsFolder += "/";
		}

		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		final Configuration configuration = ctx.getConfiguration();

		// create a sized-based trigger policy, using config setting for size.
		SizeBasedTriggeringPolicy policy = SizeBasedTriggeringPolicy.createPolicy(this.getLogSize());
		// create the pattern for log statements
		PatternLayout layout = PatternLayout.newBuilder().withPattern("%d [%t] %p %c | %F:%L | %m%n")
				.withAlwaysWriteExceptions(true).build();

		// create a rolling file appender
		Appender appender = RollingFileAppender.newBuilder()
				.withFileName(Paths.get(logsFolder, modid + ".log").toString())
				.withFilePattern(Paths.get(logsFolder, modid + "-%d{yyyy-MM-dd-HH_mm_ss}.log").toString())
				.withAppend(true).setName(appenderName).withBufferedIo(true).withImmediateFlush(true)
				.withPolicy(policy)
				.setLayout(layout)
				.setIgnoreExceptions(true).withAdvertise(false).setConfiguration(configuration).build();

		appender.start();
		configuration.addAppender(appender);
		
		// create a appender reference
		AppenderRef ref = AppenderRef.createAppenderRef("File", null, null);
		AppenderRef[] refs = new AppenderRef[] {ref};
		
		LoggerConfig loggerConfig = LoggerConfig.createLogger(false, Level.toLevel(this.getLoggingLevel(), Level.INFO), modid, "true", refs, null, configuration, null );
		loggerConfig.addAppender(appender, null, null);
		configuration.addLogger(modid, loggerConfig);
		
		// update logger with new appenders
		ctx.updateLoggers();
	}
	
	/**
	 * 
	 * @return
	 */
	default public String getLogsFolder() {
		return DEFAULT_LOGGER_FOLDER;
	}
	
	default public String getLogSize() {
		return DEFAULT_LOGGER_SIZE;
	}
	
	default public String getLoggingLevel() {
		return DEFAULT_LOGGER_SIZE;
	}
}
