/*
 * This file is part of  GottschCore.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.gottschcore.util;

import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * 
 * @author Mark Gottschling on Nov 13, 2022
 *
 */
public class LangUtil {
	public static final String NEWLINE = "";
	public static final String INDENT2 = "  ";
	public static final String INDENT4 = "    ";
	
	/**
	 * 
	 * @param tooltip
	 * @param consumer
	 */
	public static void appendAdvancedHoverText(String modid, List<Component> tooltip, Consumer<List<Component>> consumer) {
		if (!Screen.hasShiftDown()) {
			tooltip.add(new TextComponent(NEWLINE));
			// TODO how do make this call to tooltip generic for any mod because it would require the modid
			tooltip.add(new TranslatableComponent(tooltip(modid, "hold_shift")).withStyle(ChatFormatting.GRAY));
			tooltip.add(new TextComponent(LangUtil.NEWLINE));
		}
		else {
			consumer.accept(tooltip);
		}
	}

    public static String name(String modid, String prefix, String suffix) {
    	return StringUtils.stripEnd(prefix.trim(), ".")
    			+ "."
    			+ modid
    			+ "."
    			+ StringUtils.stripStart(suffix.trim(), ".");
    }
    
    public static String item(String modid, String suffix) {
    	return name(modid, "item", suffix);
    }
    
    public static String tooltip(String modid, String suffix) {
    	return name(modid, "tooltip", suffix);
    }
    
    public static String screen(String modid, String suffix) {
    	return name(modid, "screen", suffix);
    }

	public static String chat(String modid, String suffix) {
		return name(modid, "chat", suffix);
	}
}
