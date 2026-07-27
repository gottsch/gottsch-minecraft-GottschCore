/*
 * This file is part of Treasure2.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
 *
 * Treasure2 is free software: you can redistribute it and/or modify
 * it under the terms of the Open Software Licence 3.0.
 *
 * Treasure2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Open Software Licence 3.0 for more details.
 *
 * You should have received a copy of the Open Software Licence
 * along with Treasure2. If not, see <https://www.tldrlegal.com/license/open-software-licence-3-0>.
 */
package mod.gottsch.forge.gottschcore.block.entity;

import mod.gottsch.forge.gottschcore.GottschCore;
import mod.gottsch.forge.gottschcore.util.ModUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * this class is meant to be used in Structure NBTs only as a marker
 * where a block extending StructureMobSetBlock block will be placed.
 * @author by Mark Gottschling on 8/14/2025
 */
public abstract class StructureMobSetBlockEntity extends BlockEntity {
    public static final String MOBSET = "mobSet";
    public static final String MOBSETS = "mobSets";
    public static final String PROXIMITY = "proximity";
    public static final String TARGET_BLOCK = "targetBlock";

    private ResourceLocation mobSet;
    private List<ResourceLocation> mobSets;
    private int proximity;
    private ResourceLocation targetBlock;

    public StructureMobSetBlockEntity(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        try {
            super.load(tag);
            try {
                // read the custom name
                if (tag.contains(MOBSET)) {
                    this.mobSet = ModUtil.asLocation(tag.getString(MOBSET)).orElse(null);
                }
                if (tag.contains(MOBSETS, Tag.TAG_LIST)) {
                    ListTag listTag = tag.getList(MOBSETS, Tag.TAG_STRING);
                    listTag.stream()
                            .map(Tag::getAsString)
                            .filter(str -> !str.isEmpty())
                            .map(ModUtil::asLocation)
                            .forEach(location -> location.ifPresent(this.getMobSets()::add));
                }

                if (tag.contains(PROXIMITY)) {
                    this.proximity = tag.getInt(PROXIMITY);
                }
                if (tag.contains(TARGET_BLOCK)) {
                    this.targetBlock = ModUtil.asLocation(tag.getString(TARGET_BLOCK)).orElse(null);
                }
            } catch (Exception e) {
                GottschCore.LOGGER.error("error reading StructureMobSetBlockEntity properties from tag:", e);
            }
        } catch(Exception e) {
            GottschCore.LOGGER.error(e);
            throw e;
        }
    }

    /**
     *
     */
    @Override
    public void saveAdditional(CompoundTag nbt) {
        try {
            super.saveAdditional(nbt);

            Optional.ofNullable(getMobSet())
                    .ifPresent(mobSet -> nbt.putString(MOBSET, getMobSet().toString()));

            if (getMobSets() != null && !getMobSets().isEmpty()) {
                ListTag list = new ListTag();
                getMobSets().forEach(name -> list.add(StringTag.valueOf(name.toString())));
                nbt.put(MOBSETS, list);
            }

            nbt.putInt(PROXIMITY, this.proximity);

            Optional.ofNullable(getTargetBlock())
                    .ifPresent(block -> nbt.putString(TARGET_BLOCK, block.toString()));

        } catch(Exception e) {
            GottschCore.LOGGER.error(e);
            throw e;
        }
    }

    public ResourceLocation getMobSet() {
        return mobSet;
    }

    public void setMobSet(ResourceLocation mobSet) {
        this.mobSet = mobSet;
    }

    public List<ResourceLocation> getMobSets() {
        return mobSets != null ? mobSets : (mobSets = new ArrayList<>());
    }

    public void setMobSets(List<ResourceLocation> mobSets) {
        this.mobSets = mobSets;
    }

    public int getProximity() {
        return proximity;
    }

    public void setProximity(int proximity) {
        this.proximity = proximity;
    }

    public ResourceLocation getTargetBlock() {
        return targetBlock;
    }

    public void setTargetBlock(ResourceLocation targetBlock) {
        this.targetBlock = targetBlock;
    }
}
