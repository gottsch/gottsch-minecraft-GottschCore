package com.someguyssoftware.gottschcore.config;

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

import com.someguyssoftware.gottschcore.mod.IMod;

/**
 * @author Mark Gottschling on Apr 28, 2021
 *
 */
public class IModSetup {
	/**
	 * 
	 * @param mod
	 */
	public static void addRollingFileAppender(IMod mod) {

		String appenderName = mod.getName() + "Appender";
		String logsFolder = mod.getConfig().getLogsFolder();
		if (!logsFolder.endsWith("/")) {
			logsFolder += "/";
		}

		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		final Configuration config = ctx.getConfiguration();

		// create a sized-based trigger policy, using config setting for size.
		SizeBasedTriggeringPolicy policy = SizeBasedTriggeringPolicy.createPolicy(mod.getConfig().getLogSize());
		// create the pattern for log statements
		PatternLayout layout = PatternLayout.newBuilder().withPattern("%d [%t] %p %c | %F:%L | %m%n")
				.withAlwaysWriteExceptions(true).build();

		// create a rolling file appender
		Appender appender = RollingFileAppender.newBuilder()
				.withFileName(Paths.get(logsFolder, mod.getId() + ".log").toString())
				.withFilePattern(Paths.get(logsFolder, mod.getId() + "-%d{yyyy-MM-dd-HH_mm_ss}.log").toString())
				.withAppend(true).setName(appenderName).withBufferedIo(true).withImmediateFlush(true)
				.withPolicy(policy)
				.setLayout(layout)
				.setIgnoreExceptions(true).withAdvertise(false).setConfiguration(config).build();

		appender.start();
		config.addAppender(appender);
		
		// create a appender reference
		AppenderRef ref = AppenderRef.createAppenderRef("File", null, null);
		AppenderRef[] refs = new AppenderRef[] {ref};
		
		LoggerConfig loggerConfig = LoggerConfig.createLogger(false, Level.toLevel(mod.getConfig().getLoggingLevel(), Level.INFO), mod.getName(), "true", refs, null, config, null );
		loggerConfig.addAppender(appender, null, null);
		config.addLogger(mod.getName(), loggerConfig);
		
		// update logger with new appenders
		ctx.updateLoggers();
	}
}
