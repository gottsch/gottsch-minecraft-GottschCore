package mod.gottsch.forge.gottschcore.registry;


import mod.gottsch.forge.gottschcore.GottschCore;
import mod.gottsch.forge.gottschcore.config.MobSetConfiguration;
import mod.gottsch.forge.gottschcore.random.WeightedCollection;
import mod.gottsch.forge.gottschcore.util.ModUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fml.ModList;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author by Mark Gottschling on 5/13/2025
 */
public class MobSetRegistry {
    public static final Map<ResourceLocation, WeightedCollection<Integer, ResourceLocation>> REGISTRY = new HashMap<>();
    // TODO by category

    /**
     *
     */
    public MobSetRegistry() {}

    /**
     * load/register mob sets from config
     */
    @Deprecated(forRemoval = true, since = "2.6.0")
    public static void initialize(MobSetConfiguration config) {
        GottschCore.LOGGER.debug("initializing mob sets");
        // clear just the rarity selector. the registry is initialized during setup.
        REGISTRY.clear();

        GottschCore.LOGGER.info("mobSetConfig is present");
            // for each mob set
            config.getMobSets().forEach(mobSet -> {
                GottschCore.LOGGER.debug("processing mobSet -> {}", mobSet.getName());
                // get the weighted collection
                WeightedCollection<Integer, ResourceLocation> collection;
                ResourceLocation name = mobSet.getName();
                if (REGISTRY.containsKey(name)) {
                    collection = REGISTRY.get(name);
                } else {
                    collection = new WeightedCollection<>();
                }

                // populate the weighted collection
                mobSet.getMobs().forEach(weightedMob -> {
                    ResourceLocation mobName = weightedMob.getName();
                    // determine if the mob's mod is loaded
                    if (ModList.get().isLoaded(mobName.getNamespace())) {
                        Optional<EntityType<?>> entityType = EntityType.byString(weightedMob.getName().toString());
                        if (entityType.isPresent()) {
                            GottschCore.LOGGER.debug("adding weighted mob -> {}", mobName);
                            collection.add(weightedMob.getWeight(), mobName);
                        }
                    }
                });

                // add the collection to the registry
                if (collection.size() > 0) {
                    GottschCore.LOGGER.debug("registered mobset -> {}", name);
                    REGISTRY.put(name, collection);
                }
            });
    }

    // TODO probably change to wrap weighted collection in an object along with count
    public static Optional<WeightedCollection<Integer, ResourceLocation>> get(ResourceLocation mobSetName) {
        if (REGISTRY.containsKey(mobSetName)) {
            return Optional.of(REGISTRY.get(mobSetName));
        } else {
            return Optional.empty();
        }
    }
}

