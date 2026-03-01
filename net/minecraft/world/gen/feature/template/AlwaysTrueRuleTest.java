package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.template.IRuleTestType;
import net.minecraft.world.gen.feature.template.RuleTest;

public class AlwaysTrueRuleTest
extends RuleTest {
    public static final Codec<AlwaysTrueRuleTest> field_237043_a_;
    public static final AlwaysTrueRuleTest INSTANCE;

    private AlwaysTrueRuleTest() {
    }

    @Override
    public boolean test(BlockState p_215181_1_, Random p_215181_2_) {
        return true;
    }

    @Override
    protected IRuleTestType<?> getType() {
        return IRuleTestType.ALWAYS_TRUE;
    }

    static {
        INSTANCE = new AlwaysTrueRuleTest();
        field_237043_a_ = Codec.unit(() -> INSTANCE);
    }
}
