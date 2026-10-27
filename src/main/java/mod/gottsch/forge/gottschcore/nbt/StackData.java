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

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * 1.21-style item data components on a 1.20.1 stack: {@code ItemStack.get/has/set/remove/getOrDefault} for a
 * {@link TagComponent}, kept in the stack's NBT. A port back from 1.21 turns {@code stack.get(X.get())} into
 * {@code StackData.get(stack, X)} with one search and replace.
 * <p>
 * The stack tag syncs to clients by itself, so unlike an item capability nothing here needs a packet. Same rules as
 * {@link TagData}, which this delegates to; in addition, removing the last value clears the stack's tag, so a stack
 * whose data is all removed stacks with a fresh one again.
 *
 * @author Mark Gottschling on 10/16/2026
 */
public final class StackData {
    private StackData() {}

    public static boolean has(ItemStack stack, TagComponent<?> component) {
        return TagData.has(stack.getTag(), component);
    }

    /** the value, or null when the stack has none or it doesn't decode; never writes to the stack */
    @Nullable
    public static <T> T get(ItemStack stack, TagComponent<T> component) {
        return TagData.get(stack.getTag(), component);
    }

    public static <T> T getOrDefault(ItemStack stack, TagComponent<T> component, T defaultValue) {
        return TagData.getOrDefault(stack.getTag(), component, defaultValue);
    }

    /** sets the value; null removes it, as 1.21's {@code set} does */
    public static <T> void set(ItemStack stack, TagComponent<T> component, @Nullable T value) {
        if (value == null) {
            remove(stack, component);
            return;
        }
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            TagData.set(tag, component, value);
            return;
        }
        // not getOrCreateTag: a value that fails to encode must not leave an empty tag behind
        CompoundTag fresh = new CompoundTag();
        TagData.set(fresh, component, value);
        if (!fresh.isEmpty()) {
            stack.setTag(fresh);
        }
    }

    public static void remove(ItemStack stack, TagComponent<?> component) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            TagData.remove(tag, component);
            if (tag.isEmpty()) {
                stack.setTag(null);
            }
        }
    }
}
