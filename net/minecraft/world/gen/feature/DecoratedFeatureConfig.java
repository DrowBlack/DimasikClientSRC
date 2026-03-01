package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.ConfiguredPlacement;

public class DecoratedFeatureConfig
implements IFeatureConfig {
    public static final Codec<DecoratedFeatureConfig> field_236491_a_ = RecordCodecBuilder.create(p_236493_0_ -> p_236493_0_.group(((MapCodec)ConfiguredFeature.field_236264_b_.fieldOf("feature")).forGetter(p_236494_0_ -> p_236494_0_.feature), ((MapCodec)ConfiguredPlacement.field_236952_a_.fieldOf("decorator")).forGetter(p_236492_0_ -> p_236492_0_.decorator)).apply((Applicative<DecoratedFeatureConfig, ?>)p_236493_0_, DecoratedFeatureConfig::new));
    public final Supplier<ConfiguredFeature<?, ?>> feature;
    public final ConfiguredPlacement<?> decorator;

    public DecoratedFeatureConfig(Supplier<ConfiguredFeature<?, ?>> p_i241984_1_, ConfiguredPlacement<?> p_i241984_2_) {
        this.feature = p_i241984_1_;
        this.decorator = p_i241984_2_;
    }

    public String toString() {
        return String.format("< %s [%s | %s] >", this.getClass().getSimpleName(), Registry.FEATURE.getKey((Feature<?>)this.feature.get().func_242766_b()), this.decorator);
    }

    @Override
    public Stream<ConfiguredFeature<?, ?>> func_241856_an_() {
        return this.feature.get().func_242768_d();
    }
}
