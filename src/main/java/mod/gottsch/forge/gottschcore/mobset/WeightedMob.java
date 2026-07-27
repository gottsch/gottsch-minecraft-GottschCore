package mod.gottsch.forge.gottschcore.mobset;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

/**
 * @author by Mark Gottschling on 9/18/2025
 */
public record WeightedMob(ResourceLocation id, int weight) {
    public static final Codec<WeightedMob> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(WeightedMob::id),
            Codec.INT.fieldOf("weight").forGetter(WeightedMob::weight)
    ).apply(instance, WeightedMob::new));

    public WeightedMob withWeight(int weight) {
        return new WeightedMob(this.id, weight);
    }
}
