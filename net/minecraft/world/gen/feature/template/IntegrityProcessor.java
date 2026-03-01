package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;

public class IntegrityProcessor
extends StructureProcessor {
    public static final Codec<IntegrityProcessor> field_237077_a_ = ((MapCodec)Codec.FLOAT.fieldOf("integrity")).orElse(Float.valueOf(1.0f)).xmap(IntegrityProcessor::new, p_237078_0_ -> Float.valueOf(p_237078_0_.integrity)).codec();
    private final float integrity;

    public IntegrityProcessor(float integrity) {
        this.integrity = integrity;
    }

    @Override
    @Nullable
    public Template.BlockInfo func_230386_a_(IWorldReader p_230386_1_, BlockPos p_230386_2_, BlockPos p_230386_3_, Template.BlockInfo p_230386_4_, Template.BlockInfo p_230386_5_, PlacementSettings p_230386_6_) {
        Random random = p_230386_6_.getRandom(p_230386_5_.pos);
        return !(this.integrity >= 1.0f) && !(random.nextFloat() <= this.integrity) ? null : p_230386_5_;
    }

    @Override
    protected IStructureProcessorType<?> getType() {
        return IStructureProcessorType.BLOCK_ROT;
    }
}
