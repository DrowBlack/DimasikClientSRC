package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.util.Direction;
import net.minecraft.util.SectionDistanceGraph;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.lighting.LightDataMap;
import net.minecraft.world.lighting.LightEngine;

public abstract class SectionLightStorage<M extends LightDataMap<M>>
extends SectionDistanceGraph {
    protected static final NibbleArray EMPTY_ARRAY = new NibbleArray();
    private static final Direction[] DIRECTIONS = Direction.values();
    private final LightType type;
    private final IChunkLightProvider chunkProvider;
    protected final LongSet activeLightSections = new LongOpenHashSet();
    protected final LongSet addedEmptySections = new LongOpenHashSet();
    protected final LongSet addedActiveLightSections = new LongOpenHashSet();
    protected volatile M uncachedLightData;
    protected final M cachedLightData;
    protected final LongSet dirtyCachedSections = new LongOpenHashSet();
    protected final LongSet changedLightPositions = new LongOpenHashSet();
    protected final Long2ObjectMap<NibbleArray> newArrays = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap());
    private final LongSet field_241536_n_ = new LongOpenHashSet();
    private final LongSet chunksToRetain = new LongOpenHashSet();
    private final LongSet noLightSections = new LongOpenHashSet();
    protected volatile boolean hasSectionsToUpdate;

    protected SectionLightStorage(LightType lightTypeIn, IChunkLightProvider chunkLightProvider, M dataMap) {
        super(3, 16, 256);
        this.type = lightTypeIn;
        this.chunkProvider = chunkLightProvider;
        this.cachedLightData = dataMap;
        this.uncachedLightData = ((LightDataMap)dataMap).copy();
        ((LightDataMap)this.uncachedLightData).disableCaching();
    }

    protected boolean hasSection(long sectionPosIn) {
        return this.getArray(sectionPosIn, true) != null;
    }

    @Nullable
    protected NibbleArray getArray(long sectionPosIn, boolean cached) {
        return this.getArray(cached ? this.cachedLightData : this.uncachedLightData, sectionPosIn);
    }

    @Nullable
    protected NibbleArray getArray(M map, long sectionPosIn) {
        return ((LightDataMap)map).getArray(sectionPosIn);
    }

    @Nullable
    public NibbleArray getArray(long sectionPosIn) {
        NibbleArray nibblearray = (NibbleArray)this.newArrays.get(sectionPosIn);
        return nibblearray != null ? nibblearray : this.getArray(sectionPosIn, false);
    }

    protected abstract int getLightOrDefault(long var1);

    protected int getLight(long worldPos) {
        long i = SectionPos.worldToSection(worldPos);
        NibbleArray nibblearray = this.getArray(i, true);
        return nibblearray.get(SectionPos.mask(BlockPos.unpackX(worldPos)), SectionPos.mask(BlockPos.unpackY(worldPos)), SectionPos.mask(BlockPos.unpackZ(worldPos)));
    }

    protected void setLight(long worldPos, int lightLevel) {
        long i = SectionPos.worldToSection(worldPos);
        if (this.dirtyCachedSections.add(i)) {
            ((LightDataMap)this.cachedLightData).copyArray(i);
        }
        NibbleArray nibblearray = this.getArray(i, true);
        nibblearray.set(SectionPos.mask(BlockPos.unpackX(worldPos)), SectionPos.mask(BlockPos.unpackY(worldPos)), SectionPos.mask(BlockPos.unpackZ(worldPos)), lightLevel);
        for (int j = -1; j <= 1; ++j) {
            for (int k = -1; k <= 1; ++k) {
                for (int l = -1; l <= 1; ++l) {
                    this.changedLightPositions.add(SectionPos.worldToSection(BlockPos.offset(worldPos, k, l, j)));
                }
            }
        }
    }

    @Override
    protected int getLevel(long sectionPosIn) {
        if (sectionPosIn == Long.MAX_VALUE) {
            return 2;
        }
        if (this.activeLightSections.contains(sectionPosIn)) {
            return 0;
        }
        return !this.noLightSections.contains(sectionPosIn) && ((LightDataMap)this.cachedLightData).hasArray(sectionPosIn) ? 1 : 2;
    }

    @Override
    protected int getSourceLevel(long pos) {
        if (this.addedEmptySections.contains(pos)) {
            return 2;
        }
        return !this.activeLightSections.contains(pos) && !this.addedActiveLightSections.contains(pos) ? 2 : 0;
    }

    @Override
    protected void setLevel(long sectionPosIn, int level) {
        int i = this.getLevel(sectionPosIn);
        if (i != 0 && level == 0) {
            this.activeLightSections.add(sectionPosIn);
            this.addedActiveLightSections.remove(sectionPosIn);
        }
        if (i == 0 && level != 0) {
            this.activeLightSections.remove(sectionPosIn);
            this.addedEmptySections.remove(sectionPosIn);
        }
        if (i >= 2 && level != 2) {
            if (this.noLightSections.contains(sectionPosIn)) {
                this.noLightSections.remove(sectionPosIn);
            } else {
                ((LightDataMap)this.cachedLightData).setArray(sectionPosIn, this.getOrCreateArray(sectionPosIn));
                this.dirtyCachedSections.add(sectionPosIn);
                this.addSection(sectionPosIn);
                for (int j = -1; j <= 1; ++j) {
                    for (int k = -1; k <= 1; ++k) {
                        for (int l = -1; l <= 1; ++l) {
                            this.changedLightPositions.add(SectionPos.worldToSection(BlockPos.offset(sectionPosIn, k, l, j)));
                        }
                    }
                }
            }
        }
        if (i != 2 && level >= 2) {
            this.noLightSections.add(sectionPosIn);
        }
        this.hasSectionsToUpdate = !this.noLightSections.isEmpty();
    }

    protected NibbleArray getOrCreateArray(long sectionPosIn) {
        NibbleArray nibblearray = (NibbleArray)this.newArrays.get(sectionPosIn);
        return nibblearray != null ? nibblearray : new NibbleArray();
    }

    protected void cancelSectionUpdates(LightEngine<?, ?> engine, long sectionPosIn) {
        if (engine.func_227467_c_() < 8192) {
            engine.func_227465_a_(p_227469_2_ -> SectionPos.worldToSection(p_227469_2_) == sectionPosIn);
        } else {
            int i = SectionPos.toWorld(SectionPos.extractX(sectionPosIn));
            int j = SectionPos.toWorld(SectionPos.extractY(sectionPosIn));
            int k = SectionPos.toWorld(SectionPos.extractZ(sectionPosIn));
            for (int l = 0; l < 16; ++l) {
                for (int i1 = 0; i1 < 16; ++i1) {
                    for (int j1 = 0; j1 < 16; ++j1) {
                        long k1 = BlockPos.pack(i + l, j + i1, k + j1);
                        engine.cancelUpdate(k1);
                    }
                }
            }
        }
    }

    protected boolean hasSectionsToUpdate() {
        return this.hasSectionsToUpdate;
    }

    protected void updateSections(LightEngine<M, ?> engine, boolean updateSkyLight, boolean updateBlockLight) {
        if (this.hasSectionsToUpdate() || !this.newArrays.isEmpty()) {
            long l;
            Iterator<Long> iterator = this.noLightSections.iterator();
            while (iterator.hasNext()) {
                l = (Long)iterator.next();
                this.cancelSectionUpdates(engine, l);
                NibbleArray nibblearray = (NibbleArray)this.newArrays.remove(l);
                NibbleArray nibblearray1 = ((LightDataMap)this.cachedLightData).removeArray(l);
                if (!this.chunksToRetain.contains(SectionPos.toSectionColumnPos(l))) continue;
                if (nibblearray != null) {
                    this.newArrays.put(l, nibblearray);
                    continue;
                }
                if (nibblearray1 == null) continue;
                this.newArrays.put(l, nibblearray1);
            }
            ((LightDataMap)this.cachedLightData).invalidateCaches();
            iterator = this.noLightSections.iterator();
            while (iterator.hasNext()) {
                l = (Long)iterator.next();
                this.removeSection(l);
            }
            this.noLightSections.clear();
            this.hasSectionsToUpdate = false;
            for (Long2ObjectMap.Entry entry : this.newArrays.long2ObjectEntrySet()) {
                long j = entry.getLongKey();
                if (!this.hasSection(j)) continue;
                NibbleArray nibblearray2 = (NibbleArray)entry.getValue();
                if (((LightDataMap)this.cachedLightData).getArray(j) == nibblearray2) continue;
                this.cancelSectionUpdates(engine, j);
                ((LightDataMap)this.cachedLightData).setArray(j, nibblearray2);
                this.dirtyCachedSections.add(j);
            }
            ((LightDataMap)this.cachedLightData).invalidateCaches();
            if (!updateBlockLight) {
                for (long l2 : this.newArrays.keySet()) {
                    this.func_241538_b_(engine, l2);
                }
            } else {
                for (long l3 : this.field_241536_n_) {
                    this.func_241538_b_(engine, l3);
                }
            }
            this.field_241536_n_.clear();
            Iterator objectiterator = this.newArrays.long2ObjectEntrySet().iterator();
            while (objectiterator.hasNext()) {
                Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)objectiterator.next();
                long j1 = entry.getLongKey();
                if (!this.hasSection(j1)) continue;
                objectiterator.remove();
            }
        }
    }

    private void func_241538_b_(LightEngine<M, ?> p_241538_1_, long p_241538_2_) {
        if (this.hasSection(p_241538_2_)) {
            int i = SectionPos.toWorld(SectionPos.extractX(p_241538_2_));
            int j = SectionPos.toWorld(SectionPos.extractY(p_241538_2_));
            int k = SectionPos.toWorld(SectionPos.extractZ(p_241538_2_));
            for (Direction direction : DIRECTIONS) {
                long l = SectionPos.withOffset(p_241538_2_, direction);
                if (this.newArrays.containsKey(l) || !this.hasSection(l)) continue;
                for (int i1 = 0; i1 < 16; ++i1) {
                    for (int j1 = 0; j1 < 16; ++j1) {
                        long k1;
                        long l1 = switch (direction) {
                            case Direction.DOWN -> {
                                k1 = BlockPos.pack(i + j1, j, k + i1);
                                yield BlockPos.pack(i + j1, j - 1, k + i1);
                            }
                            case Direction.UP -> {
                                k1 = BlockPos.pack(i + j1, j + 16 - 1, k + i1);
                                yield BlockPos.pack(i + j1, j + 16, k + i1);
                            }
                            case Direction.NORTH -> {
                                k1 = BlockPos.pack(i + i1, j + j1, k);
                                yield BlockPos.pack(i + i1, j + j1, k - 1);
                            }
                            case Direction.SOUTH -> {
                                k1 = BlockPos.pack(i + i1, j + j1, k + 16 - 1);
                                yield BlockPos.pack(i + i1, j + j1, k + 16);
                            }
                            case Direction.WEST -> {
                                k1 = BlockPos.pack(i, j + i1, k + j1);
                                yield BlockPos.pack(i - 1, j + i1, k + j1);
                            }
                            default -> {
                                k1 = BlockPos.pack(i + 16 - 1, j + i1, k + j1);
                                yield BlockPos.pack(i + 16, j + i1, k + j1);
                            }
                        };
                        p_241538_1_.scheduleUpdate(k1, l1, p_241538_1_.getEdgeLevel(k1, l1, p_241538_1_.getLevel(k1)), false);
                        p_241538_1_.scheduleUpdate(l1, k1, p_241538_1_.getEdgeLevel(l1, k1, p_241538_1_.getLevel(l1)), false);
                    }
                }
            }
        }
    }

    protected void addSection(long sectionPos) {
    }

    protected void removeSection(long p_215523_1_) {
    }

    protected void setColumnEnabled(long p_215526_1_, boolean p_215526_3_) {
    }

    public void retainChunkData(long sectionColumnPos, boolean retain) {
        if (retain) {
            this.chunksToRetain.add(sectionColumnPos);
        } else {
            this.chunksToRetain.remove(sectionColumnPos);
        }
    }

    protected void setData(long sectionPosIn, @Nullable NibbleArray array, boolean p_215529_4_) {
        if (array != null) {
            this.newArrays.put(sectionPosIn, array);
            if (!p_215529_4_) {
                this.field_241536_n_.add(sectionPosIn);
            }
        } else {
            this.newArrays.remove(sectionPosIn);
        }
    }

    protected void updateSectionStatus(long sectionPosIn, boolean isEmpty) {
        boolean flag = this.activeLightSections.contains(sectionPosIn);
        if (!flag && !isEmpty) {
            this.addedActiveLightSections.add(sectionPosIn);
            this.scheduleUpdate(Long.MAX_VALUE, sectionPosIn, 0, true);
        }
        if (flag && isEmpty) {
            this.addedEmptySections.add(sectionPosIn);
            this.scheduleUpdate(Long.MAX_VALUE, sectionPosIn, 2, false);
        }
    }

    protected void processAllLevelUpdates() {
        if (this.needsUpdate()) {
            this.processUpdates(Integer.MAX_VALUE);
        }
    }

    protected void updateAndNotify() {
        if (!this.dirtyCachedSections.isEmpty()) {
            Object m = ((LightDataMap)this.cachedLightData).copy();
            ((LightDataMap)m).disableCaching();
            this.uncachedLightData = m;
            this.dirtyCachedSections.clear();
        }
        if (!this.changedLightPositions.isEmpty()) {
            LongIterator longiterator = this.changedLightPositions.iterator();
            while (longiterator.hasNext()) {
                long i = longiterator.nextLong();
                this.chunkProvider.markLightChanged(this.type, SectionPos.from(i));
            }
            this.changedLightPositions.clear();
        }
    }
}
