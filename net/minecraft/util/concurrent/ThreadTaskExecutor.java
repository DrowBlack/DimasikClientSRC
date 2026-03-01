package net.minecraft.util.concurrent;

import com.google.common.collect.Queues;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SUnloadChunkPacket;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.util.concurrent.ITaskExecutor;
import net.optifine.Config;
import net.optifine.util.PacketRunnable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ThreadTaskExecutor<R extends Runnable>
implements ITaskExecutor<R>,
Executor {
    private final String name;
    private static final Logger LOGGER = LogManager.getLogger();
    private final Queue<R> queue = Queues.newConcurrentLinkedQueue();
    private int drivers;

    protected ThreadTaskExecutor(String nameIn) {
        this.name = nameIn;
    }

    protected abstract R wrapTask(Runnable var1);

    protected abstract boolean canRun(R var1);

    public boolean isOnExecutionThread() {
        return Thread.currentThread() == this.getExecutionThread();
    }

    protected abstract Thread getExecutionThread();

    protected boolean shouldDeferTasks() {
        return !this.isOnExecutionThread();
    }

    public int getQueueSize() {
        return this.queue.size();
    }

    @Override
    public String getName() {
        return this.name;
    }

    public <V> CompletableFuture<V> supplyAsync(Supplier<V> supplier) {
        return this.shouldDeferTasks() ? CompletableFuture.supplyAsync(supplier, this) : CompletableFuture.completedFuture(supplier.get());
    }

    private CompletableFuture<Void> deferTask(Runnable taskIn) {
        return CompletableFuture.supplyAsync(() -> {
            taskIn.run();
            return null;
        }, this);
    }

    public CompletableFuture<Void> runAsync(Runnable taskIn) {
        if (this.shouldDeferTasks()) {
            return this.deferTask(taskIn);
        }
        taskIn.run();
        return CompletableFuture.completedFuture(null);
    }

    public void runImmediately(Runnable taskIn) {
        if (!this.isOnExecutionThread()) {
            this.deferTask(taskIn).join();
        } else {
            taskIn.run();
        }
    }

    @Override
    public void enqueue(R taskIn) {
        this.queue.add(taskIn);
        LockSupport.unpark(this.getExecutionThread());
    }

    @Override
    public void execute(Runnable p_execute_1_) {
        if (this.shouldDeferTasks()) {
            this.enqueue(this.wrapTask(p_execute_1_));
        } else {
            p_execute_1_.run();
        }
    }

    protected void dropTasks() {
        this.queue.clear();
    }

    protected void drainTasks() {
        int i = Integer.MAX_VALUE;
        if (Config.isLazyChunkLoading() && this == Minecraft.getInstance()) {
            i = this.getTaskCount();
        }
        while (this.driveOne() && --i > 0) {
        }
    }

    protected boolean driveOne() {
        Runnable r = (Runnable)this.queue.peek();
        if (r == null) {
            return false;
        }
        if (this.drivers == 0 && !this.canRun(r)) {
            return false;
        }
        this.run((Runnable)this.queue.remove());
        return true;
    }

    public void driveUntil(BooleanSupplier isDone) {
        ++this.drivers;
        try {
            while (!isDone.getAsBoolean()) {
                if (this.driveOne()) continue;
                this.threadYieldPark();
            }
        }
        finally {
            --this.drivers;
        }
    }

    protected void threadYieldPark() {
        Thread.yield();
        LockSupport.parkNanos("waiting for tasks", 100000L);
    }

    protected void run(R taskIn) {
        block2: {
            try {
                taskIn.run();
            }
            catch (Exception exception) {
                LOGGER.fatal("Error executing task on {}", (Object)this.getName(), (Object)exception);
                if (!(exception.getCause() instanceof OutOfMemoryError)) break block2;
                OutOfMemoryError outofmemoryerror = (OutOfMemoryError)exception.getCause();
                throw outofmemoryerror;
            }
        }
    }

    private int getTaskCount() {
        if (this.queue.isEmpty()) {
            return 0;
        }
        Runnable[] ar = this.queue.toArray(new Runnable[this.queue.size()]);
        double d0 = this.getChunkUpdateWeight(ar);
        if (d0 < 5.0) {
            return Integer.MAX_VALUE;
        }
        int i = ar.length;
        int j = Math.max(Config.getFpsAverage(), 1);
        double d1 = i * 10 / j;
        return this.getCount(ar, d1);
    }

    private int getCount(R[] p_getCount_1_, double p_getCount_2_) {
        double d0 = 0.0;
        for (int i = 0; i < p_getCount_1_.length; ++i) {
            R r = p_getCount_1_[i];
            if (!((d0 += this.getChunkUpdateWeight((Runnable)r)) > p_getCount_2_)) continue;
            return i + 1;
        }
        return p_getCount_1_.length;
    }

    private double getChunkUpdateWeight(R[] p_getChunkUpdateWeight_1_) {
        double d0 = 0.0;
        for (int i = 0; i < p_getChunkUpdateWeight_1_.length; ++i) {
            R r = p_getChunkUpdateWeight_1_[i];
            d0 += this.getChunkUpdateWeight((Runnable)r);
        }
        return d0;
    }

    private double getChunkUpdateWeight(Runnable p_getChunkUpdateWeight_1_) {
        if (p_getChunkUpdateWeight_1_ instanceof PacketRunnable) {
            PacketRunnable packetrunnable = (PacketRunnable)p_getChunkUpdateWeight_1_;
            IPacket ipacket = packetrunnable.getPacket();
            if (ipacket instanceof SChunkDataPacket) {
                return 1.0;
            }
            if (ipacket instanceof SUpdateLightPacket) {
                return 0.2;
            }
            if (ipacket instanceof SUnloadChunkPacket) {
                return 2.6;
            }
        }
        return 0.0;
    }
}
