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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

import static mod.gottsch.forge.gottschcore.command.FormatterConstants.*;

/**
 * Shared command/message formatting utility for all gottsch mods.
 *
 * <h3>Status messages</h3>
 * Short feedback returned after a command runs. Two shapes:
 * <ul>
 *   <li><b>Shorthand</b> — title only, for self-explanatory outcomes.
 *   <li><b>Full</b> — title + grey body, for outcomes that need context.
 * </ul>
 * All methods return {@code List<Component>} so both
 * {@code CommandSourceStack.sendSuccess()} and {@code player.sendSystemMessage()}
 * can consume the result.
 *
 * <h3>Structured reports</h3>
 * Use {@link #report(String)} to get a {@link ReportBuilder} for admin/debug
 * commands like {@code inspect} or {@code simulate}.
 *
 * <h3>Entry point families</h3>
 * <ul>
 *   <li><b>Component-based</b> ({@code formatSuccess(Component title)}, etc.) — caller
 *       controls translation. Preferred for raw-string admin output.
 *   <li><b>Lang-key-based</b> ({@code formatSuccessKey(String langKey, ...)}, etc.) — caller
 *       passes the full translation key. Preferred for player-facing localised messages.
 * </ul>
 *
 * @author Mark Gottschling on May 11, 2026
 */
public final class CommandResponseFormatter {

    private CommandResponseFormatter() {}

    // =========================================================================
    // Component-based — caller controls translation
    // =========================================================================

    /** Green ✔ — shorthand (title only). */
    public static List<Component> formatSuccess(Component title) {
        return buildShorthand(ChatFormatting.GREEN, "✔", title);
    }

    /** Green ✔ SUCCESS — full (title + grey body). */
    public static List<Component> formatSuccess(Component title, Component body) {
        return buildFull(ChatFormatting.GREEN, "✔", "SUCCESS", title, body);
    }

    /** Alias for {@link #formatSuccess(Component, Component)}. */
    public static List<Component> formatSuccessWithDetail(Component title, Component detail) {
        return formatSuccess(title, detail);
    }

    /** Red ✘ — shorthand. */
    public static List<Component> formatFailure(Component title) {
        return buildShorthand(ChatFormatting.RED, "✘", title);
    }

    /** Red ✘ ERROR — full. */
    public static List<Component> formatFailure(Component title, Component body) {
        return buildFull(ChatFormatting.RED, "✘", "ERROR", title, body);
    }

