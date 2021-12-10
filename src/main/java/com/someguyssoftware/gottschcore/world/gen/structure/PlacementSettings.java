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

import com.google.common.collect.Lists;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;


/**
 * Mod copy of vanilla StructurePlaceSettings
 * @author Mark Gottschling on Aug 5, 2020
 *
 */
public class PlacementSettings {
	   private Mirror mirror = Mirror.NONE;
	   private Rotation rotation = Rotation.NONE;
	   private BlockPos rotationPivot = BlockPos.ZERO;
	   private boolean ignoreEntities;
	   @Nullable
	   private ChunkPos chunkPos;
	   @Nullable
	   private BoundingBox boundingBox;
	   private boolean keepLiquids = true;
	   @Nullable
	   private Random random;
	   @Nullable
	   private int palette;
	   private final List<StructureProcessor> processors = Lists.newArrayList();
	   private boolean knownShape;
	   private boolean finalizeEntities;

	   public PlacementSettings copy() {
	      PlacementSettings placementsettings = new PlacementSettings();
	      placementsettings.mirror = this.mirror;
	      placementsettings.rotation = this.rotation;
	      placementsettings.rotationPivot = this.rotationPivot;
	      placementsettings.ignoreEntities = this.ignoreEntities;
	      placementsettings.chunkPos = this.chunkPos;
	      placementsettings.boundingBox = this.boundingBox;
	      placementsettings.keepLiquids = this.keepLiquids;
	      placementsettings.random = this.random;
	      placementsettings.palette = this.palette;
	      placementsettings.processors.addAll(this.processors);
	      placementsettings.knownShape = this.knownShape;
	      placementsettings.finalizeEntities = this.finalizeEntities;
	      return placementsettings;
	   }

	   public PlacementSettings setMirror(Mirror p_186214_1_) {
	      this.mirror = p_186214_1_;
	      return this;
	   }

	   public PlacementSettings setRotation(Rotation p_186220_1_) {
	      this.rotation = p_186220_1_;
	      return this;
	   }

	   public PlacementSettings setRotationPivot(BlockPos p_207665_1_) {
	      this.rotationPivot = p_207665_1_;
	      return this;
	   }

	   public PlacementSettings setIgnoreEntities(boolean p_186222_1_) {
	      this.ignoreEntities = p_186222_1_;
	      return this;
	   }

	   public PlacementSettings setChunkPos(ChunkPos p_186218_1_) {
	      this.chunkPos = p_186218_1_;
	      return this;
	   }

	   public PlacementSettings setBoundingBox(BoundingBox p_186223_1_) {
	      this.boundingBox = p_186223_1_;
	      return this;
	   }

	   public PlacementSettings setRandom(@Nullable Random p_189950_1_) {
	      this.random = p_189950_1_;
	      return this;
	   }

	   public PlacementSettings setKnownShape(boolean p_215223_1_) {
	      this.knownShape = p_215223_1_;
	      return this;
	   }

	   public PlacementSettings clearProcessors() {
	      this.processors.clear();
	      return this;
	   }

	   public PlacementSettings addProcessor(StructureProcessor p_215222_1_) {
	      this.processors.add(p_215222_1_);
	      return this;
	   }

	   public PlacementSettings popProcessor(StructureProcessor p_215220_1_) {
	      this.processors.remove(p_215220_1_);
	      return this;
	   }

	   public Mirror getMirror() {
	      return this.mirror;
	   }

	   public Rotation getRotation() {
	      return this.rotation;
	   }

	   public BlockPos getRotationPivot() {
	      return this.rotationPivot;
	   }

	   public Random getRandom(@Nullable BlockPos p_74400_) {
		      if (this.random != null) {
		         return this.random;
		      } else {
		         return p_74400_ == null ? new Random(Util.getMillis()) : new Random(Mth.getSeed(p_74400_));
		      }
		   }

	   public boolean isIgnoreEntities() {
	      return this.ignoreEntities;
	   }

	   @Nullable
	   public BoundingBox getBoundingBox() {
	      if (this.boundingBox == null && this.chunkPos != null) {
	         this.updateBoundingBoxFromChunkPos();
	      }

	      return this.boundingBox;
	   }

	   public boolean getKnownShape() {
	      return this.knownShape;
	   }

	   public List<StructureProcessor> getProcessors() {
	      return this.processors;
	   }

	   void updateBoundingBoxFromChunkPos() {
	      if (this.chunkPos != null) {
	         this.boundingBox = this.calculateBoundingBox(this.chunkPos);
	      }

	   }

	   public boolean shouldKeepLiquids() {
	      return this.keepLiquids;
	   }

	   /**
	    * 
	    * @param palette
	    * @param pos
	    * @return
	    */
	   public GottschTemplate.Palette getRandomPalette(List<GottschTemplate.Palette> palette, @Nullable BlockPos pos) {
	      int i = palette.size();
	      if (i == 0) {
	         throw new IllegalStateException("No palettes");
	      } else {
	         return palette.get(this.getRandom(pos).nextInt(i));
	      }
	   }

	   @Nullable
	   private BoundingBox calculateBoundingBox(@Nullable ChunkPos p_186216_1_) {
	      if (p_186216_1_ == null) {
	         return this.boundingBox;
	      } else {
	         int i = p_186216_1_.x * 16;
	         int j = p_186216_1_.z * 16;
	         return new BoundingBox(i, 0, j, i + 16 - 1, 255, j + 16 - 1);
	      }
	   }

	   public PlacementSettings setFinalizeEntities(boolean p_237133_1_) {
	      this.finalizeEntities = p_237133_1_;
	      return this;
	   }

	   public boolean shouldFinalizeEntities() {
	      return this.finalizeEntities;
	   }
}