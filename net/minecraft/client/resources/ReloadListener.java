package net.minecraft.client.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;

public abstract class ReloadListener<T>
implements IFutureReloadListener {
    @Override
    public final CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return ((CompletableFuture)CompletableFuture.supplyAsync(() -> this.prepare(resourceManager, preparationsProfiler), backgroundExecutor).thenCompose(stage::markCompleteAwaitingOthers)).thenAcceptAsync(p_215269_3_ -> this.apply(p_215269_3_, resourceManager, reloadProfiler), gameExecutor);
    }

    protected abstract T prepare(IResourceManager var1, IProfiler var2);

    protected abstract void apply(T var1, IResourceManager var2, IProfiler var3);
}
