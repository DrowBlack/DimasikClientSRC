package net.minecraft.world.gen.surfacebuilders;

import net.minecraft.block.BlockState;

public interface ISurfaceBuilderConfig {
    public BlockState getTop();

    public BlockState getUnder();
}
