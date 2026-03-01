package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;

public class NopProcessor
extends StructureProcessor {
    public static final Codec<NopProcessor> field_237097_a_;
    public static final NopProcessor INSTANCE;

    private NopProcessor() {
    }

    @Override
    @Nullable
    public Template.BlockInfo func_230386_a_(IWorldReader p_230386_1_, BlockPos p_230386_2_, BlockPos p_230386_3_, Template.BlockInfo p_230386_4_, Template.BlockInfo p_230386_5_, PlacementSettings p_230386_6_) {
        return p_230386_5_;
    }

    @Override
    protected IStructureProcessorType<?> getType() {
        return IStructureProcessorType.NOP;
    }

    static {
        INSTANCE = new NopProcessor();
        field_237097_a_ = Codec.unit(() -> INSTANCE);
    }
}
