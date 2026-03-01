package net.minecraft.world.gen.placement;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class TopSolidRangeConfig
implements IPlacementConfig {
    public static final Codec<TopSolidRangeConfig> field_236985_a_ = RecordCodecBuilder.create(p_236986_0_ -> p_236986_0_.group(((MapCodec)Codec.INT.fieldOf("bottom_offset")).orElse(0).forGetter(p_236988_0_ -> p_236988_0_.field_242813_c), ((MapCodec)Codec.INT.fieldOf("top_offset")).orElse(0).forGetter(p_236987_0_ -> p_236987_0_.field_242814_d), ((MapCodec)Codec.INT.fieldOf("maximum")).orElse(0).forGetter(p_242816_0_ -> p_242816_0_.field_242815_e)).apply((Applicative<TopSolidRangeConfig, ?>)p_236986_0_, TopSolidRangeConfig::new));
    public final int field_242813_c;
    public final int field_242814_d;
    public final int field_242815_e;

    public TopSolidRangeConfig(int p_i241992_1_, int p_i241992_2_, int p_i241992_3_) {
        this.field_242813_c = p_i241992_1_;
        this.field_242814_d = p_i241992_2_;
        this.field_242815_e = p_i241992_3_;
    }
}
