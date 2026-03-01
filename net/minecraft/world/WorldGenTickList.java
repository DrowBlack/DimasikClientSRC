package net.minecraft.world;

import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ITickList;
import net.minecraft.world.TickPriority;

public class WorldGenTickList<T>
implements ITickList<T> {
    private final Function<BlockPos, ITickList<T>> tickListProvider;

    public WorldGenTickList(Function<BlockPos, ITickList<T>> tickListProviderIn) {
        this.tickListProvider = tickListProviderIn;
    }

    @Override
    public boolean isTickScheduled(BlockPos pos, T itemIn) {
        return this.tickListProvider.apply(pos).isTickScheduled(pos, itemIn);
    }

    @Override
    public void scheduleTick(BlockPos pos, T itemIn, int scheduledTime, TickPriority priority) {
        this.tickListProvider.apply(pos).scheduleTick(pos, itemIn, scheduledTime, priority);
    }

    @Override
    public boolean isTickPending(BlockPos pos, T obj) {
        return false;
    }
}
