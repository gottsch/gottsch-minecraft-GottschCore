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
package mod.gottsch.forge.gottschcore.json;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Codec helpers that refuse to guess.
 *
 * <h2>The hole this closes</h2>
 * <p>DFU's {@code optionalFieldOf(name, default)} <strong>cannot tell "absent" from "present and
 * malformed"</strong>. {@code OptionalFieldCodec} decodes the field, and on <em>any</em> failure
 * returns an empty optional &mdash; so the default is substituted either way. The consequences are
 * not theoretical:</p>
 *
 * <ul>
 *   <li>{@code "probability": "high"} on a decoration rule decodes as {@code 0.0}. The behaviour is
 *       silently off for the whole pack.</li>
 *   <li>One malformed entry anywhere in a {@code blocks} palette empties the <em>entire</em>
 *       palette, because the list codec fails as a unit. The rule then reports itself inactive and
 *       that decoration simply never appears.</li>
 *   <li>A malformed {@code floor_growth} object decodes as {@code NONE}, taking the whole behaviour
 *       with it.</li>
 * </ul>
 *
 * <p>In every case there is no error, no log line, and the datapack loads. The author sees a
 * dungeon that is merely <em>plainer</em> than they authored, which is close to undiagnosable in
 * game &mdash; and it is why "I set the probability and nothing changed" is such a hard report to
 * act on.</p>
 *
 * <h2>What strict means here</h2>
 * <p>Absent is still fine and still yields the default &mdash; that is what makes a field optional,
 * and every existing pack that simply omits a key keeps working. Only <em>present and undecodable</em>
 * changes behaviour: it now fails the file, naming the field. That is the same split Dungeons2 made
 * for its own configs.</p>
 *
 * <p>This is deliberately narrower than a closed schema: an <em>undeclared</em> key is still
 * ignored here. A shared library has consumers whose packs may legitimately carry spare fields, so
 * closing the schema is a decision for the consuming mod's own records rather than for these.</p>
 *
 * @author Mark Gottschling on Aug 19, 2026
 */
public final class StrictCodecs {

    private StrictCodecs() {}

    /**
     * Optional in the sense of "may be omitted", not "may be wrong".
     *
     * @param fallback used when the key is absent, never when it is present and fails to decode
     */
    public static <A> MapCodec<A> strictOptionalFieldOf(Codec<A> codec, String name, A fallback) {
        return new MapCodec<>() {
            @Override
            public <T> DataResult<A> decode(DynamicOps<T> ops, MapLike<T> input) {
                T value = input.get(name);
                if (value == null) {
                    return DataResult.success(fallback);
                }
                // The field name is prepended because a bare DFU message ("Not a number: ...") in a
                // 900-line processor_list is close to useless on its own.
                return codec.parse(ops, value)
                        .mapError(message -> "field '" + name + "': " + message);
            }

            @Override
            public <T> RecordBuilder<T> encode(A input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return prefix.add(name, codec.encodeStart(ops, input));
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.of(ops.createString(name));
            }
        };
    }

    /** The {@link Optional} form; absent stays empty, malformed fails. */
    public static <A> MapCodec<Optional<A>> strictOptionalFieldOf(Codec<A> codec, String name) {
        return new MapCodec<>() {
            @Override
            public <T> DataResult<Optional<A>> decode(DynamicOps<T> ops, MapLike<T> input) {
                T value = input.get(name);
                if (value == null) {
                    return DataResult.success(Optional.empty());
                }
                return codec.parse(ops, value).map(Optional::of)
                        .mapError(message -> "field '" + name + "': " + message);
            }

            @Override
            public <T> RecordBuilder<T> encode(Optional<A> input, DynamicOps<T> ops,
                                               RecordBuilder<T> prefix) {
                return input.isPresent()
                        ? prefix.add(name, codec.encodeStart(ops, input.get()))
                        : prefix;
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.of(ops.createString(name));
            }
        };
    }
}
