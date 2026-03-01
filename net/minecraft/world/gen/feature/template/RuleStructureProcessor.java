package net.minecraft.world.gen.feature.template;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.RuleEntry;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;

public class RuleStructureProcessor
extends StructureProcessor {
    public static final Codec<RuleStructureProcessor> field_237125_a_ = ((MapCodec)RuleEntry.field_237108_a_.listOf().fieldOf("rules")).xmap(RuleStructureProcessor::new, p_237126_0_ -> p_237126_0_.rules).codec();
    private final ImmutableList<RuleEntry> rules;

    public RuleStructureProcessor(List<? extends RuleEntry> rules) {
        this.rules = ImmutableList.copyOf(rules);
    }

    @Override
    @Nullable
    public Template.BlockInfo func_230386_a_(IWorldReader p_230386_1_, BlockPos p_230386_2_, BlockPos p_230386_3_, Template.BlockInfo p_230386_4_, Template.BlockInfo p_230386_5_, PlacementSettings p_230386_6_) {
        Random random = new Random(MathHelper.getPositionRandom(p_230386_5_.pos));
        BlockState blockstate = p_230386_1_.getBlockState(p_230386_5_.pos);
        for (RuleEntry ruleentry : this.rules) {
            if (!ruleentry.func_237110_a_(p_230386_5_.state, blockstate, p_230386_4_.pos, p_230386_5_.pos, p_230386_3_, random)) continue;
            return new Template.BlockInfo(p_230386_5_.pos, ruleentry.getOutputState(), ruleentry.getOutputNbt());
        }
        return p_230386_5_;
    }

    @Override
    protected IStructureProcessorType<?> getType() {
        return IStructureProcessorType.RULE;
    }
}
