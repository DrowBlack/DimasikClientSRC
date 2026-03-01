package net.minecraft.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.profiler.EmptyProfiler;
import net.minecraft.resources.IAsyncReloader;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;

public class AsyncReloader<S>
implements IAsyncReloader {
    protected final IResourceManager resourceManager;
    protected final CompletableFuture<Unit> allAsyncCompleted = new CompletableFuture();
    protected final CompletableFuture<List<S>> resultListFuture;
    private final Set<IFutureReloadListener> taskSet;
    private final int taskCount;
    private int syncScheduled;
    private int syncCompleted;
    private final AtomicInteger asyncScheduled = new AtomicInteger();
    private final AtomicInteger asyncCompleted = new AtomicInteger();

    public static AsyncReloader<Void> create(IResourceManager resourceManager, List<IFutureReloadListener> listeners, Executor backgroundExecutor, Executor gameExecutor, CompletableFuture<Unit> alsoWaitedFor) {
        return new AsyncReloader<Void>(backgroundExecutor, gameExecutor, resourceManager, listeners, (stage, resourceManager2, preparationsProfiler, p_219561_4_, p_219561_5_) -> preparationsProfiler.reload(stage, resourceManager2, EmptyProfiler.INSTANCE, EmptyProfiler.INSTANCE, backgroundExecutor, p_219561_5_), alsoWaitedFor);
    }

    protected AsyncReloader(Executor backgroundExecutor, final Executor gameExecutor, IResourceManager resourceManager, List<IFutureReloadListener> listeners, IStateFactory<S> stateFactory, CompletableFuture<Unit> alsoWaitedFor) {
        this.resourceManager = resourceManager;
        this.taskCount = listeners.size();
        this.asyncScheduled.incrementAndGet();
        alsoWaitedFor.thenRun(this.asyncCompleted::incrementAndGet);
        ArrayList<CompletableFuture<S>> list = Lists.newArrayList();
        CompletableFuture<Unit> completablefuture = alsoWaitedFor;
        this.taskSet = Sets.newHashSet(listeners);
        for (final IFutureReloadListener ifuturereloadlistener : listeners) {
            final CompletableFuture<Unit> completablefuture1 = completablefuture;
            CompletableFuture<S> completablefuture2 = stateFactory.create(new IFutureReloadListener.IStage(){

                @Override
                public <T> CompletableFuture<T> markCompleteAwaitingOthers(T backgroundResult) {
                    gameExecutor.execute(() -> {
                        AsyncReloader.this.taskSet.remove(ifuturereloadlistener);
                        if (AsyncReloader.this.taskSet.isEmpty()) {
                            AsyncReloader.this.allAsyncCompleted.complete(Unit.INSTANCE);
                        }
                    });
                    return AsyncReloader.this.allAsyncCompleted.thenCombine((CompletionStage)completablefuture1, (unit, instance) -> backgroundResult);
                }
            }, resourceManager, ifuturereloadlistener, runnable -> {
                this.asyncScheduled.incrementAndGet();
                backgroundExecutor.execute(() -> {
                    runnable.run();
                    this.asyncCompleted.incrementAndGet();
                });
            }, runnable -> {
                ++this.syncScheduled;
                gameExecutor.execute(() -> {
                    runnable.run();
                    ++this.syncCompleted;
                });
            });
            list.add(completablefuture2);
            completablefuture = completablefuture2;
        }
        this.resultListFuture = Util.gather(list);
    }

    @Override
    public CompletableFuture<Unit> onceDone() {
        return this.resultListFuture.thenApply(result -> Unit.INSTANCE);
    }

    @Override
    public float estimateExecutionSpeed() {
        int i = this.taskCount - this.taskSet.size();
        float f = this.asyncCompleted.get() * 2 + this.syncCompleted * 2 + i * 1;
        float f1 = this.asyncScheduled.get() * 2 + this.syncScheduled * 2 + this.taskCount * 1;
        return f / f1;
    }

    @Override
    public boolean asyncPartDone() {
        return this.allAsyncCompleted.isDone();
    }

    @Override
    public boolean fullyDone() {
        return this.resultListFuture.isDone();
    }

    @Override
    public void join() {
        if (this.resultListFuture.isCompletedExceptionally()) {
            this.resultListFuture.join();
        }
    }

    public static interface IStateFactory<S> {
        public CompletableFuture<S> create(IFutureReloadListener.IStage var1, IResourceManager var2, IFutureReloadListener var3, Executor var4, Executor var5);
    }
}
