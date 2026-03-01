package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public interface IWorldWriter {
    public boolean setBlockState(BlockPos var1, BlockState var2, int var3, int var4);

    default public boolean setBlockState(BlockPos pos, BlockState newState, int flags) {
        return this.setBlockState(pos, newState, flags, 512);
    }

    public boolean removeBlock(BlockPos var1, boolean var2);

    default public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
        return this.destroyBlock(pos, dropBlock, null);
    }

    default public boolean destroyBlock(BlockPos pos, boolean dropBlock, @Nullable Entity entity) {
        return this.destroyBlock(pos, dropBlock, entity, 512);
    }

    public boolean destroyBlock(BlockPos var1, boolean var2, @Nullable Entity var3, int var4);

    default public boolean addEntity(Entity entityIn) {
        return false;
    }
}
