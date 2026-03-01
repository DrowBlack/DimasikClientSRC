package net.minecraft.util.concurrent;

import net.minecraft.util.concurrent.ThreadTaskExecutor;

public abstract class RecursiveEventLoop<R extends Runnable>
extends ThreadTaskExecutor<R> {
    private int running;

    public RecursiveEventLoop(String name) {
        super(name);
    }

    @Override
    protected boolean shouldDeferTasks() {
        return this.isTaskRunning() || super.shouldDeferTasks();
    }

    protected boolean isTaskRunning() {
        return this.running != 0;
    }

    @Override
    protected void run(R taskIn) {
        ++this.running;
        try {
            super.run(taskIn);
        }
        finally {
            --this.running;
        }
    }
}
