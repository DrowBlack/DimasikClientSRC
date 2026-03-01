package net.minecraft.world.gen.feature.template;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.feature.template.IPosRuleTests;
import net.minecraft.world.gen.feature.template.PosRuleTest;

public class AxisAlignedLinearPosTest
extends PosRuleTest {
    public static final Codec<AxisAlignedLinearPosTest> field_237045_a_ = RecordCodecBuilder.create(p_237051_0_ -> p_237051_0_.group(((MapCodec)Codec.FLOAT.fieldOf("min_chance")).orElse(Float.valueOf(0.0f)).forGetter(p_237056_0_ -> Float.valueOf(p_237056_0_.field_237046_b_)), ((MapCodec)Codec.FLOAT.fieldOf("max_chance")).orElse(Float.valueOf(0.0f)).forGetter(p_237055_0_ -> Float.valueOf(p_237055_0_.field_237047_d_)), ((MapCodec)Codec.INT.fieldOf("min_dist")).orElse(0).forGetter(p_237054_0_ -> p_237054_0_.field_237048_e_), ((MapCodec)Codec.INT.fieldOf("max_dist")).orElse(0).forGetter(p_237053_0_ -> p_237053_0_.field_237049_f_), ((MapCodec)Direction.Axis.CODEC.fieldOf("axis")).orElse(Direction.Axis.Y).forGetter(p_237052_0_ -> p_237052_0_.field_237050_g_)).apply((Applicative<AxisAlignedLinearPosTest, ?>)p_237051_0_, AxisAlignedLinearPosTest::new));
    private final float field_237046_b_;
    private final float field_237047_d_;
    private final int field_237048_e_;
    private final int field_237049_f_;
    private final Direction.Axis field_237050_g_;

    public AxisAlignedLinearPosTest(float p_i232114_1_, float p_i232114_2_, int p_i232114_3_, int p_i232114_4_, Direction.Axis p_i232114_5_) {
        if (p_i232114_3_ >= p_i232114_4_) {
            throw new IllegalArgumentException("Invalid range: [" + p_i232114_3_ + "," + p_i232114_4_ + "]");
        }
        this.field_237046_b_ = p_i232114_1_;
        this.field_237047_d_ = p_i232114_2_;
        this.field_237048_e_ = p_i232114_3_;
        this.field_237049_f_ = p_i232114_4_;
        this.field_237050_g_ = p_i232114_5_;
    }

    @Override
    public boolean func_230385_a_(BlockPos p_230385_1_, BlockPos p_230385_2_, BlockPos p_230385_3_, Random p_230385_4_) {
        Direction direction = Direction.getFacingFromAxis(Direction.AxisDirection.POSITIVE, this.field_237050_g_);
        float f = Math.abs((p_230385_2_.getX() - p_230385_3_.getX()) * direction.getXOffset());
        float f1 = Math.abs((p_230385_2_.getY() - p_230385_3_.getY()) * direction.getYOffset());
        float f2 = Math.abs((p_230385_2_.getZ() - p_230385_3_.getZ()) * direction.getZOffset());
        int i = (int)(f + f1 + f2);
        float f3 = p_230385_4_.nextFloat();
        return (double)f3 <= MathHelper.clampedLerp(this.field_237046_b_, this.field_237047_d_, MathHelper.func_233020_c_(i, this.field_237048_e_, this.field_237049_f_));
    }

    @Override
    protected IPosRuleTests<?> func_230384_a_() {
        return IPosRuleTests.field_237105_c_;
    }
}