    /**
     * Red ✘ ERROR — with a bulleted list of reasons.
     * <pre>
     * ✘ ERROR
     * ══════════════════════════════
     *
     * Title
     *
     * ├─ reason 1
     * └─ reason 2
     * </pre>
     */
    public static List<Component> formatFailureWithReasons(Component title, List<Component> reasons) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal("✘ ERROR").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        lines.add(Component.literal(TITLE_BAR).withStyle(ChatFormatting.RED));
        lines.add(newline());
        lines.add(title.copy().withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
        lines.add(newline());
        for (int i = 0; i < reasons.size(); i++) {
            String branch = (i == reasons.size() - 1) ? LAST_BRANCH : BRANCH;
            lines.add(Component.literal(branch).withStyle(ChatFormatting.GRAY)
                    .append(reasons.get(i).copy().withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC)));
        }
        lines.add(newline());
        return lines;
    }

    /** Yellow ⚠ — shorthand. */
    public static List<Component> formatWarning(Component title) {
        return buildShorthand(ChatFormatting.YELLOW, "⚠", title);
    }

    /** Yellow ⚠ WARNING — full. */
    public static List<Component> formatWarning(Component title, Component body) {
        return buildFull(ChatFormatting.YELLOW, "⚠", "WARNING", title, body);
    }

    /** Aqua ℹ — shorthand. */
    public static List<Component> formatInfo(Component title) {
        return buildShorthand(ChatFormatting.AQUA, "ℹ", title);
    }

    /** Aqua ℹ INFO — full. */
    public static List<Component> formatInfo(Component title, Component body) {
        return buildFull(ChatFormatting.AQUA, "ℹ", "INFO", title, body);
    }

    // =========================================================================
    // Lang-key-based — caller passes full translation key
    // =========================================================================

    /** Green ✔ — shorthand, translation-key variant. */
    public static List<Component> formatSuccessKey(String langKey, Object... args) {
        return buildShorthandKey(ChatFormatting.GREEN, "✔", langKey, args);
    }

    /** Green ✔ SUCCESS — full, translation-key variant. */
    public static List<Component> formatSuccessKey(String titleKey, String bodyKey, Object... bodyArgs) {
        return buildFullKey(ChatFormatting.GREEN, "✔", "SUCCESS", titleKey, bodyKey, bodyArgs);
    }

    /** Red ✘ — shorthand, translation-key variant. */
    public static List<Component> formatFailureKey(String langKey, Object... args) {
        return buildShorthandKey(ChatFormatting.RED, "✘", langKey, args);
    }

    /** Red ✘ ERROR — full, translation-key variant. */
    public static List<Component> formatFailureKey(String titleKey, String bodyKey, Object... bodyArgs) {
        return buildFullKey(ChatFormatting.RED, "✘", "ERROR", titleKey, bodyKey, bodyArgs);
    }

    /** Yellow ⚠ — shorthand, translation-key variant. */
    public static List<Component> formatWarningKey(String langKey, Object... args) {
        return buildShorthandKey(ChatFormatting.YELLOW, "⚠", langKey, args);
    }

    /** Yellow ⚠ WARNING — full, translation-key variant. */
    public static List<Component> formatWarningKey(String titleKey, String bodyKey, Object... bodyArgs) {
        return buildFullKey(ChatFormatting.YELLOW, "⚠", "WARNING", titleKey, bodyKey, bodyArgs);
    }

    /** Aqua ℹ — shorthand, translation-key variant. */
    public static List<Component> formatInfoKey(String langKey, Object... args) {
        return buildShorthandKey(ChatFormatting.AQUA, "ℹ", langKey, args);
    }

    /** Aqua ℹ INFO — full, translation-key variant. */
    public static List<Component> formatInfoKey(String titleKey, String bodyKey, Object... bodyArgs) {
        return buildFullKey(ChatFormatting.AQUA, "ℹ", "INFO", titleKey, bodyKey, bodyArgs);
    }

    // =========================================================================
    // Structured report builder
    // =========================================================================

    /**
     * Returns a {@link ReportBuilder} pre-seeded with the given header title.
     * Call {@link ReportBuilder#build()} to get the final list of lines.
     */
    public static ReportBuilder report(String title) {
        return new ReportBuilder(title);
    }

    /**
     * ⚠ WARNING prompt for destructive commands that require a second confirmation step.
     *
     * <pre>
     * ⚠ WARNING
     * ══════════════════════════════
     *
     * action (bold white)
     *
     * detail (grey)  [✔ Confirm]
     * </pre>
     *
     * Build {@code confirmButton} with {@link FormatterConstants#buildConfirmButton(String)}.
     */
    public static List<Component> formatConfirmPrompt(Component action, Component detail, Component confirmButton) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal("⚠ WARNING").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
        lines.add(Component.literal(TITLE_BAR).withStyle(ChatFormatting.YELLOW));
        lines.add(newline());
        lines.add(action.copy().withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
        lines.add(newline());
        lines.add(detail.copy().withStyle(ChatFormatting.GRAY).append(confirmButton));
        lines.add(newline());
        return lines;
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    private static List<Component> buildShorthand(ChatFormatting color, String icon, Component title) {
        List<Component> lines = new ArrayList<>();
        MutableComponent titleLine = Component.literal(icon + " ").withStyle(color, ChatFormatting.BOLD)
                .append(title.copy().withStyle(color, ChatFormatting.BOLD));
        lines.add(titleLine);
        lines.add(Component.literal(TITLE_BAR).withStyle(color));
        lines.add(newline());
        return lines;
    }

    private static List<Component> buildFull(
            ChatFormatting color, String icon, String label, Component title, Component body) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal(icon + " " + label).withStyle(color, ChatFormatting.BOLD));
        lines.add(Component.literal(TITLE_BAR).withStyle(color));
        lines.add(newline());
        lines.add(title.copy().withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
        lines.add(newline());
        lines.add(body.copy().withStyle(ChatFormatting.GRAY));
        lines.add(newline());
        return lines;
    }

    private static List<Component> buildShorthandKey(ChatFormatting color, String icon, String langKey, Object[] args) {
        List<Component> lines = new ArrayList<>();
        MutableComponent title = Component.translatable(langKey, args).withStyle(color, ChatFormatting.BOLD);
        lines.add(Component.literal(icon + " ").withStyle(color, ChatFormatting.BOLD).append(title));
        lines.add(Component.literal(TITLE_BAR).withStyle(color));
        lines.add(newline());
        return lines;
    }

    private static List<Component> buildFullKey(
            ChatFormatting color, String icon, String label,
            String titleKey, String bodyKey, Object[] bodyArgs) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal(icon + " " + label).withStyle(color, ChatFormatting.BOLD));
        lines.add(Component.literal(TITLE_BAR).withStyle(color));
        lines.add(newline());
        lines.add(Component.translatable(titleKey).withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
        lines.add(newline());
        lines.add(Component.translatable(bodyKey, bodyArgs).withStyle(ChatFormatting.GRAY));
        lines.add(newline());
        return lines;
    }
}
