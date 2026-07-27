package mod.gottsch.forge.gottschcore.mobset;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author by Mark Gottschling on 9/18/2025
 */
public enum MobSetDataRegistry {
    INSTANCE;

    // data pack reloads run off the main thread, so the registry must be thread-safe
    private static final Map<ResourceLocation, MobSetData> REGISTRY = new ConcurrentHashMap<>();

    public static Optional<MobSetData> register(MobSetData data) {
        return Optional.ofNullable(REGISTRY.put(data.getId(), data));
    }

    public static Optional<MobSetData> get(ResourceLocation id) {
        // ConcurrentHashMap does not permit null keys, so guard against an unset id
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(REGISTRY.get(id));
    }

    public static void clear() {
        REGISTRY.clear();
    }
}
