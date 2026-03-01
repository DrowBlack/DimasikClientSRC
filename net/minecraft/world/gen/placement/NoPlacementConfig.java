package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class NoPlacementConfig
implements IPlacementConfig {
    public static final Codec<NoPlacementConfig> field_236555_a_;
    public static final NoPlacementConfig field_236556_b_;

    static {
        field_236556_b_ = new NoPlacementConfig();
        field_236555_a_ = Codec.unit(() -> field_236556_b_);
    }
}
