package net.minecraft.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;

public interface ITickList<T> {
    public boolean isTickScheduled(BlockPos var1, T var2);

    default public void scheduleTick(BlockPos pos, T itemIn, int scheduledTime) {
        this.scheduleTick(pos, itemIn, scheduledTime, TickPriority.NORMAL);
    }

    public void scheduleTick(BlockPos var1, T var2, int var3, TickPriority var4);

    public boolean isTickPending(BlockPos var1, T var2);
}
