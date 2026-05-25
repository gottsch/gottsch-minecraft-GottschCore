/*
 * This file is part of GottschCore.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.gottschcore.command;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;

/**
 * Shared display constants, style helpers, and interactive component builders
 * used by {@link CommandResponseFormatter} and dependent mods.
 *
 * @author Mark Gottschling on May 11, 2026
 */
public final class FormatterConstants {

    // ===== BOX-DRAWING STRINGS =====

    /** Double-line horizontal bar (U+2550). Used as the outer title separator. */
    public static final String TITLE_BAR   = "══════════════════════════════";
    /** Single-line horizontal bar (U+2500). Used as section sub-headers. */
    public static final String SECTION_BAR = "──────────────────────────────";
    public static final String BRANCH      = "├─ ";
    public static final String LAST_BRANCH = "└─ ";
    public static final String VERTICAL    = "│  ";

    /** Empty string that renders as a blank line in Minecraft chat. */
    public static final String NEWLINE = "";

    // ===== SEMANTIC ICON COMPONENTS =====

    public static final Component ICON_SUCCESS = bold("✔", ChatFormatting.GREEN);
    public static final Component ICON_FAILURE = bold("✘", ChatFormatting.RED);
    public static final Component ICON_WARNING = bold("⚠", ChatFormatting.YELLOW);
    public static final Component ICON_INFO    = bold("ℹ", ChatFormatting.AQUA);

    private FormatterConstants() {}

    // ===== NEWLINE =====

    public static Component newline() {
        return Component.literal(NEWLINE);
    }

    // ===== STYLE HELPERS =====

    public static Component bold(String text, ChatFormatting color) {
        return Component.literal(text).withStyle(color, ChatFormatting.BOLD);
    }

    public static Component grey(String text) {
        return Component.literal(text).withStyle(ChatFormatting.GRAY);
    }

    public static Component aqua(String text) {
        return Component.literal(text).withStyle(ChatFormatting.AQUA);
    }

    public static Component gold(String text) {
        return Component.literal(text).withStyle(ChatFormatting.GOLD);
    }

    // ===== LOCATION FORMATTING =====

    /** Returns a gold-coloured "x, y, z" component from a BlockPos. */
    public static Component formatLocation(BlockPos pos) {
        return gold(pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
    }

    // ===== INTERACTIVE (CLICK / HOVER) BUILDERS =====

    /**
     * A text component that runs {@code command} on click and shows {@code hover} on hover.
     */
    public static Component clickable(String text, String command, Component hover) {
        return Component.literal(text).withStyle(Style.EMPTY
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover)));
    }

    /**
     * A text component that fills the chat bar with {@code command} on click and shows
     * {@code hover} on hover.
     */
    public static Component suggestable(String text, String command, Component hover) {
        return Component.literal(text).withStyle(Style.EMPTY
                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover)));
    }

    /**
     * Wraps an existing icon component with a run-command click event and hover tooltip.
     * The icon's text and colour are preserved; only interaction style is added.
     */
    public static Component clickableIcon(Component icon, String command, Component hover) {
        return icon.copy().withStyle(Style.EMPTY
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover)));
    }

    /**
     * A bold green {@code [✔ Confirm]} button that runs {@code command} on click.
     * Append this to the detail line of a {@link CommandResponseFormatter#formatConfirmPrompt} message.
     */
    public static Component buildConfirmButton(String command) {
        return Component.literal(" [✔ Confirm]").withStyle(Style.EMPTY
                .withColor(ChatFormatting.GREEN)
                .withBold(true)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.literal("Confirm — this cannot be undone"))));
    }
}
