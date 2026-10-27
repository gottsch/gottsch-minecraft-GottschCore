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
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link TagData} and {@link StackData}: round trips through NbtOps, and the rules (reads never write, null removes,
 * bad data reads as absent, an emptied stack loses its tag).
 *
 * @author Mark Gottschling on 10/16/2026
 */
class TagDataTest {
    record State(int uses, double mana, List<String> spells) {
        static final Codec<State> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("uses").forGetter(State::uses),
                Codec.DOUBLE.fieldOf("mana").forGetter(State::mana),
                Codec.STRING.listOf().fieldOf("spells").forGetter(State::spells)
        ).apply(instance, State::new));
    }

    static final TagComponent<State> STATE = TagComponent.of("test", "state", State.CODEC);
    static final TagComponent<String> NAME = TagComponent.of("test", "name", Codec.STRING);
    /** a codec that can never encode, for the failure path */
    static final TagComponent<String> BROKEN = TagComponent.of("test", "broken",
            Codec.STRING.flatXmap(DataResult::success, s -> DataResult.error(() -> "no")));

    @BeforeAll
    static void boot() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void ofBuildsNamespacedKey() {
        assertEquals("test:state", STATE.key());
        assertSame(STATE, STATE.get());
    }

    @Test
    void tagRoundTrip() {
        CompoundTag tag = new CompoundTag();
        State state = new State(5, 12.5, List.of("a", "b"));
        TagData.set(tag, STATE, state);
        assertTrue(TagData.has(tag, STATE));
        assertEquals(state, TagData.get(tag, STATE));
        assertTrue(tag.contains("test:state"));
    }

    @Test
    void readsNeverWrite() {
        CompoundTag tag = new CompoundTag();
        assertNull(TagData.get(tag, STATE));
        assertEquals("x", TagData.getOrDefault(tag, NAME, "x"));
        assertTrue(tag.isEmpty());
        assertNull(TagData.get(null, STATE));
        assertFalse(TagData.has(null, STATE));
    }

    @Test
    void nullRemoves() {
        CompoundTag tag = new CompoundTag();
        TagData.set(tag, NAME, "ring");
        TagData.set(tag, NAME, null);
        assertFalse(TagData.has(tag, NAME));
    }

    @Test
    void badDataReadsAsAbsent() {
        CompoundTag tag = new CompoundTag();
        tag.put("test:state", StringTag.valueOf("not a state"));
        assertNull(TagData.get(tag, STATE));
        assertEquals("d", TagData.getOrDefault(tag, NAME, "d"));
    }

    @Test
    void stackRoundTripAndNoTagFromReads() {
        ItemStack stack = new ItemStack(Items.STICK);
        assertNull(StackData.get(stack, STATE));
        assertFalse(stack.hasTag());
        State state = new State(1, 2.0, List.of());
        StackData.set(stack, STATE, state);
        assertEquals(state, StackData.get(stack, STATE));
        assertTrue(StackData.has(stack, STATE));
    }

    @Test
    void stackLosesEmptiedTag() {
        ItemStack stack = new ItemStack(Items.STICK);
        StackData.set(stack, NAME, "ring");
        StackData.remove(stack, NAME);
        assertFalse(stack.hasTag());
        // stacks with a fresh one again
        assertTrue(ItemStack.isSameItemSameTags(stack, new ItemStack(Items.STICK)));
    }

    @Test
    void stackKeepsOtherData() {
        ItemStack stack = new ItemStack(Items.STICK);
        stack.getOrCreateTag().putInt("Damage", 3);
        StackData.set(stack, NAME, "ring");
        StackData.remove(stack, NAME);
        assertTrue(stack.hasTag());
        assertEquals(3, stack.getTag().getInt("Damage"));
    }

    @Test
    void failedEncodeLeavesNoTag() {
        ItemStack stack = new ItemStack(Items.STICK);
        StackData.set(stack, BROKEN, "x");
        assertFalse(stack.hasTag());
    }
}
