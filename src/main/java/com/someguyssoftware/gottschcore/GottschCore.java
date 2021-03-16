package com.someguyssoftware.gottschcore;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.someguyssoftware.gottschcore.annotation.Credits;
import com.someguyssoftware.gottschcore.annotation.ModInfo;
import com.someguyssoftware.gottschcore.config.GottschCoreConfig;
import com.someguyssoftware.gottschcore.config.IConfig;
import com.someguyssoftware.gottschcore.mod.IMod;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * 
 * @author Mark Gottschling on Feb 26, 2020
 *
 */
@Mod(value = GottschCore.MODID)
@ModInfo(modid = GottschCore.MODID, name = GottschCore.NAME, version = GottschCore.VERSION, minecraftVersion = "1.15.2", forgeVersion = "31.2.0", updateJsonUrl = "https://raw.githubusercontent.com/gottsch/gottsch-minecraft-GottschCore/1.15.2-master/update.json")
@Credits(values = { "GottschCore for Minecraft 1.12+ was first developed by Mark Gottschling on Jul 13, 2017." })
public class GottschCore implements IMod {
	// logger
	public static final Logger LOGGER = LogManager.getLogger(GottschCore.class.getSimpleName());

	// constants
	public static final String MODID = "gottschcore";
	protected static final String NAME = "GottschCore";
	protected static final String VERSION = "1.0.0";

	public static GottschCore instance;
	private static GottschCoreConfig config;

	public GottschCore() {
		GottschCore.instance = this;
		GottschCore.config = new GottschCoreConfig(this);

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, GottschCoreConfig.COMMON_CONFIG);

		// Register the setup method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

		GottschCoreConfig.loadConfig(GottschCoreConfig.COMMON_CONFIG,
				FMLPaths.CONFIGDIR.get().resolve("gottschcore-common.toml"));

		// TODO research overridding the log4j.json/xml to see if custom logging can be
		// added
	}

	/**
	 * ie. preint
	 * 
	 * @param event
	 */
	private void setup(final FMLCommonSetupEvent event) {
		// TODO create logging
		addRollingFileAppender(GottschCore.NAME, null);
	}

	@Override
	public IMod getInstance() {
		return GottschCore.instance;
	}

	@Override
	public String getId() {
		return GottschCore.MODID;
	}

	@Override
	public IConfig getConfig() {
		return GottschCore.config;
	}
	
	/**
	 * TODO need the ILoggerConfig or just IConfig
	 * @param modName
	 * @param object
	 */
	public static void addRollingFileAppender(String modName, Object object) {

		String appenderName = modName + "Appender";
		String loggerFolder = "logs/gottschcore/";
		if (!loggerFolder.endsWith("/")) {
			loggerFolder += "/";
		}

		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		final Configuration config = ctx.getConfiguration();

		// create a sized-based trigger policy, using config setting for size.
		SizeBasedTriggeringPolicy policy = SizeBasedTriggeringPolicy.createPolicy(/*modConfig.getLoggerSize()*/"1000K");
		// create the pattern for log statements
		PatternLayout layout = PatternLayout.newBuilder().withPattern("%d [%t] %p %c | %F:%L | %m%n")
				.withAlwaysWriteExceptions(true).build();

		// create a rolling file appender
		Appender appender = RollingFileAppender.newBuilder()
				.withFileName(loggerFolder + /*modConfig.getLoggerFilename()*/"gottschcore" + ".log")
				.withFilePattern(loggerFolder + /*modConfig.getLoggerFilename()*/"gottschcore" + "-%d{yyyy-MM-dd-HH_mm_ss}.log")
				.withAppend(true).setName(appenderName).withBufferedIo(true).withImmediateFlush(true)
				.withPolicy(policy)
				.setLayout(layout)
				.setIgnoreExceptions(true).withAdvertise(false).setConfiguration(config).build();

		appender.start();
		config.addAppender(appender);
		
		// create a appender reference
		AppenderRef ref = AppenderRef.createAppenderRef("File", null, null);
		AppenderRef[] refs = new AppenderRef[] {ref};
		
		LoggerConfig loggerConfig = LoggerConfig.createLogger(false, Level.DEBUG, modName, "true", refs, null, config, null );
		loggerConfig.addAppender(appender, null, null);
		config.addLogger(modName, loggerConfig);
		
		// update logger with new appenders
		ctx.updateLoggers();
	}
}
