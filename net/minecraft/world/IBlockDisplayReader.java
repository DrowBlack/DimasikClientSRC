package net.minecraft.world;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;

public interface IBlockDisplayReader
extends IBlockReader {
    public float func_230487_a_(Direction var1, boolean var2);

    public WorldLightManager getLightManager();

    public int getBlockColor(BlockPos var1, ColorResolver var2);

    default public int getLightFor(LightType lightTypeIn, BlockPos blockPosIn) {
        return this.getLightManager().getLightEngine(lightTypeIn).getLightFor(blockPosIn);
    }

    default public int getLightSubtracted(BlockPos blockPosIn, int amount) {
        return this.getLightManager().getLightSubtracted(blockPosIn, amount);
    }

    default public boolean canSeeSky(BlockPos blockPosIn) {
        return this.getLightFor(LightType.SKY, blockPosIn) >= this.getMaxLightLevel();
    }
}
