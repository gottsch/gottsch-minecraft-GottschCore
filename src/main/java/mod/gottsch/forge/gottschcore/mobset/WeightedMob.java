package mod.gottsch.forge.gottschcore.mobset;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

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

    public ResourceLocation getId() {
        return id;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "WeightedMob{" +
                "id=" + id +
                ", weight=" + weight +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WeightedMob that = (WeightedMob) o;
        return weight == that.weight && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, weight);
    }
}
