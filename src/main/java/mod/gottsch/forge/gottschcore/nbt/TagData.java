/*
 * This file is part of  GottschCore.
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
package mod.gottsch.forge.gottschcore.nbt;

import mod.gottsch.forge.gottschcore.GottschCore;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import javax.annotation.Nullable;

/**
 * Codec-backed fields in any {@code CompoundTag}: {@code get}, {@code has}, {@code set}, {@code remove} and
 * {@code getOrDefault} for a {@link TagComponent}, written and read through its codec with {@code NbtOps}. For a block
 * entity's {@code saveAdditional}/{@code load}, a structure piece's tag, an entity's persistent data, or anything else
 * that hands you a compound. {@link StackData} is the same thing for item stacks.
 * <p>
 * The rules:
 * <ul>
 *     <li>reads never write: {@link #get} and {@link #getOrDefault} leave the tag alone, so they are safe from
 *     rendering and tooltip code;</li>
 *     <li>a value that won't decode reads as absent and logs a warning naming the key; one that won't encode is not
 *     written, also with a warning;</li>
 *     <li>setting null removes the value.</li>
 * </ul>
 * Reads accept a null tag (as from {@code ItemStack.getTag()}) and treat it as empty.
 *
 * @author Mark Gottschling on 10/16/2026
 */
public final class TagData {
    private TagData() {}

    public static boolean has(@Nullable CompoundTag tag, TagComponent<?> component) {
        return tag != null && tag.contains(component.key());
    }

    /** the value, or null when the tag has none or it doesn't decode */
    @Nullable
    public static <T> T get(@Nullable CompoundTag tag, TagComponent<T> component) {
        if (tag == null || !tag.contains(component.key())) {
            return null;
        }
        return component.codec().parse(NbtOps.INSTANCE, tag.get(component.key()))
                .resultOrPartial(error -> GottschCore.LOGGER.warn("can't read {}: {}", component.key(), error))
                .orElse(null);
    }

    public static <T> T getOrDefault(@Nullable CompoundTag tag, TagComponent<T> component, T defaultValue) {
        T value = get(tag, component);
        return value != null ? value : defaultValue;
    }

    /** writes the value under the component's key; null removes it */
    public static <T> void set(CompoundTag tag, TagComponent<T> component, @Nullable T value) {
        if (value == null) {
            remove(tag, component);
            return;
        }
        component.codec().encodeStart(NbtOps.INSTANCE, value)
                .resultOrPartial(error -> GottschCore.LOGGER.warn("can't write {}: {}", component.key(), error))
                .ifPresent((Tag encoded) -> tag.put(component.key(), encoded));
    }

    public static void remove(@Nullable CompoundTag tag, TagComponent<?> component) {
        if (tag != null) {
            tag.remove(component.key());
        }
    }
}
