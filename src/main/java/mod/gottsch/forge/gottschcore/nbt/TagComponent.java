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

import com.mojang.serialization.Codec;

/**
 * One typed value kept in NBT: the key it lives under and the codec that reads and writes it. Read and write it with
 * {@link TagData} (any {@code CompoundTag}: a block entity's save data, a structure piece, an entity's persistent data)
 * or {@link StackData} (an item stack, the 1.20.1 stand-in for 1.21's item data components).
 * <p>
 * One type serves both: the key and codec are the same whatever tag holds them, so a separate stack type would add
 * nothing. Declare each as a constant, as you would a 1.21 {@code DataComponentType}:
 * <pre>{@code
 * public static final TagComponent<MyState> MY_STATE = TagComponent.of(MODID, "my_state", MyState.CODEC);
 * }</pre>
 * Using the 1.21 component id as the key ({@code "modid:my_state"}) keeps a mod that ships on both versions naming the
 * same data the same way.
 *
 * @param key   the NBT key the value lives under
 * @param codec reads and writes the value
 * @author Mark Gottschling on 10/16/2026
 */
public record TagComponent<T>(String key, Codec<T> codec) {

    /** a component keyed {@code "modid:name"} */
    public static <T> TagComponent<T> of(String modid, String name, Codec<T> codec) {
        return new TagComponent<>(modid + ":" + name, codec);
    }

    /**
     * Itself. Lets code written against 1.21's {@code DeferredHolder} components, which call {@code X.get()} for the
     * component type, compile unchanged when ported back.
     */
    public TagComponent<T> get() {
        return this;
    }
}
