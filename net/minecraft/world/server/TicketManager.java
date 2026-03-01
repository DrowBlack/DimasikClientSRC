package net.minecraft.world.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SortedArraySet;
import net.minecraft.util.concurrent.ITaskExecutor;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkDistanceGraph;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkTaskPriorityQueueSorter;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.Ticket;
import net.minecraft.world.server.TicketType;
import net.optifine.reflect.Reflector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TicketManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int PLAYER_TICKET_LEVEL = 33 + ChunkStatus.getDistance(ChunkStatus.FULL) - 2;
    private final Long2ObjectMap<ObjectSet<ServerPlayerEntity>> playersByChunkPos = new Long2ObjectOpenHashMap<ObjectSet<ServerPlayerEntity>>();
    private final Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> tickets = new Long2ObjectOpenHashMap();
    private final ChunkTicketTracker ticketTracker = new ChunkTicketTracker();
    private final PlayerChunkTracker playerChunkTracker = new PlayerChunkTracker(8);
    private final PlayerTicketTracker playerTicketTracker = new PlayerTicketTracker(65);
    private final Set<ChunkHolder> chunkHolders = Sets.newHashSet();
    private final ChunkTaskPriorityQueueSorter field_219384_l;
    private final ITaskExecutor<ChunkTaskPriorityQueueSorter.FunctionEntry<Runnable>> field_219385_m;
    private final ITaskExecutor<ChunkTaskPriorityQueueSorter.RunnableEntry> field_219386_n;
    private final LongSet chunkPositions = new LongOpenHashSet();
    private final Executor field_219388_p;
    private long currentTime;
    private final Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> forcedTickets = new Long2ObjectOpenHashMap();

    protected TicketManager(Executor p_i50707_1_, Executor p_i50707_2_) {
        ChunkTaskPriorityQueueSorter chunktaskpriorityqueuesorter;
        ITaskExecutor<Runnable> itaskexecutor = ITaskExecutor.inline("player ticket throttler", p_i50707_2_::execute);
        this.field_219384_l = chunktaskpriorityqueuesorter = new ChunkTaskPriorityQueueSorter(ImmutableList.of(itaskexecutor), p_i50707_1_, 4);
        this.field_219385_m = chunktaskpriorityqueuesorter.func_219087_a(itaskexecutor, true);
        this.field_219386_n = chunktaskpriorityqueuesorter.func_219091_a(itaskexecutor);
        this.field_219388_p = p_i50707_2_;
    }

    protected void tick() {
        ++this.currentTime;
        ObjectIterator objectiterator = this.tickets.long2ObjectEntrySet().fastIterator();
        while (objectiterator.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)objectiterator.next();
            if (((SortedArraySet)entry.getValue()).removeIf(p_lambda$tick$0_1_ -> p_lambda$tick$0_1_.isExpired(this.currentTime))) {
                this.ticketTracker.updateSourceLevel(entry.getLongKey(), TicketManager.getLevel((SortedArraySet)entry.getValue()), false);
            }
            if (!((SortedArraySet)entry.getValue()).isEmpty()) continue;
            objectiterator.remove();
        }
    }

    private static int getLevel(SortedArraySet<Ticket<?>> p_229844_0_) {
        return !p_229844_0_.isEmpty() ? p_229844_0_.getSmallest().getLevel() : ChunkManager.MAX_LOADED_LEVEL + 1;
    }

    protected abstract boolean contains(long var1);

    @Nullable
    protected abstract ChunkHolder getChunkHolder(long var1);

    @Nullable
    protected abstract ChunkHolder setChunkLevel(long var1, int var3, @Nullable ChunkHolder var4, int var5);

    public boolean processUpdates(ChunkManager chunkManager) {
        boolean flag;
        this.playerChunkTracker.processAllUpdates();
        this.playerTicketTracker.processAllUpdates();
        int i = Integer.MAX_VALUE - this.ticketTracker.func_215493_a(Integer.MAX_VALUE);
        boolean bl = flag = i != 0;
        if (flag) {
            // empty if block
        }
        if (!this.chunkHolders.isEmpty()) {
            this.chunkHolders.forEach(p_lambda$processUpdates$1_1_ -> p_lambda$processUpdates$1_1_.processUpdates(chunkManager));
            this.chunkHolders.clear();
            return true;
        }
        if (!this.chunkPositions.isEmpty()) {
            LongIterator longiterator = this.chunkPositions.iterator();
            while (longiterator.hasNext()) {
                long j = longiterator.nextLong();
                if (!this.getTicketSet(j).stream().anyMatch(p_lambda$processUpdates$2_0_ -> p_lambda$processUpdates$2_0_.getType() == TicketType.PLAYER)) continue;
                ChunkHolder chunkholder = chunkManager.func_219220_a(j);
                if (chunkholder == null) {
                    throw new IllegalStateException();
                }
                CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> completablefuture = chunkholder.getEntityTickingFuture();
                completablefuture.thenAccept(p_lambda$processUpdates$5_3_ -> this.field_219388_p.execute(() -> this.field_219386_n.enqueue(ChunkTaskPriorityQueueSorter.func_219073_a(() -> {}, j, false))));
            }
            this.chunkPositions.clear();
        }
        return flag;
    }

    private void register(long chunkPosIn, Ticket<?> ticketIn) {
        SortedArraySet<Ticket<?>> sortedarrayset = this.getTicketSet(chunkPosIn);
        int i = TicketManager.getLevel(sortedarrayset);
        Ticket<?> ticket = sortedarrayset.func_226175_a_(ticketIn);
        ticket.setTimestamp(this.currentTime);
        if (ticketIn.getLevel() < i) {
            this.ticketTracker.updateSourceLevel(chunkPosIn, ticketIn.getLevel(), true);
        }
        if (Reflector.callBoolean(ticketIn, Reflector.ForgeTicket_isForceTicks, new Object[0])) {
            SortedArraySet sortedarrayset1 = this.forcedTickets.computeIfAbsent(chunkPosIn, p_lambda$register$6_0_ -> SortedArraySet.newSet(4));
            sortedarrayset1.func_226175_a_(ticket);
        }
    }

    private void release(long chunkPosIn, Ticket<?> ticketIn) {
        SortedArraySet<Ticket<?>> sortedarrayset1;
        SortedArraySet<Ticket<?>> sortedarrayset = this.getTicketSet(chunkPosIn);
        if (sortedarrayset.remove(ticketIn)) {
            // empty if block
        }
        if (sortedarrayset.isEmpty()) {
            this.tickets.remove(chunkPosIn);
        }
        this.ticketTracker.updateSourceLevel(chunkPosIn, TicketManager.getLevel(sortedarrayset), false);
        if (Reflector.callBoolean(ticketIn, Reflector.ForgeTicket_isForceTicks, new Object[0]) && (sortedarrayset1 = this.forcedTickets.get(chunkPosIn)) != null) {
            sortedarrayset1.remove(ticketIn);
        }
    }

    public <T> void registerWithLevel(TicketType<T> type, ChunkPos pos, int level, T value) {
        this.register(pos.asLong(), new Ticket<T>(type, level, value));
    }

    public <T> void releaseWithLevel(TicketType<T> type, ChunkPos pos, int level, T value) {
        Ticket<T> ticket = new Ticket<T>(type, level, value);
        this.release(pos.asLong(), ticket);
    }

    public <T> void register(TicketType<T> type, ChunkPos pos, int distance, T value) {
        this.register(pos.asLong(), new Ticket<T>(type, 33 - distance, value));
    }

    public <T> void release(TicketType<T> type, ChunkPos pos, int distance, T value) {
        Ticket<T> ticket = new Ticket<T>(type, 33 - distance, value);
        this.release(pos.asLong(), ticket);
    }

    private SortedArraySet<Ticket<?>> getTicketSet(long p_229848_1_) {
        return this.tickets.computeIfAbsent(p_229848_1_, p_lambda$getTicketSet$7_0_ -> SortedArraySet.newSet(4));
    }

    protected void forceChunk(ChunkPos pos, boolean add) {
        Ticket<ChunkPos> ticket = new Ticket<ChunkPos>(TicketType.FORCED, 31, pos);
        if (add) {
            this.register(pos.asLong(), ticket);
        } else {
            this.release(pos.asLong(), ticket);
        }
    }

    public void updatePlayerPosition(SectionPos sectionPosIn, ServerPlayerEntity player) {
        long i = sectionPosIn.asChunkPos().asLong();
        this.playersByChunkPos.computeIfAbsent(i, p_lambda$updatePlayerPosition$8_0_ -> new ObjectOpenHashSet()).add(player);
        this.playerChunkTracker.updateSourceLevel(i, 0, true);
        this.playerTicketTracker.updateSourceLevel(i, 0, true);
    }

    public void removePlayer(SectionPos sectionPosIn, ServerPlayerEntity player) {
        long i = sectionPosIn.asChunkPos().asLong();
        ObjectSet objectset = (ObjectSet)this.playersByChunkPos.get(i);
        objectset.remove(player);
        if (objectset.isEmpty()) {
            this.playersByChunkPos.remove(i);
            this.playerChunkTracker.updateSourceLevel(i, Integer.MAX_VALUE, false);
            this.playerTicketTracker.updateSourceLevel(i, Integer.MAX_VALUE, false);
        }
    }

    protected String func_225413_c(long p_225413_1_) {
        SortedArraySet<Ticket<?>> sortedarrayset = this.tickets.get(p_225413_1_);
        String s = sortedarrayset != null && !sortedarrayset.isEmpty() ? sortedarrayset.getSmallest().toString() : "no_ticket";
        return s;
    }

    protected void setViewDistance(int viewDistance) {
        this.playerTicketTracker.setViewDistance(viewDistance);
    }

    public int getSpawningChunksCount() {
        this.playerChunkTracker.processAllUpdates();
        return this.playerChunkTracker.chunksInRange.size();
    }

    public boolean isOutsideSpawningRadius(long chunkPosIn) {
        this.playerChunkTracker.processAllUpdates();
        return this.playerChunkTracker.chunksInRange.containsKey(chunkPosIn);
    }

    public String func_225412_c() {
        return this.field_219384_l.func_225396_a();
    }

    public <T> void registerTicking(TicketType<T> p_registerTicking_1_, ChunkPos p_registerTicking_2_, int p_registerTicking_3_, T p_registerTicking_4_) {
        Ticket ticket = (Ticket)Reflector.ForgeTicket_Constructor.newInstance(p_registerTicking_1_, 33 - p_registerTicking_3_, p_registerTicking_4_, true);
        this.register(p_registerTicking_2_.asLong(), ticket);
    }

    public <T> void releaseTicking(TicketType<T> p_releaseTicking_1_, ChunkPos p_releaseTicking_2_, int p_releaseTicking_3_, T p_releaseTicking_4_) {
        Ticket ticket = (Ticket)Reflector.ForgeTicket_Constructor.newInstance(p_releaseTicking_1_, 33 - p_releaseTicking_3_, p_releaseTicking_4_, true);
        this.release(p_releaseTicking_2_.asLong(), ticket);
    }

    public boolean shouldForceTicks(long p_shouldForceTicks_1_) {
        SortedArraySet<Ticket<?>> sortedarrayset = this.forcedTickets.get(p_shouldForceTicks_1_);
        return sortedarrayset != null && !sortedarrayset.isEmpty();
    }

    class ChunkTicketTracker
    extends ChunkDistanceGraph {
        public ChunkTicketTracker() {
            super(ChunkManager.MAX_LOADED_LEVEL + 2, 256, 256);
        }

        @Override
        protected int getSourceLevel(long pos) {
            SortedArraySet<Ticket<?>> sortedarrayset = TicketManager.this.tickets.get(pos);
            if (sortedarrayset == null) {
                return Integer.MAX_VALUE;
            }
            return sortedarrayset.isEmpty() ? Integer.MAX_VALUE : sortedarrayset.getSmallest().getLevel();
        }

        @Override
        protected int getLevel(long sectionPosIn) {
            ChunkHolder chunkholder;
            if (!TicketManager.this.contains(sectionPosIn) && (chunkholder = TicketManager.this.getChunkHolder(sectionPosIn)) != null) {
                return chunkholder.getChunkLevel();
            }
            return ChunkManager.MAX_LOADED_LEVEL + 1;
        }

        @Override
        protected void setLevel(long sectionPosIn, int level) {
            int i;
            ChunkHolder chunkholder = TicketManager.this.getChunkHolder(sectionPosIn);
            int n = i = chunkholder == null ? ChunkManager.MAX_LOADED_LEVEL + 1 : chunkholder.getChunkLevel();
            if (i != level && (chunkholder = TicketManager.this.setChunkLevel(sectionPosIn, level, chunkholder, i)) != null) {
                TicketManager.this.chunkHolders.add(chunkholder);
            }
        }

        public int func_215493_a(int toUpdateCount) {
            return this.processUpdates(toUpdateCount);
        }
    }

    class PlayerChunkTracker
    extends ChunkDistanceGraph {
        protected final Long2ByteMap chunksInRange;
        protected final int range;

        protected PlayerChunkTracker(int levelCount) {
            super(levelCount + 2, 2048, 2048);
            this.chunksInRange = new Long2ByteOpenHashMap();
            this.range = levelCount;
            this.chunksInRange.defaultReturnValue((byte)(levelCount + 2));
        }

        @Override
        protected int getLevel(long sectionPosIn) {
            return this.chunksInRange.get(sectionPosIn);
        }

        @Override
        protected void setLevel(long sectionPosIn, int level) {
            byte b0 = level > this.range ? this.chunksInRange.remove(sectionPosIn) : this.chunksInRange.put(sectionPosIn, (byte)level);
            this.chunkLevelChanged(sectionPosIn, b0, level);
        }

        protected void chunkLevelChanged(long chunkPosIn, int oldLevel, int newLevel) {
        }

        @Override
        protected int getSourceLevel(long pos) {
            return this.hasPlayerInChunk(pos) ? 0 : Integer.MAX_VALUE;
        }

        private boolean hasPlayerInChunk(long chunkPosIn) {
            ObjectSet objectset = (ObjectSet)TicketManager.this.playersByChunkPos.get(chunkPosIn);
            return objectset != null && !objectset.isEmpty();
        }

        public void processAllUpdates() {
            this.processUpdates(Integer.MAX_VALUE);
        }
    }

    class PlayerTicketTracker
    extends PlayerChunkTracker {
        private int viewDistance;
        private final Long2IntMap field_215513_f;
        private final LongSet field_215514_g;

        protected PlayerTicketTracker(int p_i50682_2_) {
            super(p_i50682_2_);
            this.field_215513_f = Long2IntMaps.synchronize(new Long2IntOpenHashMap());
            this.field_215514_g = new LongOpenHashSet();
            this.viewDistance = 0;
            this.field_215513_f.defaultReturnValue(p_i50682_2_ + 2);
        }

        @Override
        protected void chunkLevelChanged(long chunkPosIn, int oldLevel, int newLevel) {
            this.field_215514_g.add(chunkPosIn);
        }

        public void setViewDistance(int viewDistanceIn) {
            for (Long2ByteMap.Entry entry : this.chunksInRange.long2ByteEntrySet()) {
                byte b0 = entry.getByteValue();
                long i = entry.getLongKey();
                this.func_215504_a(i, b0, this.func_215505_c(b0), b0 <= viewDistanceIn - 2);
            }
            this.viewDistance = viewDistanceIn;
        }

        private void func_215504_a(long chunkPosIn, int p_215504_3_, boolean p_215504_4_, boolean p_215504_5_) {
            if (p_215504_4_ != p_215504_5_) {
                Ticket<ChunkPos> ticket = new Ticket<ChunkPos>(TicketType.PLAYER, PLAYER_TICKET_LEVEL, new ChunkPos(chunkPosIn));
                if (p_215504_5_) {
                    TicketManager.this.field_219385_m.enqueue(ChunkTaskPriorityQueueSorter.func_219069_a(() -> TicketManager.this.field_219388_p.execute(() -> {
                        if (this.func_215505_c(this.getLevel(chunkPosIn))) {
                            TicketManager.this.register(chunkPosIn, ticket);
                            TicketManager.this.chunkPositions.add(chunkPosIn);
                        } else {
                            TicketManager.this.field_219386_n.enqueue(ChunkTaskPriorityQueueSorter.func_219073_a(() -> {}, chunkPosIn, false));
                        }
                    }), chunkPosIn, () -> p_215504_3_));
                } else {
                    TicketManager.this.field_219386_n.enqueue(ChunkTaskPriorityQueueSorter.func_219073_a(() -> TicketManager.this.field_219388_p.execute(() -> TicketManager.this.release(chunkPosIn, ticket)), chunkPosIn, true));
                }
            }
        }

        @Override
        public void processAllUpdates() {
            super.processAllUpdates();
            if (!this.field_215514_g.isEmpty()) {
                LongIterator longiterator = this.field_215514_g.iterator();
                while (longiterator.hasNext()) {
                    int k;
                    long i = longiterator.nextLong();
                    int j = this.field_215513_f.get(i);
                    if (j == (k = this.getLevel(i))) continue;
                    TicketManager.this.field_219384_l.func_219066_a(new ChunkPos(i), () -> this.field_215513_f.get(i), k, p_lambda$processAllUpdates$7_3_ -> {
                        if (p_lambda$processAllUpdates$7_3_ >= this.field_215513_f.defaultReturnValue()) {
                            this.field_215513_f.remove(i);
                        } else {
                            this.field_215513_f.put(i, p_lambda$processAllUpdates$7_3_);
                        }
                    });
                    this.func_215504_a(i, k, this.func_215505_c(j), this.func_215505_c(k));
                }
                this.field_215514_g.clear();
            }
        }

        private boolean func_215505_c(int p_215505_1_) {
            return p_215505_1_ <= this.viewDistance - 2;
        }
    }
}
