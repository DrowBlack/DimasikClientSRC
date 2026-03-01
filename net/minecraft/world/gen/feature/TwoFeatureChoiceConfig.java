package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class TwoFeatureChoiceConfig
implements IFeatureConfig {
    public static final Codec<TwoFeatureChoiceConfig> field_236579_a_ = RecordCodecBuilder.create(p_236581_0_ -> p_236581_0_.group(((MapCodec)ConfiguredFeature.field_236264_b_.fieldOf("feature_true")).forGetter(p_236582_0_ -> p_236582_0_.field_227285_a_), ((MapCodec)ConfiguredFeature.field_236264_b_.fieldOf("feature_false")).forGetter(p_236580_0_ -> p_236580_0_.field_227286_b_)).apply((Applicative<TwoFeatureChoiceConfig, ?>)p_236581_0_, TwoFeatureChoiceConfig::new));
    public final Supplier<ConfiguredFeature<?, ?>> field_227285_a_;
    public final Supplier<ConfiguredFeature<?, ?>> field_227286_b_;

    public TwoFeatureChoiceConfig(Supplier<ConfiguredFeature<?, ?>> p_i241990_1_, Supplier<ConfiguredFeature<?, ?>> p_i241990_2_) {
        this.field_227285_a_ = p_i241990_1_;
        this.field_227286_b_ = p_i241990_2_;
    }

    @Override
    public Stream<ConfiguredFeature<?, ?>> func_241856_an_() {
        return Stream.concat(this.field_227285_a_.get().func_242768_d(), this.field_227286_b_.get().func_242768_d());
    }
}
