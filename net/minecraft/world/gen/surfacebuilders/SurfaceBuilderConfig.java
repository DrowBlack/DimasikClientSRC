package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;

public class SurfaceBuilderConfig
implements ISurfaceBuilderConfig {
    public static final Codec<SurfaceBuilderConfig> field_237203_a_ = RecordCodecBuilder.create(p_237204_0_ -> p_237204_0_.group(((MapCodec)BlockState.CODEC.fieldOf("top_material")).forGetter(p_237207_0_ -> p_237207_0_.topMaterial), ((MapCodec)BlockState.CODEC.fieldOf("under_material")).forGetter(p_237206_0_ -> p_237206_0_.underMaterial), ((MapCodec)BlockState.CODEC.fieldOf("underwater_material")).forGetter(p_237205_0_ -> p_237205_0_.underWaterMaterial)).apply((Applicative<SurfaceBuilderConfig, ?>)p_237204_0_, SurfaceBuilderConfig::new));
    private final BlockState topMaterial;
    private final BlockState underMaterial;
    private final BlockState underWaterMaterial;

    public SurfaceBuilderConfig(BlockState topMaterial, BlockState underMaterial, BlockState underWaterMaterial) {
        this.topMaterial = topMaterial;
        this.underMaterial = underMaterial;
        this.underWaterMaterial = underWaterMaterial;
    }

    @Override
    public BlockState getTop() {
        return this.topMaterial;
    }

    @Override
    public BlockState getUnder() {
        return this.underMaterial;
    }

    public BlockState getUnderWaterMaterial() {
        return this.underWaterMaterial;
    }
}
