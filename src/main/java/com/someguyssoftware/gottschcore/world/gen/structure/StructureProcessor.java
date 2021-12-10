/*
 * This file is part of  GottschCore.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
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
package com.someguyssoftware.gottschcore.world.gen.structure;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public abstract class StructureProcessor {
   @Nullable
   @Deprecated
   public GottschTemplate.BlockInfo process(LevelAccessor worldReaderIn, BlockPos pos, GottschTemplate.BlockInfo p_215194_3_, GottschTemplate.BlockInfo blockInfo, PlacementSettings placementSettingsIn) {
      return blockInfo;
   }

   /**
    * FORGE: Add template parameter
    * 
    * @param worldReaderIn
    * @param pos
    * @param p_215194_3_
    * @param blockInfo
    * @param placementSettingsIn
    * @param template The template being placed, can be null due to deprecated
    *                 method calls.
    * @see #process(IWorldReader, BlockPos,
    *      net.minecraft.world.gen.feature.template.Template.BlockInfo,
    *      net.minecraft.world.gen.feature.template.Template.BlockInfo,
    *      PlacementSettings)
    */
   @Nullable
   public GottschTemplate.BlockInfo process(LevelAccessor worldReaderIn, BlockPos pos, GottschTemplate.BlockInfo p_215194_3_, GottschTemplate.BlockInfo blockInfo, PlacementSettings placementSettingsIn, @Nullable GottschTemplate template) {
      return process(worldReaderIn, pos, p_215194_3_, blockInfo, placementSettingsIn);
   }

   /**
    * FORGE: Add entity processing.
    * <p>
    * Use this method to process entities from a structure in much the same way as
    * blocks, parameters are analogous.
    * 
    * @param world
    * @param seedPos
    * @param rawEntityInfo
    * @param entityInfo
    * @param placementSettings
    * @param template
    * 
    * @see #process(IWorldReader, BlockPos,
    *      net.minecraft.world.gen.feature.template.Template.BlockInfo,
    *      net.minecraft.world.gen.feature.template.Template.BlockInfo,
    *      PlacementSettings)
    */
   public GottschTemplate.EntityInfo processEntity(LevelAccessor world, BlockPos seedPos, GottschTemplate.EntityInfo rawEntityInfo, GottschTemplate.EntityInfo entityInfo, PlacementSettings placementSettings, GottschTemplate template) {
      return entityInfo;
   }

//   protected abstract IStructureProcessorType getType();
//
//   protected abstract <T> Dynamic<T> serialize0(DynamicOps<T> ops);
//
//   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
//      return new Dynamic<>(ops, ops.mergeInto(this.serialize0(ops).getValue(), ops.createString("processor_type"), ops.createString(Registry.STRUCTURE_PROCESSOR.getKey(this.getType()).toString())));
//   }
}