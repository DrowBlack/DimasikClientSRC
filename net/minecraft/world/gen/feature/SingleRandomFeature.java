package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class SingleRandomFeature
implements IFeatureConfig {
    public static final Codec<SingleRandomFeature> field_236642_a_ = ((MapCodec)ConfiguredFeature.field_242764_c.fieldOf("features")).xmap(SingleRandomFeature::new, p_236643_0_ -> p_236643_0_.features).codec();
    public final List<Supplier<ConfiguredFeature<?, ?>>> features;

    public SingleRandomFeature(List<Supplier<ConfiguredFeature<?, ?>>> features) {
        this.features = features;
    }

    @Override
    public Stream<ConfiguredFeature<?, ?>> func_241856_an_() {
        return this.features.stream().flatMap(p_242826_0_ -> ((ConfiguredFeature)p_242826_0_.get()).func_242768_d());
    }
}
