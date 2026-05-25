package mod.gottsch.forge.gottschcore.mobset;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * @author by Mark Gottschling on 9/18/2025
 */
public class MobSetData {

    // id of set
    private final ResourceLocation id;
    // category of set
    private final String category;
    // count of mobs. vanilla NumberProvider does not provide a CODEC,
    // therefor a simple count class is being used.
    private final MobCount count;
    //
    private final List<WeightedMob> mobWeights;
    private final boolean replace;

    // a Codec to parse this data from a JSON file.
    public static final Codec<MobSetData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(MobSetData::getId),
            Codec.STRING.fieldOf("category").forGetter(MobSetData::getCategory),
            MobCount.CODEC.fieldOf("count").forGetter(MobSetData::getCount),
            WeightedMob.CODEC.listOf().fieldOf("mobs").forGetter(MobSetData::getMobs),
            Codec.BOOL.optionalFieldOf("replace", false).forGetter(MobSetData::isReplace)
    ).apply(instance, MobSetData::new));


    public MobSetData(ResourceLocation id, String category, int num, List<WeightedMob> mobs) {
        this(id, category, num, num, mobs, false);
    }
    public MobSetData(ResourceLocation id, String category, int min, int max, List<WeightedMob> mobs, boolean replace) {
        this(id, category, new MobCount(min, max), mobs, replace);
    }

    public MobSetData(ResourceLocation id, String category, MobCount count, List<WeightedMob> mobs, boolean replace) {
        this.id = id;
        this.category = category;
        this.count = count;
        this.mobWeights = mobs;
        this.replace = replace;
    }

    public MobSetData withMobs(List<WeightedMob> list) {
        return new MobSetData(this.id, this.category, this.count, list, this.replace);
    }

    public MobSetData withCount(MobCount count) {
        return new MobSetData(this.id, this.category, count, this.mobWeights, this.replace);
    }

    public String getCategory() {
        return category;
    }

    public MobCount getCount() {
        return count;
    }

    public List<WeightedMob> getMobs() {
        return mobWeights;
    }

    public ResourceLocation getId() {
        return id;
    }

    public boolean isReplace() {
        return replace;
    }

    @Override
    public String toString() {
        return "MobSetData{" +
                "category='" + category + '\'' +
                ", name=" + id +
                ", count=" + count +
                ", mobWeights=" + mobWeights +
                '}';
    }
}
