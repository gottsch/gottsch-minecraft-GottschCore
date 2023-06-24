package mod.gottsch.forge.gottschcore.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

/**
 * 
 * @author Mark Gottschling on Oct 19, 2022
 *
 */
public class Config extends AbstractConfig {
	// setup as a singleton
	public static Config instance = new Config();
	
	private Config() {}
	
	/**
	 * 
	 */
	public static void register() {
		registerCommonConfigs();
		// perform any initializations on data
		Config.init();
	}
	
	public static class CommonConfig {
		public static Logging LOGGING;
	}
	
	/**
	 * 
	 */
	private static void registerCommonConfigs() {
		ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
		CommonConfig.LOGGING = new Logging(COMMON_BUILDER);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_BUILDER.build());
	}
	
	private static void init() {
		
	}
}
