package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.carver.ICarverConfig;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class ProbabilityConfig
implements ICarverConfig,
IFeatureConfig {
    public static final Codec<ProbabilityConfig> field_236576_b_ = RecordCodecBuilder.create(p_236578_0_ -> p_236578_0_.group(((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("probability")).forGetter(p_236577_0_ -> Float.valueOf(p_236577_0_.probability))).apply((Applicative<ProbabilityConfig, ?>)p_236578_0_, ProbabilityConfig::new));
    public final float probability;

    public ProbabilityConfig(float probability) {
        this.probability = probability;
    }
}
