package com.someguyssoftware.gottschcore.world.gen.structure;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;

public abstract class StructureProcessor {
   @Nullable
   @Deprecated
   public GottschTemplate.BlockInfo process(IWorldReader worldReaderIn, BlockPos pos, GottschTemplate.BlockInfo p_215194_3_, GottschTemplate.BlockInfo blockInfo, PlacementSettings placementSettingsIn) {
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
   public GottschTemplate.BlockInfo process(IWorldReader worldReaderIn, BlockPos pos, GottschTemplate.BlockInfo p_215194_3_, GottschTemplate.BlockInfo blockInfo, PlacementSettings placementSettingsIn, @Nullable GottschTemplate template) {
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
   public GottschTemplate.EntityInfo processEntity(IWorldReader world, BlockPos seedPos, GottschTemplate.EntityInfo rawEntityInfo, GottschTemplate.EntityInfo entityInfo, PlacementSettings placementSettings, GottschTemplate template) {
      return entityInfo;
   }

   protected abstract IStructureProcessorType getType();

   protected abstract <T> Dynamic<T> serialize0(DynamicOps<T> ops);

   public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
      return new Dynamic<>(ops, ops.mergeInto(this.serialize0(ops).getValue(), ops.createString("processor_type"), ops.createString(Registry.STRUCTURE_PROCESSOR.getKey(this.getType()).toString())));
   }
}