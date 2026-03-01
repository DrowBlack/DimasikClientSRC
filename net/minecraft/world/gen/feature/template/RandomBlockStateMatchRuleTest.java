package net.minecraft.world.gen.feature.template;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.template.IRuleTestType;
import net.minecraft.world.gen.feature.template.RuleTest;

public class RandomBlockStateMatchRuleTest
extends RuleTest {
    public static final Codec<RandomBlockStateMatchRuleTest> field_237121_a_ = RecordCodecBuilder.create(p_237122_0_ -> p_237122_0_.group(((MapCodec)BlockState.CODEC.fieldOf("block_state")).forGetter(p_237124_0_ -> p_237124_0_.state), ((MapCodec)Codec.FLOAT.fieldOf("probability")).forGetter(p_237123_0_ -> Float.valueOf(p_237123_0_.probability))).apply((Applicative<RandomBlockStateMatchRuleTest, ?>)p_237122_0_, RandomBlockStateMatchRuleTest::new));
    private final BlockState state;
    private final float probability;

    public RandomBlockStateMatchRuleTest(BlockState state, float probability) {
        this.state = state;
        this.probability = probability;
    }

    @Override
    public boolean test(BlockState p_215181_1_, Random p_215181_2_) {
        return p_215181_1_ == this.state && p_215181_2_.nextFloat() < this.probability;
    }

    @Override
    protected IRuleTestType<?> getType() {
        return IRuleTestType.RANDOM_BLOCKSTATE_MATCH;
    }
}
