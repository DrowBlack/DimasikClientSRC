package net.minecraft.world.chunk;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ChunkManager;

public class ChunkTaskPriorityQueue<T> {
    public static final int MAX_LOADED_LEVELS = ChunkManager.MAX_LOADED_LEVEL + 2 + 32;
    private final List<Long2ObjectLinkedOpenHashMap<List<Optional<T>>>> chunkPriorityQueue = IntStream.range(0, MAX_LOADED_LEVELS).mapToObj(p_lambda$new$0_0_ -> new Long2ObjectLinkedOpenHashMap()).collect(Collectors.toList());
    private volatile int maxLoaded = MAX_LOADED_LEVELS;
    private final String queueName;
    private final LongSet loadedChunks = new LongOpenHashSet();
    private final int priority;

    public ChunkTaskPriorityQueue(String queueName, int priority) {
        this.queueName = queueName;
        this.priority = priority;
    }

    protected void func_219407_a(int p_219407_1_, ChunkPos pos, int p_219407_3_) {
        if (p_219407_1_ < MAX_LOADED_LEVELS) {
            Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2objectlinkedopenhashmap = this.chunkPriorityQueue.get(p_219407_1_);
            List<Optional<T>> list = long2objectlinkedopenhashmap.remove(pos.asLong());
            if (p_219407_1_ == this.maxLoaded) {
                while (this.maxLoaded < MAX_LOADED_LEVELS && this.chunkPriorityQueue.get(this.maxLoaded).isEmpty()) {
                    ++this.maxLoaded;
                }
            }
            if (list != null && !list.isEmpty()) {
                this.chunkPriorityQueue.get(p_219407_3_).computeIfAbsent(pos.asLong(), p_lambda$func_219407_a$1_0_ -> Lists.newArrayList()).addAll(list);
                this.maxLoaded = Math.min(this.maxLoaded, p_219407_3_);
            }
        }
    }

    protected void addTaskToChunk(Optional<T> task, long chunkPos, int chunkLevel) {
        this.chunkPriorityQueue.get(chunkLevel).computeIfAbsent(chunkPos, p_lambda$addTaskToChunk$2_0_ -> Lists.newArrayList()).add(task);
        this.maxLoaded = Math.min(this.maxLoaded, chunkLevel);
    }

    protected void clearChunkFromQueue(long chunkPos, boolean fullClear) {
        for (Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2objectlinkedopenhashmap : this.chunkPriorityQueue) {
            List<Optional<T>> list = long2objectlinkedopenhashmap.get(chunkPos);
            if (list == null) continue;
            if (fullClear) {
                list.clear();
            } else {
                list.removeIf(p_lambda$clearChunkFromQueue$3_0_ -> !p_lambda$clearChunkFromQueue$3_0_.isPresent());
            }
            if (!list.isEmpty()) continue;
            long2objectlinkedopenhashmap.remove(chunkPos);
        }
        while (this.maxLoaded < MAX_LOADED_LEVELS && this.chunkPriorityQueue.get(this.maxLoaded).isEmpty()) {
            ++this.maxLoaded;
        }
        this.loadedChunks.remove(chunkPos);
    }

    private Runnable func_219418_a(long chunkPos) {
        return () -> this.loadedChunks.add(chunkPos);
    }

    @Nullable
    public Stream<Object> func_219417_a() {
        if (this.loadedChunks.size() >= this.priority) {
            return null;
        }
        if (this.maxLoaded >= MAX_LOADED_LEVELS) {
            return null;
        }
        int i = this.maxLoaded;
        Long2ObjectLinkedOpenHashMap<List<Optional<T>>> long2objectlinkedopenhashmap = this.chunkPriorityQueue.get(i);
        long j = long2objectlinkedopenhashmap.firstLongKey();
        List<Optional<T>> list = long2objectlinkedopenhashmap.removeFirst();
        while (this.maxLoaded < MAX_LOADED_LEVELS && this.chunkPriorityQueue.get(this.maxLoaded).isEmpty()) {
            ++this.maxLoaded;
        }
        return list.stream().map(p_lambda$func_219417_a$6_3_ -> p_lambda$func_219417_a$6_3_.map(Either::left).orElseGet(() -> Either.right(this.func_219418_a(j))));
    }

    public String toString() {
        return this.queueName + " " + this.maxLoaded + "...";
    }

    @VisibleForTesting
    LongSet getLoadedChunks() {
        return new LongOpenHashSet(this.loadedChunks);
    }
}
