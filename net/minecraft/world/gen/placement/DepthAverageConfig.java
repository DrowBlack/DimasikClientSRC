package net.minecraft.world.gen.placement;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class DepthAverageConfig
implements IPlacementConfig {
    public static final Codec<DepthAverageConfig> field_236955_a_ = RecordCodecBuilder.create(p_236956_0_ -> p_236956_0_.group(((MapCodec)Codec.INT.fieldOf("baseline")).forGetter(p_236959_0_ -> p_236959_0_.baseline), ((MapCodec)Codec.INT.fieldOf("spread")).forGetter(p_236958_0_ -> p_236958_0_.spread)).apply((Applicative<DepthAverageConfig, ?>)p_236956_0_, DepthAverageConfig::new));
    public final int baseline;
    public final int spread;

    public DepthAverageConfig(int p_i242022_1_, int p_i242022_2_) {
        this.baseline = p_i242022_1_;
        this.spread = p_i242022_2_;
    }
}
