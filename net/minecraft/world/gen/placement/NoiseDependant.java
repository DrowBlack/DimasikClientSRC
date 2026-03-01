package net.minecraft.world.gen.placement;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class NoiseDependant
implements IPlacementConfig {
    public static final Codec<NoiseDependant> field_236550_a_ = RecordCodecBuilder.create(p_236552_0_ -> p_236552_0_.group(((MapCodec)Codec.DOUBLE.fieldOf("noise_level")).forGetter(p_236554_0_ -> p_236554_0_.noiseLevel), ((MapCodec)Codec.INT.fieldOf("below_noise")).forGetter(p_236553_0_ -> p_236553_0_.belowNoise), ((MapCodec)Codec.INT.fieldOf("above_noise")).forGetter(p_236551_0_ -> p_236551_0_.aboveNoise)).apply((Applicative<NoiseDependant, ?>)p_236552_0_, NoiseDependant::new));
    public final double noiseLevel;
    public final int belowNoise;
    public final int aboveNoise;

    public NoiseDependant(double noiseLevel, int belowNoise, int aboveNoise) {
        this.noiseLevel = noiseLevel;
        this.belowNoise = belowNoise;
        this.aboveNoise = aboveNoise;
    }
}
