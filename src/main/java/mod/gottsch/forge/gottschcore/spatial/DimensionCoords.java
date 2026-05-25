/*
 * This file is part of GottschCore.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.gottschcore.spatial;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * An immutable x/y/z coordinate that also carries a dimension key.
 * Extends Coords so it works anywhere ICoords is expected, and adds
 * save()/load() support for the dimension field.
 *
 * @author Mark Gottschling on May 11, 2026
 */
@Immutable
public class DimensionCoords extends Coords {

    public static final DimensionCoords EMPTY = new DimensionCoords(Level.OVERWORLD, 0, -255, 0);

    private final ResourceKey<Level> dimension;

    @SuppressWarnings("deprecation")
    public DimensionCoords(ResourceKey<Level> dimension, int x, int y, int z) {
        super(x, y, z);
        this.dimension = dimension;
    }

    public static DimensionCoords of(ResourceKey<Level> dimension, int x, int y, int z) {
        return new DimensionCoords(dimension, x, y, z);
    }

    public static DimensionCoords of(ResourceKey<Level> dimension, BlockPos pos) {
        return new DimensionCoords(dimension, pos.getX(), pos.getY(), pos.getZ());
    }

    public static DimensionCoords of(ResourceKey<Level> dimension, ICoords coords) {
        return new DimensionCoords(dimension, coords.getX(), coords.getY(), coords.getZ());
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        tag.putString("dimension", dimension.location().toString());
        return tag;
    }

    /**
     * Returns a new DimensionCoords loaded from the tag.
     * Defaults to the overworld if no "dimension" key is present (backward compatibility).
     */
    @Override
    public ICoords load(CompoundTag tag) {
        ICoords base = super.load(tag);
        ResourceKey<Level> dim = Level.OVERWORLD;
        if (tag.contains("dimension")) {
            dim = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(tag.getString("dimension")));
        }
        return new DimensionCoords(dim, base.getX(), base.getY(), base.getZ());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DimensionCoords other = (DimensionCoords) obj;
        return getX() == other.getX()
                && getY() == other.getY()
                && getZ() == other.getZ()
                && Objects.equals(dimension, other.dimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getZ(), dimension);
    }

    @Override
    public String toString() {
        return "DimensionCoords [dimension=" + dimension.location()
                + ", x=" + getX() + ", y=" + getY() + ", z=" + getZ() + "]";
    }

    @Override
    public String toShortString() {
        return dimension.location() + " " + getX() + " " + getY() + " " + getZ();
    }
}
