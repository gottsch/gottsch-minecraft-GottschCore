package mod.gottsch.forge.gottschcore.mobset;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;

/**
 * @author by Mark Gottschling on 9/18/2025
 */
public class MobCount {
    private int min;
    private int max;

    public static final Codec<MobCount> CODEC = RecordCodecBuilder.create(instance -> instance.group(

            Codec.INT.fieldOf("min").forGetter(MobCount::getMin),
            Codec.INT.fieldOf("max").forGetter(MobCount::getMax)
    ).apply(instance, MobCount::new));

    public MobCount(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setMin(int min) {
        this.min = min;
    }

    @Override
    public String toString() {
        return "MobCount{" +
                "max=" + max +
                ", min=" + min +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MobCount mobCount = (MobCount) o;
        return min == mobCount.min && max == mobCount.max;
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }
}
