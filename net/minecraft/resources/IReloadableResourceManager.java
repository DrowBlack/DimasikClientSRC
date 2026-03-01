package net.minecraft.resources;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resources.IAsyncReloader;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.util.Unit;

public interface IReloadableResourceManager
extends IResourceManager,
AutoCloseable {
    default public CompletableFuture<Unit> reloadResourcesAndThen(Executor backgroundExecutor, Executor gameExecutor, List<IResourcePack> resourcePacks, CompletableFuture<Unit> waitingFor) {
        return this.reloadResources(backgroundExecutor, gameExecutor, waitingFor, resourcePacks).onceDone();
    }

    public IAsyncReloader reloadResources(Executor var1, Executor var2, CompletableFuture<Unit> var3, List<IResourcePack> var4);

    public void addReloadListener(IFutureReloadListener var1);

    @Override
    public void close();
}
