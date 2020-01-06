/**
 * 
 */
package com.someguyssoftware.gottschcore.config;

import java.nio.file.Path;

import org.apache.commons.lang3.tuple.Pair;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.someguyssoftware.gottschcore.GottschCore;
import com.someguyssoftware.gottschcore.mod.IMod;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

/**
 * @author Mark Gottschling on Nov 15, 2019
 *
 */
@Mod.EventBusSubscriber
public class GottschCoreConfig extends AbstractConfig {
    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    static {
        COMMON_CONFIG = COMMON_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
    
    /**
     * 
     * @param mod
     */
    public GottschCoreConfig(IMod mod) {
    }
    
//    public static class Mod {
//    	public ConfigValue<String> folder;
//    	@Deprecated
//    	public ForgeConfigSpec.BooleanValue enabled;
//    	public ForgeConfigSpec.BooleanValue enableVersionChecker;
//    	public ConfigValue<String> latestVersion;
//    	public ForgeConfigSpec.BooleanValue latestVersionReminder;
//    	
//	    public Mod(final ForgeConfigSpec.Builder builder) {
//			builder
//			.comment("General mod properties")
//			.push(MOD_CATEGORY);    	
//			
//		    folder = builder.comment("The relative path to the mods folder.").define("folder", "mods");
//		    enabled = builder.comment("").define("enabled", true);
//		    enableVersionChecker = builder.comment("").define("enableVersionChecker", true);
//		    // TODO could get the annotation @Version here if null;
//		    latestVersion = builder.comment("").define("latestVersion", "");
//		    latestVersionReminder = builder.comment("").define("latestVersionReminder", true);
//	    }	 
//    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {

    }

    @SubscribeEvent
    public static void onReload(final ModConfig.ConfigReloading configEvent) {
    }
}
