package com.github.lunatrius.schematica.api;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface ISchematic {
    public BlockState getBlockState(BlockPos var1);

    public int getWidth();

    public int getHeight();

    public int getLength();
}
