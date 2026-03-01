package net.minecraft.world.gen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.SimplePlacement;

public class SquarePlacement
extends SimplePlacement<NoPlacementConfig> {
    public SquarePlacement(Codec<NoPlacementConfig> p_i242032_1_) {
        super(p_i242032_1_);
    }

    @Override
    public Stream<BlockPos> getPositions(Random random, NoPlacementConfig p_212852_2_, BlockPos pos) {
        int i = random.nextInt(16) + pos.getX();
        int j = random.nextInt(16) + pos.getZ();
        int k = pos.getY();
        return Stream.of(new BlockPos(i, k, j));
    }
}
