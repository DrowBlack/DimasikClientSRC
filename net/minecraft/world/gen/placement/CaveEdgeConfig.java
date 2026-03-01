package net.minecraft.world.gen.placement;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class CaveEdgeConfig
implements IPlacementConfig {
    public static final Codec<CaveEdgeConfig> field_236946_a_ = RecordCodecBuilder.create(p_236947_0_ -> p_236947_0_.group(((MapCodec)GenerationStage.Carving.field_236074_c_.fieldOf("step")).forGetter(p_236949_0_ -> p_236949_0_.step), ((MapCodec)Codec.FLOAT.fieldOf("probability")).forGetter(p_236948_0_ -> Float.valueOf(p_236948_0_.probability))).apply((Applicative<CaveEdgeConfig, ?>)p_236947_0_, CaveEdgeConfig::new));
    protected final GenerationStage.Carving step;
    protected final float probability;

    public CaveEdgeConfig(GenerationStage.Carving step, float probability) {
        this.step = step;
        this.probability = probability;
    }
}
