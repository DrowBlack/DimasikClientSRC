package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.SimplePlacement;

public class ChancePlacement
extends SimplePlacement<ChanceConfig> {
    public ChancePlacement(Codec<ChanceConfig> p_i242015_1_) {
        super(p_i242015_1_);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, ChanceConfig p_212852_2_, BlockPos pos) {
        return random.nextFloat() < 1.0f / (float)p_212852_2_.chance ? Stream.of(pos) : Stream.empty();
    }
}
