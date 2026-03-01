package net.minecraft.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ITickList;
import net.minecraft.world.TickPriority;

public class EmptyTickList<T>
implements ITickList<T> {
    private static final EmptyTickList<Object> INSTANCE = new EmptyTickList();

    public static <T> EmptyTickList<T> get() {
        return INSTANCE;
    }

    @Override
    public boolean isTickScheduled(BlockPos pos, T itemIn) {
        return false;
    }

    @Override
    public void scheduleTick(BlockPos pos, T itemIn, int scheduledTime) {
    }

    @Override
    public void scheduleTick(BlockPos pos, T itemIn, int scheduledTime, TickPriority priority) {
    }

    @Override
    public boolean isTickPending(BlockPos pos, T obj) {
        return false;
    }
}
