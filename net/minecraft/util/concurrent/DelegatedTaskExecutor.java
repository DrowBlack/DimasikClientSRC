package net.minecraft.util.concurrent;

import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.concurrent.ITaskExecutor;
import net.minecraft.util.concurrent.ITaskQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DelegatedTaskExecutor<T>
implements ITaskExecutor<T>,
AutoCloseable,
Runnable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final AtomicInteger flags = new AtomicInteger(0);
    public final ITaskQueue<? super T, ? extends Runnable> queue;
    private final Executor delegate;
    private final String name;

    public static DelegatedTaskExecutor<Runnable> create(Executor p_213144_0_, String p_213144_1_) {
        return new DelegatedTaskExecutor<Runnable>(new ITaskQueue.Single(new ConcurrentLinkedQueue()), p_213144_0_, p_213144_1_);
    }

    public DelegatedTaskExecutor(ITaskQueue<? super T, ? extends Runnable> queueIn, Executor delegateIn, String nameIn) {
        this.delegate = delegateIn;
        this.queue = queueIn;
        this.name = nameIn;
    }

    private boolean setActive() {
        int i;
        do {
            if (((i = this.flags.get()) & 3) == 0) continue;
            return false;
        } while (!this.flags.compareAndSet(i, i | 2));
        return true;
    }

    private void clearActive() {
        int i;
        while (!this.flags.compareAndSet(i = this.flags.get(), i & 0xFFFFFFFD)) {
        }
    }

    private boolean shouldSchedule() {
        if ((this.flags.get() & 1) != 0) {
            return false;
        }
        return !this.queue.isEmpty();
    }

    @Override
    public void close() {
        int i;
        while (!this.flags.compareAndSet(i = this.flags.get(), i | 1)) {
        }
    }

    private boolean isActive() {
        return (this.flags.get() & 2) != 0;
    }

    private boolean driveOne() {
        String s;
        Thread thread;
        if (!this.isActive()) {
            return false;
        }
        Runnable runnable = this.queue.poll();
        if (runnable == null) {
            return false;
        }
        if (SharedConstants.developmentMode) {
            thread = Thread.currentThread();
            s = thread.getName();
            thread.setName(this.name);
        } else {
            thread = null;
            s = null;
        }
        runnable.run();
        if (thread != null) {
            thread.setName(s);
        }
        return true;
    }

    @Override
    public void run() {
        try {
            this.driveWhile(p_213147_0_ -> p_213147_0_ == 0);
        }
        finally {
            this.clearActive();
            this.reschedule();
        }
    }

    @Override
    public void enqueue(T taskIn) {
        this.queue.enqueue(taskIn);
        this.reschedule();
    }

    private void reschedule() {
        if (this.shouldSchedule() && this.setActive()) {
            try {
                this.delegate.execute(this);
            }
            catch (RejectedExecutionException rejectedexecutionexception1) {
                try {
                    this.delegate.execute(this);
                }
                catch (RejectedExecutionException rejectedexecutionexception) {
                    LOGGER.error("Cound not schedule mailbox", (Throwable)rejectedexecutionexception);
                }
            }
        }
    }

    private int driveWhile(Int2BooleanFunction p_213145_1_) {
        int i = 0;
        while (p_213145_1_.get(i) && this.driveOne()) {
            ++i;
        }
        return i;
    }

    public String toString() {
        return this.name + " " + this.flags.get() + " " + this.queue.isEmpty();
    }

    @Override
    public String getName() {
        return this.name;
    }
}
