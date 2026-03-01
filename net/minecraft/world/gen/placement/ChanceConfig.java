package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class ChanceConfig
implements IPlacementConfig {
    public static final Codec<ChanceConfig> field_236950_a_ = ((MapCodec)Codec.INT.fieldOf("chance")).xmap(ChanceConfig::new, p_236951_0_ -> p_236951_0_.chance).codec();
    public final int chance;

    public ChanceConfig(int chance) {
        this.chance = chance;
    }
}
