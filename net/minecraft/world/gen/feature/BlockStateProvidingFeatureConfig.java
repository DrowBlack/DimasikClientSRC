package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class BlockStateProvidingFeatureConfig
implements IFeatureConfig {
    public static final Codec<BlockStateProvidingFeatureConfig> field_236453_a_ = ((MapCodec)BlockStateProvider.CODEC.fieldOf("state_provider")).xmap(BlockStateProvidingFeatureConfig::new, p_236454_0_ -> p_236454_0_.field_227268_a_).codec();
    public final BlockStateProvider field_227268_a_;

    public BlockStateProvidingFeatureConfig(BlockStateProvider p_i225830_1_) {
        this.field_227268_a_ = p_i225830_1_;
    }
}
