package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.carver.ICarverConfig;

public class EmptyCarverConfig
implements ICarverConfig {
    public static final Codec<EmptyCarverConfig> field_236237_b_;
    public static final EmptyCarverConfig field_236238_c_;

    static {
        field_236238_c_ = new EmptyCarverConfig();
        field_236237_b_ = Codec.unit(() -> field_236238_c_);
    }
}
