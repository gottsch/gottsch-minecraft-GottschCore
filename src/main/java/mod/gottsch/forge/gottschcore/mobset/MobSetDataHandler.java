package mod.gottsch.forge.gottschcore.mobset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import mod.gottsch.forge.gottschcore.GottschCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;


import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author by Mark Gottschling on 9/18/2025
 */
public class MobSetDataHandler extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final String DATA_DIRECTORY = "mob_sets";

    public MobSetDataHandler() {
        super(GSON, DATA_DIRECTORY);
    }

    /**
     *
     * @param jsonElementMap  The parsed JSON data.
     * @param resourceManager The resource manager.
     * @param profilerFiller  The profiler.
     */
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        GottschCore.LOGGER.info("loading mob_sets from data packs...");

        jsonElementMap.forEach((location, jsonElement) -> {
            try {
                // deserialize the JSON element into our MobSetData object
                MobSetData newData = MobSetData.CODEC
                        .parse(JsonOps.INSTANCE, jsonElement)
                        .getOrThrow(false, GottschCore.LOGGER::error);

                // fetch existing data and decide whether to replace, merge, or register new
                MobSetDataRegistry.get(newData.getId())
                        .ifPresentOrElse(
                                existingData -> {
                                    MobSetData finalData = existingData.isReplace()
                                            ? newData
                                            : mergeMobSetData(existingData, newData); // delegate merging to a helper method
                                    // replace existing data with final data
                                    MobSetDataRegistry.register(finalData);
                                },
                                // register net new mob set
                                () -> MobSetDataRegistry.register(newData)
                        );
                if (GottschCore.LOGGER.isDebugEnabled()) {
                    GottschCore.LOGGER.debug("registered mob_set data -> {}", newData.getId());
                }
            } catch (Exception e) {
                GottschCore.LOGGER.error("failed to parse mob_set data JSON for {}: {}", location, e.getMessage());
            }
        });
    }

    /**
     * merges new MobSetData into an existing MobSetData object.
     * this is where the core logic from the original TODOs is implemented.
     * assumes MobSetData has appropriate setters or a builder for creating a new instance.
     */
    private MobSetData mergeMobSetData(MobSetData existingData, MobSetData newData) {
        // 1. update/replace Count
        MobSetData result = existingData.withCount(newData.getCount());

        // 2. merge mobs: use a Map for efficient lookups and updates
        Map<ResourceLocation, WeightedMob> mergedMobs = result.getMobs().stream()
                .collect(Collectors.toMap(
                        WeightedMob::id,
                        Function.identity(),
                        (oldMob, newMob) -> oldMob // should not happen with distinct IDs
                ));

        // for each new mob, either update the existing one or add it
        newData.getMobs().forEach(newMob -> {
            mergedMobs.compute(newMob.getId(), (mobId, existingMob) -> {
                if (existingMob != null) {
                    // mob found: update the existing mob with properties from the new mob.
                    return existingMob.withWeight(newMob.getWeight()); // Example update
                } else {
                    // Mob not found: Add the new mob.
                    return newMob;
                }
            });
        });

        // create the final MobSetData with the merged list of Mobs
        return result.withMobs(mergedMobs.values().stream().toList());
    }
}