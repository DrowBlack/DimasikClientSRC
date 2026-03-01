package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class ReplaceBlockConfig
implements IFeatureConfig {
    public static final Codec<ReplaceBlockConfig> field_236604_a_ = RecordCodecBuilder.create(p_236606_0_ -> p_236606_0_.group(((MapCodec)BlockState.CODEC.fieldOf("target")).forGetter(p_236607_0_ -> p_236607_0_.target), ((MapCodec)BlockState.CODEC.fieldOf("state")).forGetter(p_236605_0_ -> p_236605_0_.state)).apply((Applicative<ReplaceBlockConfig, ?>)p_236606_0_, ReplaceBlockConfig::new));
    public final BlockState target;
    public final BlockState state;

    public ReplaceBlockConfig(BlockState target, BlockState state) {
        this.target = target;
        this.state = state;
    }
}
