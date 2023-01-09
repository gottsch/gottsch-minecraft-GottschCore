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

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

/**
 * 
 * @author Mark Gottschling on Oct 19, 2022
 *
 */
public abstract class AbstractConfig implements IConfig {

	/*
	 * 
	 */
	public static class Logging {
		public ConfigValue<String> level;
		public ConfigValue<String> folder;
		public ConfigValue<String> size;

		public Logging(final ForgeConfigSpec.Builder builder) {
			builder.comment(CATEGORY_DIV, " Logging properties", CATEGORY_DIV).push(LOGGING_CATEGORY);

			level = builder
					.comment(
							" The logging level. Set to 'off' to disable logging.",
							" Values = [trace|debug|info|warn|error|off]")
					.define("level", DEFAULT_LOGGER_LEVEL);
			
			size = builder
					.comment(" The size a log file can be before rolling over to a new file.")
					.define("size", DEFAULT_LOGGER_SIZE);
			
			folder = builder
					.comment(
							" The directory where the logs should be stored.",
							" This is relative to the Minecraft install path.")
					.define("folder", DEFAULT_LOGGER_FOLDER);

			builder.pop();
		}
	}
}
