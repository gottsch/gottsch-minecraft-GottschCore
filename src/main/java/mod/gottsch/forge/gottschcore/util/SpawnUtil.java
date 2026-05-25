/*
 * This file is part of  GottschCore.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.gottschcore.util;

import mod.gottsch.forge.gottschcore.spatial.Coords;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import mod.gottsch.forge.gottschcore.world.WorldInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Optional;

/**
 * @author by Mark Gottschling on 5/14/2025
 */
public class SpawnUtil {

    /**
     * NOTE this method spawns the mob, but does NOT add the mob to the world.
     *
     * @param level
     * @param random
     * @param entityType
     * @param mob
     * @param coords
     * @return
     */
    public static Optional<? extends LivingEntity> spawnMob(ServerLevel level, RandomSource random, EntityType<? extends LivingEntity> entityType, Entity mob, ICoords coords) {
        return spawnMob(level, random, entityType, MobSpawnType.TRIGGERED, level.getCurrentDifficultyAt(coords.toPos()), coords);
    }

//    public static Optional<? extends LivingEntity> spawnMob(ServerLevel level, RandomSource random, EntityType<? extends LivingEntity> entityType, MobSpawnType spawnType, DifficultyInstance difficulty, ICoords coords) {
//
//        // 20 tries
//        for (int i = 0; i < 20; i++) {
//            int spawnX = coords.getX() + Mth.nextInt(random, 1, 2) * Mth.nextInt(random, -1, 1);
//            int spawnY = coords.getY() + Mth.nextInt(random, 1, 2) * Mth.nextInt(random, -1, 1);
//            int spawnZ = coords.getZ() + Mth.nextInt(random, 1, 2) * Mth.nextInt(random, -1, 1);
//            ICoords spawnCoords = Coords.of(spawnX, spawnY, spawnZ);
//
//            if (!WorldInfo.isClientSide(level)) {
//                SpawnPlacements.Type placement = SpawnPlacements.getPlacementType(entityType);
//                if (NaturalSpawner.isSpawnPositionOk(placement, level, spawnCoords.toPos(), entityType)) {
//                    Optional<? extends LivingEntity> mob = Optional.ofNullable(entityType.create(level));
//                    if (mob.isPresent()) {
//                        if (mob.get() instanceof Mob) {
//                            Optional<SpawnGroupData> groupData = Optional.ofNullable(ForgeEventFactory.onFinalizeSpawn(mob.get(), level, difficulty, MobSpawnType.TRIGGERED, (SpawnGroupData) null, (CompoundTag) null));
//
//                            if (groupData.isPresent()) {
//                                mob.get().setPos((double) spawnX, (double) spawnY, (double) spawnZ);
//                                return mob;
//                            }
//                        }
//                        return mob;
//                    }
//                }
//            }
//        }
//        return Optional.empty();
//    }

    public static Optional<? extends LivingEntity> spawnMob(ServerLevel level, RandomSource random, EntityType<? extends LivingEntity> entityType, MobSpawnType spawnType, DifficultyInstance difficulty, ICoords coords) {
        if (level.isClientSide()) {
            return Optional.empty();
        }

        final int maxTries = 20;
        final SpawnPlacements.Type placementType = SpawnPlacements.getPlacementType(entityType);

        for (int i = 0; i < maxTries; i++) {
            // generate random offset coordinates
            int offsetX = Mth.nextInt(random, 1, 2) * Mth.nextInt(random, -1, 1);
            int offsetY = Mth.nextInt(random, 1, 2) * Mth.nextInt(random, -1, 1);
            int offsetZ = Mth.nextInt(random, 1, 2) * Mth.nextInt(random, -1, 1);

            BlockPos spawnPos = coords.toPos().offset(offsetX, offsetY, offsetZ);

            // check if the spawn position is valid
            if (NaturalSpawner.isSpawnPositionOk(placementType, level, spawnPos, entityType)) {
                // attempt to create the entity and initialize it
                return Optional.ofNullable(entityType.create(level))
                        .map(mob -> {
                            mob.setPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                            return mob;
                        })
                        .flatMap(mob -> {
                            if (mob instanceof Mob) {
                                // attempt to finalize the spawn for Mob entities
                                SpawnGroupData groupData = ForgeEventFactory.onFinalizeSpawn(
                                        (Mob)mob, level, difficulty, MobSpawnType.TRIGGERED, null, null);

                                // check if the event was successful (groupData is present)
                                if (groupData != null) {
                                    return Optional.of(mob);
                                }
                                return Optional.empty(); // finalizeSpawn failed for Mob
                            }
                            return Optional.of(mob); // return non-Mob LivingEntity directly
                        });
                // ff the Optional chain succeeds, it returns the mob, otherwise the loop continues.
            }
        }
        return Optional.empty();
    }

    // convenience method to spawn and add the mob to the world.
    public static Optional<? extends LivingEntity> spawnAndAddMob(ServerLevel level, RandomSource random, EntityType<? extends LivingEntity> entityType, Entity mob, ICoords coords) {
        Optional<? extends LivingEntity> optionalMob =  SpawnUtil.spawnMob(level, random, entityType, MobSpawnType.TRIGGERED, level.getCurrentDifficultyAt(coords.toPos()), coords);
        if (optionalMob.isPresent()) {
            level.addFreshEntityWithPassengers(mob);
        }
        return optionalMob;
    }

}

