package net.minecraft.world.gen.placement;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class AtSurfaceWithExtraConfig
implements IPlacementConfig {
    public static final Codec<AtSurfaceWithExtraConfig> field_236973_a_ = RecordCodecBuilder.create(p_236974_0_ -> p_236974_0_.group(((MapCodec)Codec.INT.fieldOf("count")).forGetter(p_236977_0_ -> p_236977_0_.count), ((MapCodec)Codec.FLOAT.fieldOf("extra_chance")).forGetter(p_236976_0_ -> Float.valueOf(p_236976_0_.extraChance)), ((MapCodec)Codec.INT.fieldOf("extra_count")).forGetter(p_236975_0_ -> p_236975_0_.extraCount)).apply((Applicative<AtSurfaceWithExtraConfig, ?>)p_236974_0_, AtSurfaceWithExtraConfig::new));
    public final int count;
    public final float extraChance;
    public final int extraCount;

    public AtSurfaceWithExtraConfig(int count, float extraChanceIn, int extraCountIn) {
        this.count = count;
        this.extraChance = extraChanceIn;
        this.extraCount = extraCountIn;
    }
}
