package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Arrays;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.lighting.LightDataMap;
import net.minecraft.world.lighting.LightEngine;
import net.minecraft.world.lighting.NibbleArrayRepeater;
import net.minecraft.world.lighting.SectionLightStorage;

public class SkyLightStorage
extends SectionLightStorage<StorageMap> {
    private static final Direction[] field_215554_k = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    private final LongSet sectionsWithLight = new LongOpenHashSet();
    private final LongSet pendingAdditions = new LongOpenHashSet();
    private final LongSet pendingRemovals = new LongOpenHashSet();
    private final LongSet enabledColumns = new LongOpenHashSet();
    private volatile boolean hasPendingUpdates;

    protected SkyLightStorage(IChunkLightProvider lightProvider) {
        super(LightType.SKY, lightProvider, new StorageMap(new Long2ObjectOpenHashMap<NibbleArray>(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
    }

    @Override
    protected int getLightOrDefault(long worldPos) {
        long i = SectionPos.worldToSection(worldPos);
        int j = SectionPos.extractY(i);
        StorageMap skylightstorage$storagemap = (StorageMap)this.uncachedLightData;
        int k = skylightstorage$storagemap.surfaceSections.get(SectionPos.toSectionColumnPos(i));
        if (k != skylightstorage$storagemap.minY && j < k) {
            NibbleArray nibblearray = this.getArray(skylightstorage$storagemap, i);
            if (nibblearray == null) {
                worldPos = BlockPos.atSectionBottomY(worldPos);
                while (nibblearray == null) {
                    i = SectionPos.withOffset(i, Direction.UP);
                    if (++j >= k) {
                        return 15;
                    }
                    worldPos = BlockPos.offset(worldPos, 0, 16, 0);
                    nibblearray = this.getArray(skylightstorage$storagemap, i);
                }
            }
            return nibblearray.get(SectionPos.mask(BlockPos.unpackX(worldPos)), SectionPos.mask(BlockPos.unpackY(worldPos)), SectionPos.mask(BlockPos.unpackZ(worldPos)));
        }
        return 15;
    }

    @Override
    protected void addSection(long sectionPos) {
        long j;
        int k;
        int i = SectionPos.extractY(sectionPos);
        if (((StorageMap)this.cachedLightData).minY > i) {
            ((StorageMap)this.cachedLightData).minY = i;
            ((StorageMap)this.cachedLightData).surfaceSections.defaultReturnValue(((StorageMap)this.cachedLightData).minY);
        }
        if ((k = ((StorageMap)this.cachedLightData).surfaceSections.get(j = SectionPos.toSectionColumnPos(sectionPos))) < i + 1) {
            ((StorageMap)this.cachedLightData).surfaceSections.put(j, i + 1);
            if (this.enabledColumns.contains(j)) {
                this.scheduleFullUpdate(sectionPos);
                if (k > ((StorageMap)this.cachedLightData).minY) {
                    long l = SectionPos.asLong(SectionPos.extractX(sectionPos), k - 1, SectionPos.extractZ(sectionPos));
                    this.scheduleSurfaceUpdate(l);
                }
                this.updateHasPendingUpdates();
            }
        }
    }

    private void scheduleSurfaceUpdate(long p_223403_1_) {
        this.pendingRemovals.add(p_223403_1_);
        this.pendingAdditions.remove(p_223403_1_);
    }

    private void scheduleFullUpdate(long p_223404_1_) {
        this.pendingAdditions.add(p_223404_1_);
        this.pendingRemovals.remove(p_223404_1_);
    }

    private void updateHasPendingUpdates() {
        this.hasPendingUpdates = !this.pendingAdditions.isEmpty() || !this.pendingRemovals.isEmpty();
    }

    @Override
    protected void removeSection(long p_215523_1_) {
        long i = SectionPos.toSectionColumnPos(p_215523_1_);
        boolean flag = this.enabledColumns.contains(i);
        if (flag) {
            this.scheduleSurfaceUpdate(p_215523_1_);
        }
        int j = SectionPos.extractY(p_215523_1_);
        if (((StorageMap)this.cachedLightData).surfaceSections.get(i) == j + 1) {
            long k = p_215523_1_;
            while (!this.hasSection(k) && this.isAboveBottom(j)) {
                --j;
                k = SectionPos.withOffset(k, Direction.DOWN);
            }
            if (this.hasSection(k)) {
                ((StorageMap)this.cachedLightData).surfaceSections.put(i, j + 1);
                if (flag) {
                    this.scheduleFullUpdate(k);
                }
            } else {
                ((StorageMap)this.cachedLightData).surfaceSections.remove(i);
            }
        }
        if (flag) {
            this.updateHasPendingUpdates();
        }
    }

    @Override
    protected void setColumnEnabled(long p_215526_1_, boolean p_215526_3_) {
        this.processAllLevelUpdates();
        if (p_215526_3_ && this.enabledColumns.add(p_215526_1_)) {
            int i = ((StorageMap)this.cachedLightData).surfaceSections.get(p_215526_1_);
            if (i != ((StorageMap)this.cachedLightData).minY) {
                long j = SectionPos.asLong(SectionPos.extractX(p_215526_1_), i - 1, SectionPos.extractZ(p_215526_1_));
                this.scheduleFullUpdate(j);
                this.updateHasPendingUpdates();
            }
        } else if (!p_215526_3_) {
            this.enabledColumns.remove(p_215526_1_);
        }
    }

    @Override
    protected boolean hasSectionsToUpdate() {
        return super.hasSectionsToUpdate() || this.hasPendingUpdates;
    }

    @Override
    protected NibbleArray getOrCreateArray(long sectionPosIn) {
        NibbleArray nibblearray = (NibbleArray)this.newArrays.get(sectionPosIn);
        if (nibblearray != null) {
            return nibblearray;
        }
        long i = SectionPos.withOffset(sectionPosIn, Direction.UP);
        int j = ((StorageMap)this.cachedLightData).surfaceSections.get(SectionPos.toSectionColumnPos(sectionPosIn));
        if (j != ((StorageMap)this.cachedLightData).minY && SectionPos.extractY(i) < j) {
            NibbleArray nibblearray1;
            while ((nibblearray1 = this.getArray(i, true)) == null) {
                i = SectionPos.withOffset(i, Direction.UP);
            }
            return new NibbleArray(new NibbleArrayRepeater(nibblearray1, 0).getData());
        }
        return new NibbleArray();
    }

    @Override
    protected void updateSections(LightEngine<StorageMap, ?> engine, boolean updateSkyLight, boolean updateBlockLight) {
        super.updateSections(engine, updateSkyLight, updateBlockLight);
        if (updateSkyLight) {
            LongIterator longIterator;
            if (!this.pendingAdditions.isEmpty()) {
                longIterator = this.pendingAdditions.iterator();
                while (longIterator.hasNext()) {
                    long i = (Long)longIterator.next();
                    int j = this.getLevel(i);
                    if (j == 2 || this.pendingRemovals.contains(i) || !this.sectionsWithLight.add(i)) continue;
                    if (j == 1) {
                        this.cancelSectionUpdates(engine, i);
                        if (this.dirtyCachedSections.add(i)) {
                            ((StorageMap)this.cachedLightData).copyArray(i);
                        }
                        Arrays.fill(this.getArray(i, true).getData(), (byte)-1);
                        int i3 = SectionPos.toWorld(SectionPos.extractX(i));
                        int k3 = SectionPos.toWorld(SectionPos.extractY(i));
                        int i4 = SectionPos.toWorld(SectionPos.extractZ(i));
                        for (Direction direction : field_215554_k) {
                            long j1 = SectionPos.withOffset(i, direction);
                            if (!this.pendingRemovals.contains(j1) && (this.sectionsWithLight.contains(j1) || this.pendingAdditions.contains(j1)) || !this.hasSection(j1)) continue;
                            for (int k1 = 0; k1 < 16; ++k1) {
                                for (int l1 = 0; l1 < 16; ++l1) {
                                    long i2;
                                    long j2 = switch (direction) {
                                        case Direction.NORTH -> {
                                            i2 = BlockPos.pack(i3 + k1, k3 + l1, i4);
                                            yield BlockPos.pack(i3 + k1, k3 + l1, i4 - 1);
                                        }
                                        case Direction.SOUTH -> {
                                            i2 = BlockPos.pack(i3 + k1, k3 + l1, i4 + 16 - 1);
                                            yield BlockPos.pack(i3 + k1, k3 + l1, i4 + 16);
                                        }
                                        case Direction.WEST -> {
                                            i2 = BlockPos.pack(i3, k3 + k1, i4 + l1);
                                            yield BlockPos.pack(i3 - 1, k3 + k1, i4 + l1);
                                        }
                                        default -> {
                                            i2 = BlockPos.pack(i3 + 16 - 1, k3 + k1, i4 + l1);
                                            yield BlockPos.pack(i3 + 16, k3 + k1, i4 + l1);
                                        }
                                    };
                                    engine.scheduleUpdate(i2, j2, engine.getEdgeLevel(i2, j2, 0), true);
                                }
                            }
                        }
                        for (int j4 = 0; j4 < 16; ++j4) {
                            for (int k4 = 0; k4 < 16; ++k4) {
                                long l4 = BlockPos.pack(SectionPos.toWorld(SectionPos.extractX(i)) + j4, SectionPos.toWorld(SectionPos.extractY(i)), SectionPos.toWorld(SectionPos.extractZ(i)) + k4);
                                long i5 = BlockPos.pack(SectionPos.toWorld(SectionPos.extractX(i)) + j4, SectionPos.toWorld(SectionPos.extractY(i)) - 1, SectionPos.toWorld(SectionPos.extractZ(i)) + k4);
                                engine.scheduleUpdate(l4, i5, engine.getEdgeLevel(l4, i5, 0), true);
                            }
                        }
                        continue;
                    }
                    for (int k = 0; k < 16; ++k) {
                        for (int l = 0; l < 16; ++l) {
                            long i1 = BlockPos.pack(SectionPos.toWorld(SectionPos.extractX(i)) + k, SectionPos.toWorld(SectionPos.extractY(i)) + 16 - 1, SectionPos.toWorld(SectionPos.extractZ(i)) + l);
                            engine.scheduleUpdate(Long.MAX_VALUE, i1, 0, true);
                        }
                    }
                }
            }
            this.pendingAdditions.clear();
            if (!this.pendingRemovals.isEmpty()) {
                longIterator = this.pendingRemovals.iterator();
                while (longIterator.hasNext()) {
                    long k2 = (Long)longIterator.next();
                    if (!this.sectionsWithLight.remove(k2) || !this.hasSection(k2)) continue;
                    for (int l2 = 0; l2 < 16; ++l2) {
                        for (int j3 = 0; j3 < 16; ++j3) {
                            long l3 = BlockPos.pack(SectionPos.toWorld(SectionPos.extractX(k2)) + l2, SectionPos.toWorld(SectionPos.extractY(k2)) + 16 - 1, SectionPos.toWorld(SectionPos.extractZ(k2)) + j3);
                            engine.scheduleUpdate(Long.MAX_VALUE, l3, 15, false);
                        }
                    }
                }
            }
            this.pendingRemovals.clear();
            this.hasPendingUpdates = false;
        }
    }

    protected boolean isAboveBottom(int p_215550_1_) {
        return p_215550_1_ >= ((StorageMap)this.cachedLightData).minY;
    }

    protected boolean func_215551_l(long p_215551_1_) {
        int i = BlockPos.unpackY(p_215551_1_);
        if ((i & 0xF) != 15) {
            return false;
        }
        long j = SectionPos.worldToSection(p_215551_1_);
        long k = SectionPos.toSectionColumnPos(j);
        if (!this.enabledColumns.contains(k)) {
            return false;
        }
        int l = ((StorageMap)this.cachedLightData).surfaceSections.get(k);
        return SectionPos.toWorld(l) == i + 16;
    }

    protected boolean isAboveWorld(long p_215549_1_) {
        long i = SectionPos.toSectionColumnPos(p_215549_1_);
        int j = ((StorageMap)this.cachedLightData).surfaceSections.get(i);
        return j == ((StorageMap)this.cachedLightData).minY || SectionPos.extractY(p_215549_1_) >= j;
    }

    protected boolean isSectionEnabled(long p_215548_1_) {
        long i = SectionPos.toSectionColumnPos(p_215548_1_);
        return this.enabledColumns.contains(i);
    }

    public static final class StorageMap
    extends LightDataMap<StorageMap> {
        private int minY;
        private final Long2IntOpenHashMap surfaceSections;

        public StorageMap(Long2ObjectOpenHashMap<NibbleArray> p_i50496_1_, Long2IntOpenHashMap p_i50496_2_, int p_i50496_3_) {
            super(p_i50496_1_);
            this.surfaceSections = p_i50496_2_;
            p_i50496_2_.defaultReturnValue(p_i50496_3_);
            this.minY = p_i50496_3_;
        }

        @Override
        public StorageMap copy() {
            return new StorageMap((Long2ObjectOpenHashMap<NibbleArray>)this.arrays.clone(), this.surfaceSections.clone(), this.minY);
        }
    }
}
