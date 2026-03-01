package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;

public class MineshaftConfig
implements IFeatureConfig {
    public static final Codec<MineshaftConfig> field_236541_a_ = RecordCodecBuilder.create(p_236543_0_ -> p_236543_0_.group(((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("probability")).forGetter(p_236544_0_ -> Float.valueOf(p_236544_0_.probability)), ((MapCodec)MineshaftStructure.Type.field_236324_c_.fieldOf("type")).forGetter(p_236542_0_ -> p_236542_0_.type)).apply((Applicative<MineshaftConfig, ?>)p_236543_0_, MineshaftConfig::new));
    public final float probability;
    public final MineshaftStructure.Type type;

    public MineshaftConfig(float p_i241988_1_, MineshaftStructure.Type p_i241988_2_) {
        this.probability = p_i241988_1_;
        this.type = p_i241988_2_;
    }
}
