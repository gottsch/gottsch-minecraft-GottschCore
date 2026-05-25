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
 * Fluent builder for structured admin/debug reports sent to Minecraft chat.
 *
 * <pre>
 * List&lt;Component&gt; lines = CommandResponseFormatter.report("Crop Inspect")
 *     .row("Position", pos.toShortString())
 *     .row("Age", age)
 *     .section("Timing")
 *         .row("lastCallGameTime", state.getLastCallGameTime())
 *     .build();
 * </pre>
 *
 * Label-column width is computed globally from the longest label across all rows.
 *
 * @author Mark Gottschling on May 11, 2026
 */
public class ReportBuilder {

    private final String title;
    private final List<Row> rows = new ArrayList<>();
    private int maxLabelLen = 0;

    ReportBuilder(String title) {
        this.title = title;
    }

    /** Adds a key/value row where the value is formatted via {@code String.valueOf(value)}. */
    public ReportBuilder row(String label, Object value) {
        rows.add(new Row(label, Component.literal(String.valueOf(value)).withStyle(ChatFormatting.WHITE)));
        if (label.length() > maxLabelLen) maxLabelLen = label.length();
        return this;
    }

    /** Adds a key/value row with a pre-styled value component. */
    public ReportBuilder row(String label, Component value) {
        rows.add(new Row(label, value));
        if (label.length() > maxLabelLen) maxLabelLen = label.length();
        return this;
    }

    /** Adds a grey {@code ─── name ───} section sub-header. */
    public ReportBuilder section(String name) {
        rows.add(new Row(null, Component.literal("─── " + name + " ───").withStyle(ChatFormatting.GRAY)));
        return this;
    }

    /** Adds a blank line. */
    public ReportBuilder blank() {
        rows.add(new Row(null, Component.literal(NEWLINE)));
        return this;
    }

    /** Adds a grey italic free-form note line. */
    public ReportBuilder note(String text) {
        rows.add(new Row(null, Component.literal(text).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)));
        return this;
    }

    /**
     * Builds and returns the complete list of {@link Component} lines.
     * The result starts with a {@code ═══ title ═══} header and ends with
     * a {@code ══} footer bar.
     */
    public List<Component> build() {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal("═══ " + title + " ═══").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD));
        for (Row row : rows) {
            if (row.label() == null) {
                lines.add(row.value());
            } else {
                String padded = String.format("  %-" + maxLabelLen + "s : ", row.label());
                MutableComponent line = Component.literal(padded).withStyle(ChatFormatting.GRAY)
                        .append(row.value());
                lines.add(line);
            }
        }
        lines.add(Component.literal(TITLE_BAR).withStyle(ChatFormatting.YELLOW));
        return lines;
    }

    private record Row(String label, Component value) {}
}
