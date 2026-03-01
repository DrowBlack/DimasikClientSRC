package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.DelegatedTaskExecutor;
import net.minecraft.util.concurrent.ITaskExecutor;
import net.minecraft.util.concurrent.ITaskQueue;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.storage.RegionFileCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IOWorker
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final AtomicBoolean field_227082_c_ = new AtomicBoolean();
    private final DelegatedTaskExecutor<ITaskQueue.RunnableWithPriority> field_235969_c_;
    private final RegionFileCache field_227084_e_;
    private final Map<ChunkPos, Entry> field_227085_f_ = Maps.newLinkedHashMap();

    protected IOWorker(File p_i231890_1_, boolean p_i231890_2_, String p_i231890_3_) {
        this.field_227084_e_ = new RegionFileCache(p_i231890_1_, p_i231890_2_);
        this.field_235969_c_ = new DelegatedTaskExecutor<ITaskQueue.RunnableWithPriority>(new ITaskQueue.Priority(Priority.values().length), Util.getRenderingService(), "IOWorker-" + p_i231890_3_);
    }

    public CompletableFuture<Void> func_227093_a_(ChunkPos p_227093_1_, CompoundNBT p_227093_2_) {
        return this.func_235975_a_(() -> {
            Entry ioworker$entry = this.field_227085_f_.computeIfAbsent(p_227093_1_, p_235977_1_ -> new Entry(p_227093_2_));
            ioworker$entry.field_227113_a_ = p_227093_2_;
            return Either.left(ioworker$entry.field_227114_b_);
        }).thenCompose(Function.identity());
    }

    @Nullable
    public CompoundNBT func_227090_a_(ChunkPos p_227090_1_) throws IOException {
        CompletableFuture completablefuture = this.func_235975_a_(() -> {
            Entry ioworker$entry = this.field_227085_f_.get(p_227090_1_);
            if (ioworker$entry != null) {
                return Either.left(ioworker$entry.field_227113_a_);
            }
            try {
                CompoundNBT compoundnbt = this.field_227084_e_.readChunk(p_227090_1_);
                return Either.left(compoundnbt);
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to read chunk {}", (Object)p_227090_1_, (Object)exception);
                return Either.right(exception);
            }
        });
        try {
            return (CompoundNBT)completablefuture.join();
        }
        catch (CompletionException completionexception) {
            if (completionexception.getCause() instanceof IOException) {
                throw (IOException)completionexception.getCause();
            }
            throw completionexception;
        }
    }

    public CompletableFuture<Void> func_227088_a_() {
        CompletionStage completablefuture = this.func_235975_a_(() -> Either.left(CompletableFuture.allOf((CompletableFuture[])this.field_227085_f_.values().stream().map(p_235973_0_ -> p_235973_0_.field_227114_b_).toArray(CompletableFuture[]::new)))).thenCompose(Function.identity());
        return ((CompletableFuture)completablefuture).thenCompose(p_235974_1_ -> this.func_235975_a_(() -> {
            try {
                this.field_227084_e_.func_235987_a_();
                return Either.left(null);
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to synchronized chunks", (Throwable)exception);
                return Either.right(exception);
            }
        }));
    }

    private <T> CompletableFuture<T> func_235975_a_(Supplier<Either<T, Exception>> p_235975_1_) {
        return this.field_235969_c_.func_233528_c_(p_235976_2_ -> new ITaskQueue.RunnableWithPriority(Priority.HIGH.ordinal(), () -> this.lambda$func_235975_a_$8(p_235976_2_, (Supplier)p_235975_1_)));
    }

    private void func_235978_b_() {
        Iterator<Map.Entry<ChunkPos, Entry>> iterator = this.field_227085_f_.entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<ChunkPos, Entry> entry = iterator.next();
            iterator.remove();
            this.func_227091_a_(entry.getKey(), entry.getValue());
            this.func_235982_c_();
        }
    }

    private void func_235982_c_() {
        this.field_235969_c_.enqueue(new ITaskQueue.RunnableWithPriority(Priority.LOW.ordinal(), this::func_235978_b_));
    }

    private void func_227091_a_(ChunkPos p_227091_1_, Entry p_227091_2_) {
        try {
            this.field_227084_e_.writeChunk(p_227091_1_, p_227091_2_.field_227113_a_);
            p_227091_2_.field_227114_b_.complete(null);
        }
        catch (Exception exception) {
            LOGGER.error("Failed to store chunk {}", (Object)p_227091_1_, (Object)exception);
            p_227091_2_.field_227114_b_.completeExceptionally(exception);
        }
    }

    @Override
    public void close() throws IOException {
        if (this.field_227082_c_.compareAndSet(false, true)) {
            CompletableFuture completablefuture = this.field_235969_c_.func_213141_a(p_235971_0_ -> new ITaskQueue.RunnableWithPriority(Priority.HIGH.ordinal(), () -> p_235971_0_.enqueue(Unit.INSTANCE)));
            try {
                completablefuture.join();
            }
            catch (CompletionException completionexception) {
                if (completionexception.getCause() instanceof IOException) {
                    throw (IOException)completionexception.getCause();
                }
                throw completionexception;
            }
            this.field_235969_c_.close();
            this.field_227085_f_.forEach(this::func_227091_a_);
            this.field_227085_f_.clear();
            try {
                this.field_227084_e_.close();
            }
            catch (Exception exception) {
                LOGGER.error("Failed to close storage", (Throwable)exception);
            }
        }
    }

    private /* synthetic */ void lambda$func_235975_a_$8(ITaskExecutor p_235976_2_, Supplier p_235975_1_) {
        if (!this.field_227082_c_.get()) {
            p_235976_2_.enqueue((Either)p_235975_1_.get());
        }
        this.func_235982_c_();
    }

    static enum Priority {
        HIGH,
        LOW;

    }

    static class Entry {
        private CompoundNBT field_227113_a_;
        private final CompletableFuture<Void> field_227114_b_ = new CompletableFuture();

        public Entry(CompoundNBT p_i231891_1_) {
            this.field_227113_a_ = p_i231891_1_;
        }
    }
}
