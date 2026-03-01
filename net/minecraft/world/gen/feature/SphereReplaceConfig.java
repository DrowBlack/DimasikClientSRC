package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class SphereReplaceConfig
implements IFeatureConfig {
    public static final Codec<SphereReplaceConfig> field_236516_a_ = RecordCodecBuilder.create(p_236518_0_ -> p_236518_0_.group(((MapCodec)BlockState.CODEC.fieldOf("state")).forGetter(p_236521_0_ -> p_236521_0_.state), ((MapCodec)FeatureSpread.func_242254_a(0, 4, 4).fieldOf("radius")).forGetter(p_236520_0_ -> p_236520_0_.radius), ((MapCodec)Codec.intRange(0, 4).fieldOf("half_height")).forGetter(p_236519_0_ -> p_236519_0_.field_242809_d), ((MapCodec)BlockState.CODEC.listOf().fieldOf("targets")).forGetter(p_236517_0_ -> p_236517_0_.targets)).apply((Applicative<SphereReplaceConfig, ?>)p_236518_0_, SphereReplaceConfig::new));
    public final BlockState state;
    public final FeatureSpread radius;
    public final int field_242809_d;
    public final List<BlockState> targets;

    public SphereReplaceConfig(BlockState p_i241986_1_, FeatureSpread p_i241986_2_, int p_i241986_3_, List<BlockState> p_i241986_4_) {
        this.state = p_i241986_1_;
        this.radius = p_i241986_2_;
        this.field_242809_d = p_i241986_3_;
        this.targets = p_i241986_4_;
    }
}
