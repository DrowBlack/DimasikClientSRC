package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class ConfiguredRandomFeatureList {
    public static final Codec<ConfiguredRandomFeatureList> field_236430_a_ = RecordCodecBuilder.create(p_236433_0_ -> p_236433_0_.group(((MapCodec)ConfiguredFeature.field_236264_b_.fieldOf("feature")).forGetter(p_242789_0_ -> p_242789_0_.feature), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("chance")).forGetter(p_236432_0_ -> Float.valueOf(p_236432_0_.chance))).apply((Applicative<ConfiguredRandomFeatureList, ?>)p_236433_0_, ConfiguredRandomFeatureList::new));
    public final Supplier<ConfiguredFeature<?, ?>> feature;
    public final float chance;

    public ConfiguredRandomFeatureList(ConfiguredFeature<?, ?> p_i225822_1_, float p_i225822_2_) {
        this(() -> p_i225822_1_, p_i225822_2_);
    }

    private ConfiguredRandomFeatureList(Supplier<ConfiguredFeature<?, ?>> p_i241980_1_, float p_i241980_2_) {
        this.feature = p_i241980_1_;
        this.chance = p_i241980_2_;
    }

    public boolean func_242787_a(ISeedReader p_242787_1_, ChunkGenerator p_242787_2_, Random p_242787_3_, BlockPos p_242787_4_) {
        return this.feature.get().func_242765_a(p_242787_1_, p_242787_2_, p_242787_3_, p_242787_4_);
    }
}
