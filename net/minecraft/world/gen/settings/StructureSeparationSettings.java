package net.minecraft.world.gen.settings;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;

public class StructureSeparationSettings {
    public static final Codec<StructureSeparationSettings> field_236664_a_ = RecordCodecBuilder.create(p_236669_0_ -> p_236669_0_.group(((MapCodec)Codec.intRange(0, 4096).fieldOf("spacing")).forGetter(p_236675_0_ -> p_236675_0_.field_236665_b_), ((MapCodec)Codec.intRange(0, 4096).fieldOf("separation")).forGetter(p_236674_0_ -> p_236674_0_.field_236666_c_), ((MapCodec)Codec.intRange(0, Integer.MAX_VALUE).fieldOf("salt")).forGetter(p_236672_0_ -> p_236672_0_.field_236667_d_)).apply((Applicative<StructureSeparationSettings, ?>)p_236669_0_, StructureSeparationSettings::new)).comapFlatMap(p_236670_0_ -> p_236670_0_.field_236665_b_ <= p_236670_0_.field_236666_c_ ? DataResult.error("Spacing has to be smaller than separation") : DataResult.success(p_236670_0_), Function.identity());
    private final int field_236665_b_;
    private final int field_236666_c_;
    private final int field_236667_d_;

    public StructureSeparationSettings(int p_i232019_1_, int p_i232019_2_, int p_i232019_3_) {
        this.field_236665_b_ = p_i232019_1_;
        this.field_236666_c_ = p_i232019_2_;
        this.field_236667_d_ = p_i232019_3_;
    }

    public int func_236668_a_() {
        return this.field_236665_b_;
    }

    public int func_236671_b_() {
        return this.field_236666_c_;
    }

    public int func_236673_c_() {
        return this.field_236667_d_;
    }
}
