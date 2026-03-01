package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;
import net.minecraft.world.gen.feature.AbstractFeatureSizeType;
import net.minecraft.world.gen.feature.FeatureSizeType;

public class ThreeLayerFeature
extends AbstractFeatureSizeType {
    public static final Codec<ThreeLayerFeature> field_236716_c_ = RecordCodecBuilder.create(p_236722_0_ -> p_236722_0_.group(((MapCodec)Codec.intRange(0, 80).fieldOf("limit")).orElse(1).forGetter(p_236727_0_ -> p_236727_0_.field_236717_d_), ((MapCodec)Codec.intRange(0, 80).fieldOf("upper_limit")).orElse(1).forGetter(p_236726_0_ -> p_236726_0_.field_236718_e_), ((MapCodec)Codec.intRange(0, 16).fieldOf("lower_size")).orElse(0).forGetter(p_236725_0_ -> p_236725_0_.field_236719_f_), ((MapCodec)Codec.intRange(0, 16).fieldOf("middle_size")).orElse(1).forGetter(p_236724_0_ -> p_236724_0_.field_236720_g_), ((MapCodec)Codec.intRange(0, 16).fieldOf("upper_size")).orElse(1).forGetter(p_236723_0_ -> p_236723_0_.field_236721_h_), ThreeLayerFeature.func_236706_a_()).apply((Applicative<ThreeLayerFeature, ?>)p_236722_0_, ThreeLayerFeature::new));
    private final int field_236717_d_;
    private final int field_236718_e_;
    private final int field_236719_f_;
    private final int field_236720_g_;
    private final int field_236721_h_;

    public ThreeLayerFeature(int p_i232024_1_, int p_i232024_2_, int p_i232024_3_, int p_i232024_4_, int p_i232024_5_, OptionalInt p_i232024_6_) {
        super(p_i232024_6_);
        this.field_236717_d_ = p_i232024_1_;
        this.field_236718_e_ = p_i232024_2_;
        this.field_236719_f_ = p_i232024_3_;
        this.field_236720_g_ = p_i232024_4_;
        this.field_236721_h_ = p_i232024_5_;
    }

    @Override
    protected FeatureSizeType<?> func_230370_b_() {
        return FeatureSizeType.THREE_LAYERS_FEATURE_SIZE;
    }

    @Override
    public int func_230369_a_(int p_230369_1_, int p_230369_2_) {
        if (p_230369_2_ < this.field_236717_d_) {
            return this.field_236719_f_;
        }
        return p_230369_2_ >= p_230369_1_ - this.field_236718_e_ ? this.field_236721_h_ : this.field_236720_g_;
    }
}
